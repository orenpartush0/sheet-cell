package shticell.jaxb;

import controller.Controller;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import shticell.jaxb.schema.STLCell;
import shticell.jaxb.schema.STLSheet;
import shticell.sheet.api.Sheet;
import shticell.sheet.impl.SheetImpl;

import java.io.InputStream;

public class SchemBaseJaxb {
    private final static String JAXB_XML_GAME_PACKAGE_NAME = "Engine.shticell.jaxb.schema;";

    public static STLSheet deserializeFrom(InputStream in) throws JAXBException{
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        STLSheet unmarshal = (STLSheet) u.unmarshal(in);
        return unmarshal;
        
    }

//    private static Sheet convertToSheet(STLSheet sheet) {
//        SheetImpl res = new SheetImpl(sheet.getName(),sheet.getSTLLayout().getRows(),sheet.getSTLLayout().getColumns());
//        for( STLCell cell : sheet.getSTLCells().getSTLCell()) {
//
//        }
//
//    }


    //public SheetImpl(String _sheetName, int _numberOfRows, int _numberOfColumns)



}
