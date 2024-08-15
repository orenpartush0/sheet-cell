package shticell.jaxb;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import shticell.jaxb.schema.STLSheet;

import java.io.InputStream;

public class SchemBaseJaxb {
    private final static String JAXB_XML_GAME_PACKAGE_NAME = "Engine.shticell.jaxb.schema;";

    private static STLSheet deserializeFrom(InputStream in) throws JAXBException{
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        STLSheet unmarshal = (STLSheet) u.unmarshal(in);
        return unmarshal;
        
    }
    
    
}
