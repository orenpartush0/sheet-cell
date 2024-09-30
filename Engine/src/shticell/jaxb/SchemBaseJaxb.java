package shticell.jaxb;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import shticell.jaxb.schema.STLCell;
import shticell.jaxb.schema.STLRange;
import shticell.jaxb.schema.STLSheet;
import shticell.sheet.api.Sheet;
import shticell.sheet.cell.connection.CanRemoveFromDependsOn;
import shticell.sheet.cell.connection.CellConnectionImpl;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.exception.CellOutOfSheetException;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.impl.SheetImpl;
import shticell.sheet.range.Range;
import java.io.InputStream;
import java.util.*;

public class SchemBaseJaxb {
    private final static String JAXB_XML_GAME_PACKAGE_NAME = "shticell/jaxb/schema";
    private static final int MAXROWS = 50;
    private static final int MAXCOLS = 20;

    public static Sheet CreateSheetFromXML(InputStream in) throws Exception {
        STLSheet stlSheet = null;
        stlSheet = deserializeFrom (in);

        return convertToSheet(stlSheet);
    }

    private static STLSheet deserializeFrom(InputStream in) throws JAXBException {
        try{
            JAXBContext jc = JAXBContext.newInstance(STLSheet.class);
            Unmarshaller u = jc.createUnmarshaller();
            STLSheet unmarshal = (STLSheet) u.unmarshal(in);
            return unmarshal;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            throw e;
        }
    }

    private static void checkRowsAndCols(int rows, int cols) {
        if (rows > MAXROWS || cols > MAXCOLS) {
            throw new RuntimeException("rows must be smaller than " + MAXROWS + " and cols " + MAXCOLS);
        }
    }

    private static Sheet convertToSheet(STLSheet sheet) throws Exception {
        SheetImpl res;
        try {
            checkRowsAndCols(sheet.getSTLLayout().getRows(),sheet.getSTLLayout().getColumns());
            res = new SheetImpl(sheet.getName(),sheet.getSTLLayout().getRows(),sheet.getSTLLayout().getColumns(),
                    sheet.getSTLLayout().getSTLSize().getRowsHeightUnits(),sheet.getSTLLayout().getSTLSize().getColumnWidthUnits());

            createRanges(sheet,sheet.getSTLLayout().getRows(),sheet.getSTLLayout().getColumns(),res);

            List<STLCell> creationOrder = getCreationCellsList(sheet.getSTLCells().getSTLCell(),sheet.getSTLLayout().getRows(),sheet.getSTLLayout().getColumns(),res);
            creationOrder.forEach(cell-> {
                try {
                    res.UpdateCellByCoordinateWithOutVersionUpdate(CoordinateFactory.getCoordinate(cell.getRow(),(int)cell.getColumn().charAt(0)-(int)'A' + 1), cell.getSTLOriginalValue());
                } catch (LoopConnectionException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        catch (Exception e) {
            throw new Exception(e);
        }
        return res;
    }
    private static void createRanges (STLSheet sheet,int rows,int cols,Sheet resSheet) {
        List <STLRange> ranges = new ArrayList<STLRange>(sheet.getSTLRanges().getSTLRange());
        Set <String> rangesName = new HashSet<>();
        ranges.forEach(r->{
            if(rangesName.contains(r.getName())) {
                throw new RuntimeException("Duplicate range " + r.getName());
            }
            rangesName.add(r.getName());
            try {
                checkRange(r.getSTLBoundaries().getFrom(),r.getSTLBoundaries().getTo(),rows,cols);
            } catch (CellOutOfSheetException e) {
                throw new RuntimeException(e.getMessage() + "boundary of range " + r.getName() + "is out of sheet");
            }
            resSheet.AddRange(new Range(r.getName(),CoordinateFactory.getCoordinate(r.getSTLBoundaries().getFrom())
                    ,CoordinateFactory.getCoordinate(r.getSTLBoundaries().getTo())));

        });

    }

    private static void checkRange(String from, String to,int rows,int cols) throws CellOutOfSheetException {
        Coordinate fromCord = CoordinateFactory.getCoordinate(from);
        Coordinate toCord = CoordinateFactory.getCoordinate(to);
        checkCoordinateInSheet(rows,cols,fromCord.row(),fromCord.col());
        checkCoordinateInSheet(rows,cols,toCord.row(),toCord.col());
    }

    private static List<STLCell> getCreationCellsList(List<STLCell> cellsList,int rows, int columns, Sheet resSheet) throws LoopConnectionException {
        List<CanRemoveFromDependsOn> connectionList = getCellConnectionList(cellsList,rows,columns,resSheet);
        List<CanRemoveFromDependsOn> topoligicalConnectionList;
        Map<CanRemoveFromDependsOn, STLCell> connectionCellMap = new HashMap<>();
        for (int i = 0; i < cellsList.size(); i++) {
            connectionCellMap.put(connectionList.get(i), cellsList.get(i));
        }

        topoligicalConnectionList = topoligicalConnectionList(connectionList);
        if (topoligicalConnectionList == null) {
            throw new LoopConnectionException("The sheet in the file contain a loop");
        }
        topoligicalConnectionList.retainAll(connectionList);

        List<STLCell> topoligicalCellsList = new ArrayList<>();
        topoligicalConnectionList.forEach(c -> topoligicalCellsList.add(connectionCellMap.get(c)) );
        return topoligicalCellsList;
    }

    private static List<CanRemoveFromDependsOn> getCellConnectionList(List<STLCell> cellsList,int rows, int columns, Sheet resSheet) throws RuntimeException {
        List<CanRemoveFromDependsOn> res = new ArrayList<CanRemoveFromDependsOn>();
        Map<Coordinate,CanRemoveFromDependsOn> map = new HashMap<Coordinate,CanRemoveFromDependsOn>();
        res.addAll(
                cellsList.stream()
                        .peek(cell -> {
                            try {
                                checkCoordinateInSheet(rows, columns, cell.getRow(), (int)cell.getColumn().charAt(0)-(int)'A');
                            } catch (CellOutOfSheetException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .map(cell -> {
                            Coordinate coordinate = CoordinateFactory.getCoordinate(cell.getColumn()+cell.getRow());
                            CanRemoveFromDependsOn cellConnection1 =  map.computeIfAbsent(coordinate,k-> new CellConnectionImpl(coordinate));

                            List<Coordinate> pointTo = null;
                            try {
                                pointTo = getRefList(cell, rows, columns);
                                List<Coordinate> rangeDepndecies = getRangeInCellList(cell, rows, columns, resSheet);
                                if (rangeDepndecies != null) {
                                    pointTo.addAll(rangeDepndecies);
                                }
                            } catch (CellOutOfSheetException e) {
                                throw new RuntimeException(e);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }

                            pointTo.forEach(cord -> {
                                CanRemoveFromDependsOn cellConnection2 = map.computeIfAbsent(cord,k-> new CellConnectionImpl(cord));
                                cellConnection1.AddToDependsOn(cellConnection2);
                                cellConnection2.AddToInfluenceOn(cellConnection1);
                                map.put(coordinate, cellConnection2);
                            });

                            map.put(coordinate, cellConnection1);
                            return cellConnection1;
                        })
                        .toList());
        return res;
    }

    private static void checkCoordinateInSheet(int rows, int columns, int row, int column)throws CellOutOfSheetException {
        if(row < 0 || column < 0 || row > rows || column > columns) {
            throw new CellOutOfSheetException(row,column);
        }
    }

    private static List<Coordinate> getRefList(STLCell cell,int rows, int columns) throws CellOutOfSheetException {
        String orgVal = cell.getSTLOriginalValue().replace(" ","");
        List<Coordinate> res = new ArrayList<>();
        int index = orgVal.indexOf("REF,");
        String cord;
        Coordinate coordinate;
        while(index!= -1 && !orgVal.isEmpty()) {
            cord = orgVal.substring(index+4);
            coordinate = coordinateFromString(cord,rows,columns);
            res.add(coordinate);
            int add = 5 + coordinate.toString().length();
            orgVal = orgVal.substring(index+add);
            index = orgVal.indexOf("REF,");
        }

        return res;
    }

    private static List<Coordinate> getRangeInCellList(STLCell cell,int rows, int columns, Sheet resSheet) throws Exception {
        if(cell.getSTLOriginalValue().toUpperCase().contains("SUM") ||cell.getSTLOriginalValue().toUpperCase().contains("AVERAGE") ){
            Range range = resSheet.GetRangeDto(getRangeFromOrgValue(cell));
            if(range == null){
                throw new Exception("Range "+getRangeFromOrgValue(cell)+ " does not defined");
            }
            return range.getRangeCellsCoordinate();
        }
        return null;
    }

    private static String getRangeFromOrgValue(STLCell cell){
         return cell.getSTLOriginalValue().split(",")[1].replace("}","");
    }

    private static List<CanRemoveFromDependsOn> topoligicalConnectionList(List<CanRemoveFromDependsOn> cellsList){
        List<CanRemoveFromDependsOn> res = new ArrayList<>();
        List<CanRemoveFromDependsOn> noDependedOn = new ArrayList<>(cellsList.stream().filter(c -> c.GetDependsOn().isEmpty()).toList());
        while(!noDependedOn.isEmpty()) {
            CanRemoveFromDependsOn cell = noDependedOn.getFirst();
            res.add(cell);
            List<CanRemoveFromDependsOn> influenceOn =cell.GetInfluenceOn().stream().map(c->(CanRemoveFromDependsOn)c).toList();
            CanRemoveFromDependsOn finalCell = cell;
            influenceOn.forEach(c->{
                c.RemoveFromDependsOn(finalCell);
                if(c.GetDependsOn().isEmpty()){
                    noDependedOn.add(c);
                }
            });
            noDependedOn.remove(cell);
        }
        return res.size() == cellsList.size() ? res : null;
    }

    private static Coordinate coordinateFromString (String orgVal, int rows, int columns) throws CellOutOfSheetException {
        int col = Integer.parseInt(String.valueOf(orgVal.charAt(0)-'A'));
        String tempCol="";
        for( int i = 1; i< orgVal.length(); i++) {
            char c = orgVal.charAt(i);
            if(Character.isDigit(c)) {
                tempCol = tempCol+String.valueOf(orgVal.charAt(i));
            }
            else{
                break;
            }
        }
        int row = Integer.parseInt(tempCol);
        checkCoordinateInSheet(rows,columns,row,col);

        return CoordinateFactory.getCoordinate(row,col);
    }
}
