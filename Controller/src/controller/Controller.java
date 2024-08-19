package controller;

import dto.CellDto;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.api.Sheet;
import shticell.sheet.impl.SheetImpl;
import dto.SheetDto;

import java.util.List;

public class Controller {
    private final Sheet sheet;

    public Controller(SheetDto sheetDto) {
        sheet = new SheetImpl(sheetDto.Name(),sheetDto.numberOfRows(),sheetDto.numberOfColumns());
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
