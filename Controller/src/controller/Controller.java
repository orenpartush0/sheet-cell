package controller;

import dto.CellDto;
import shticell.jaxb.SchemBaseJaxb;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.api.Sheet;
import shticell.sheet.impl.SheetImpl;
import dto.SheetDto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class Controller {
    private Sheet sheet; // I removed the finle beacse when load a file the sheet changes

    public Controller(SheetDto sheetDto) {
        sheet = new SheetImpl(sheetDto.Name(),sheetDto.numberOfRows(),sheetDto.numberOfColumns());
    }

    public Controller(String fileName) throws Exception {
        File file = new File(fileName);
        InputStream inputStream = new FileInputStream(file);
        sheet = SchemBaseJaxb.CreateSheetFromXML(inputStream);
    }

    public void createSheetFromFile(String fileName) throws Exception {
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

}
