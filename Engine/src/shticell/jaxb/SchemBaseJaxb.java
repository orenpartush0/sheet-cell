package shticell.jaxb;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import shticell.jaxb.schema.STLCell;
import shticell.jaxb.schema.STLSheet;
import shticell.sheet.api.Sheet;
import shticell.sheet.cell.connection.CellConnection;
import shticell.sheet.cell.connection.CellConnectionImpl;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.exception.CellOutOfSheetException;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.impl.SheetImpl;

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

    private static STLSheet deserializeFrom(InputStream in) throws JAXBException{
        JAXBContext jc = JAXBContext.newInstance(STLSheet.class);
        Unmarshaller u = jc.createUnmarshaller();
        STLSheet unmarshal = (STLSheet) u.unmarshal(in);
        return unmarshal;
        
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
            res = new SheetImpl(sheet.getName(),sheet.getSTLLayout().getRows(),sheet.getSTLLayout().getColumns());
            List<STLCell> creationOrder = getCreationCellsList(sheet.getSTLCells().getSTLCell(),sheet.getSTLLayout().getRows(),sheet.getSTLLayout().getColumns());
            creationOrder.forEach(c-> {
                try {
                    res.UpdateCellByCoordinateWithOutVersionUpdate(CoordinateFactory.getCoordinate(c.getRow(),(int)c.getColumn().charAt(0)-(int)'A'), c.getSTLOriginalValue());
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

    private static List<STLCell> getCreationCellsList(List<STLCell> cellsList,int rows, int columns) throws LoopConnectionException {
        List<CellConnection> connectionList = getCellConnectionList(cellsList,rows,columns);
        List<CellConnection> topoligicalConnectionList = new ArrayList<>();
        Map<CellConnection, STLCell> connectionCellMap = new HashMap<>();
        for (int i = 0; i < cellsList.size(); i++) {
            connectionCellMap.put(connectionList.get(i), cellsList.get(i));
        }

        connectionList.forEach(c -> {
            try {
                if(!topoligicalConnectionList.contains(c)){
                    topoligicalConnectionList.add(c);
                    topoligicalConnectionList.addAll(c.GetSortedInfluenceOn());
                }
            } catch (LoopConnectionException e) {
                throw new RuntimeException(e);
            }
        });
        topoligicalConnectionList.retainAll(connectionList);

        List<STLCell> topoligicalCellsList = new ArrayList<>();
        topoligicalConnectionList.forEach(c -> topoligicalCellsList.add(connectionCellMap.get(c)) );
        return topoligicalCellsList;
    }

    private static List<CellConnection> getCellConnectionList(List<STLCell> cellsList,int rows, int columns) throws RuntimeException {
        List<CellConnection> res = new ArrayList<CellConnection>();
        Map<Coordinate,CellConnection> map = new HashMap<Coordinate,CellConnection>();
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
                            Coordinate coordinate = CoordinateFactory.getCoordinate(cell.getRow(), (int)cell.getColumn().charAt(0)-(int)'A');
                            CellConnectionImpl cellConnection1 = (CellConnectionImpl) map.computeIfAbsent(coordinate, k -> new CellConnectionImpl(coordinate));

                            List<Coordinate> pointTo = null;
                            try {
                                pointTo = getRefList(cell, rows, columns);
                            } catch (CellOutOfSheetException e) {
                                throw new RuntimeException(e);
                            }

                            pointTo.forEach(cord -> {
                                CellConnectionImpl cellConnection2 = (CellConnectionImpl) map.computeIfAbsent(cord, k -> new CellConnectionImpl(cord));
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
            int add = 5+coordinate.toString().length();
            orgVal = orgVal.substring(index+add);
            index = orgVal.indexOf("REF,");
        }

        return res;
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
