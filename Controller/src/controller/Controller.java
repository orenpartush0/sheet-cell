package controller;

import operation.Exceptions.NumberOperationException;
import operation.Exceptions.OperationException;
import dto.CellDto;
import sheet.exception.LoopConnectionException;
import sheet.Sheet;
import dto.SheetDto;

import java.util.List;

public class Controller {
    private final Sheet sheet;

    public Controller(SheetDto sheetDto) {
        sheet = new Sheet(sheetDto.Name(),sheetDto.numberOfRows(),sheetDto.numberOfColumns());
    }

    public void UpdateCellByIndex(String square, String newValue) throws NumberFormatException, LoopConnectionException, OperationException, NumberOperationException {
        sheet.UpdateCellByIndex(square, newValue);
    }

     public SheetDto getSheet(){
        return new SheetDto(sheet);
    }

    public CellDto GetCellDataByID(String CellId){
        return sheet.GetCellData(CellId);
    }

    public List<Integer> GetCountOfChangesPerVersion(){
        return sheet.GetCountOfChangesPerVersion();
    }

    public SheetDto GetSheetByVersion(int version){
        return new SheetDto(sheet.GetSheetByVersion(version));
    }

}
