package controller;

import shticell.operation.Exceptions.NumberOperationException;
import shticell.operation.Exceptions.OperationException;
import dto.CellDto;
import shticell.exception.LoopConnectionException;
import shticell.cell.sheet.sheetimpl.SheetImpl;
import dto.SheetDto;

import java.util.List;

public class Controller {
    private final SheetImpl sheetImpl;

    public Controller(SheetDto sheetDto) {
        sheetImpl = new SheetImpl(sheetDto.Name(),sheetDto.numberOfRows(),sheetDto.numberOfColumns());
    }

    public void UpdateCellByIndex(String coordinate, String newValue) throws NumberFormatException, LoopConnectionException, OperationException, NumberOperationException {
        sheetImpl.UpdateCellByCoordinate(coordinate, newValue);
    }

     public SheetDto getSheet(){
        return new SheetDto(sheetImpl);
    }

    public CellDto GetCellByCoordinate(String CellId){
        return new CellDto(sheetImpl.GetCell(CellId));
    }

    public List<Integer> GetCountOfChangesPerVersion(){
        return sheetImpl.GetCountOfChangesPerVersion();
    }

    public SheetDto GetSheetByVersion(int version){
        return new SheetDto(sheetImpl.GetSheetByVersion(version));
    }

}
