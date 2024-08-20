package shticell.jaxb;

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
import shticell.sheet.impl.SheetImpl;

import java.io.InputStream;
import java.util.*;

public class SchemBaseJaxb {
    private final static String JAXB_XML_GAME_PACKAGE_NAME = "Engine.shticell.jaxb.schema;";

    public static STLSheet deserializeFrom(InputStream in) throws JAXBException{
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        STLSheet unmarshal = (STLSheet) u.unmarshal(in);
        return unmarshal;
        
    }

    private static Sheet convertToSheet(STLSheet sheet) {
        SheetImpl res = new SheetImpl(sheet.getName(),sheet.getSTLLayout().getRows(),sheet.getSTLLayout().getColumns());
        for( STLCell cell : sheet.getSTLCells().getSTLCell()) {

        }
    }
    private static List<CellConnection> getCellConnectionList(List<STLCell> cellsList,int rows, int columns) throws CellOutOfSheetException, InvalidCellsSel {
        List<CellConnection> res = new ArrayList<CellConnection>();
        Map<Coordinate,CellConnection> map = new HashMap<Coordinate,CellConnection>();
        Coordinate coordinate;
        CellConnection cellConnection1;
        CellConnection cellConnection2;
        for( STLCell cell : cellsList) {
            coordinate = CoordinateFactory.getCoordinate(cell.getRow(),Integer.parseInt(cell.getColumn()));
            cellConnection1 = Optional.ofNullable(map.get(coordinate)).orElse(new CellConnectionImpl(coordinate));
            List<Coordinate> pointTo = getRefList(cell,rows,columns);
            for(Coordinate cord : pointTo) {
                cellConnection2 = Optional.ofNullable(map.get(coordinate)).orElse(new CellConnectionImpl(cord));
                cellConnection1.AddToDependsOn(cellConnection2);
                cellConnection2.AddToInfluenceOn(cellConnection1);
                map.put(coordinate,cellConnection2);
            }
            map.put(coordinate,cellConnection1);
            res.add(cellConnection1);
        }
        if (map.keySet().size() != res.size()) {
            throw new InvalidCellsSel();
        }
        return res;
    }


    private static void checkCoordinateInSheet(int rows, int columns, int row, int column)throws CellOutOfSheetException {
        if(row < 0 || column < 0 || row >= rows || column >= columns) {
            throw new CellOutOfSheetException(row,column);
        }
    }

    private static List<Coordinate> getRefList(STLCell cell,int rows, int columns) throws CellOutOfSheetException {
        String orgVal = cell.getSTLOriginalValue();
        List<Coordinate> res = new ArrayList<>();
        int index = orgVal.indexOf("REF");
        String cord;
        Coordinate coordinate;
        while(index!= -1 && !orgVal.isEmpty()) {
            cord = orgVal.substring(index+3);
            coordinate = coordinateFromString(cord,rows,columns);
            res.add(coordinate);
            int add = 3+coordinate.toString().length();
            orgVal = orgVal.substring(0+add);
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
