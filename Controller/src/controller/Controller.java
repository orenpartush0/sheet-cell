package controller;

import dto.CellDto;
import shticell.jaxb.SchemBaseJaxb;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.api.Sheet;
import shticell.sheet.impl.SheetImpl;
import dto.SheetDto;

import java.io.*;
import java.util.List;

public class Controller {
    private Sheet sheet;

    public Controller(){}

    public Controller(SheetDto sheetDto) {
        sheet = new SheetImpl(sheetDto.Name(),sheetDto.numberOfRows(),sheetDto.numberOfColumns(),sheetDto.rowsHeight(),sheetDto.colsWidth());
    }

    public Controller(String fileName) throws Exception {
        File file = new File(fileName);
        InputStream inputStream = new FileInputStream(file);
        sheet = SchemBaseJaxb.CreateSheetFromXML(inputStream);
    }

    public void GetSheetFromXML(String fileName) throws Exception {
        File file = new File(fileName);
        InputStream inputStream = new FileInputStream(file);
        sheet = SchemBaseJaxb.CreateSheetFromXML(inputStream);
    }


    public void UpdateCellByIndex(String square, String newValue) throws NumberFormatException, LoopConnectionException{
        sheet.UpdateCellByCoordinate(CoordinateFactory.getCoordinate(square), newValue);
    }

    public SheetDto getSheet(){
        return new SheetDto(sheet);
    }

    public CellDto GetCellByCoordinate(String square){
        return new CellDto(sheet.GetCell(CoordinateFactory.getCoordinate(square)));
    }

    public List<Integer> GetCountOfChangesPerVersion(){
        return sheet.GetCountOfChangesPerVersion();
    }

    public SheetDto GetSheetByVersion(int version){
        return new SheetDto(sheet.GetSheetByVersion(version));
    }

    public void InsertSheetToBinaryFile(String filePath) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(filePath);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(sheet);
        out.close();
    }

    public void GetSheetFromBinaryFile(String filePath) throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(filePath);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        sheet = (Sheet) in.readObject();
    }

}
