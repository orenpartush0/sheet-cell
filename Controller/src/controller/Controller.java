package controller;

import Operation.Exceptions.OperationException;
import sheet.Exception.LoopConnectionException;
import sheet.Sheet;
import dto.SheetDto;

public class Controller {
    private final Sheet sheet;

    public Controller(SheetDto sheetDto) {
        sheet = new Sheet(sheetDto.sheetName,sheetDto.numberOfRows,sheetDto.numberOfColumns);
    }

    public void UpdateCellByIndex(String square, String newValue) throws OperationException, LoopConnectionException {
        sheet.UpdateCellByIndex(square, newValue);
    }

    public SheetDto getSheet(){
        return new SheetDto(sheet,sheet.getColSize());
    }

    public String GetCellDataByID(String CellId){
        return sheet.GetCellData(CellId);
    }

}
