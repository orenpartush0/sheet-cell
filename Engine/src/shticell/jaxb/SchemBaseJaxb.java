package shticell.jaxb;

import com.sun.codemodel.JForEach;
import controller.Controller;
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
import shticell.sheet.exception.InvalidCellsSel;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.impl.SheetImpl;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class SchemBaseJaxb {
    private final static String JAXB_XML_GAME_PACKAGE_NAME = "Engine.shticell.jaxb.schema;";

    public static Sheet CreateSheetFromXML(InputStream in){
        STLSheet stlSheet = null;
        try {
            stlSheet = deserializeFrom (in);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return convertToSheet(stlSheet);
    }

    private static STLSheet deserializeFrom(InputStream in) throws JAXBException{
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        STLSheet unmarshal = (STLSheet) u.unmarshal(in);
        return unmarshal;
        
    }

    private static Sheet convertToSheet(STLSheet sheet) {
        SheetImpl res = new SheetImpl(sheet.getName(),sheet.getSTLLayout().getRows(),sheet.getSTLLayout().getColumns());
        try {
            List<STLCell> creationOrder = getCreationCellsList(sheet.getSTLCells().getSTLCell(),sheet.getSTLLayout().getRows(),sheet.getSTLLayout().getColumns());
            creationOrder.forEach(c-> {
                try {
                    res.UpdateCellByCoordinate(CoordinateFactory.getCoordinate(c.getRow(),Integer.parseInt(c.getColumn())),c.getSTLOriginalValue());
                } catch (LoopConnectionException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
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
                                checkCoordinateInSheet(rows, columns, cell.getRow(), Integer.parseInt(cell.getColumn()));
                            } catch (CellOutOfSheetException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .map(cell -> {
                            Coordinate coordinate = CoordinateFactory.getCoordinate(cell.getRow(), Integer.parseInt(cell.getColumn()));
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
        if(row < 0 || column < 0 || row >= rows || column >= columns) {
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
            int add = 4+coordinate.toString().length();
            orgVal = orgVal.substring(4+add);
            index = orgVal.indexOf("REF,");
        }
        return res;
    }

    private static Coordinate coordinateFromString (String orgVal, int rows, int columns) throws CellOutOfSheetException {
        int row = Integer.parseInt(String.valueOf(orgVal.charAt(0)-'A'));
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
        int col = Integer.parseInt(tempCol);
        checkCoordinateInSheet(rows,columns,row,col);
        return CoordinateFactory.getCoordinate(row,col);
    }





    //public SheetImpl(String _sheetName, int _numberOfRows, int _numberOfColumns)



}
