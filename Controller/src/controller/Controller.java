package controller;

import shticell.operation.Exceptions.NumberOperationException;
import shticell.operation.Exceptions.OperationException;
import dto.CellDto;
import shticell.exception.LoopConnectionException;
import shticell.sheet.api.Sheet;
import shticell.sheet.impl.SheetImpl;
import dto.SheetDto;

import java.util.List;

public class Controller {
    private final Sheet sheet;

    public Controller(SheetDto sheetDto) {
        sheet = new SheetImpl(sheetDto.Name(),sheetDto.numberOfRows(),sheetDto.numberOfColumns());
    }

    public void UpdateCellByIndex(String coordinate, String newValue) throws NumberFormatException, LoopConnectionException, OperationException, NumberOperationException {
        sheet.UpdateCellByCoordinate(coordinate, newValue);
    }

     public SheetDto getSheet(){
        return new SheetDto(sheet);
    }

    public CellDto GetCellByCoordinate(String CellId){
        return new CellDto(sheet.GetCell(CellId));
    }

    public List<Integer> GetCountOfChangesPerVersion(){
        return sheet.GetCountOfChangesPerVersion();
    }

    public SheetDto GetSheetByVersion(int version){
        return new SheetDto(sheet.GetSheetByVersion(version));
    }

}
