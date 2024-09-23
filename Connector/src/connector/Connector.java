package connector;

import dto.CellDto;
import shticell.jaxb.SchemBaseJaxb;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.api.Sheet;
import shticell.sheet.impl.SheetImpl;
import dto.SheetDto;
import shticell.sheet.range.Range;
import shticell.util.Filter;
import shticell.util.Sort;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Connector {
    private Sheet sheet;

    public void SetSheet(SheetDto sheetDto) {
        sheet = new SheetImpl(sheetDto.Name(),sheetDto.numberOfRows(),sheetDto.numberOfColumns(),sheetDto.rowsHeight(),sheetDto.colsWidth());
    }

    public void SetSheet(String path) throws Exception {
        sheet = GetSheetFromXML(path);
    }

    public Map<Integer, List<String>> getValuesInColumn(Range range) {
        return Filter.getValuesInColumn(sheet,range);
    }

    public SheetDto applyFilter(Range range , Map<Integer, List<String>> filters) {
        return Filter.getFilteredSheetDto(sheet,range,filters);
    }

    private Sheet GetSheetFromXML(String fileName) throws Exception {
        File file = new File(fileName);
        InputStream inputStream = new FileInputStream(file);
        return SchemBaseJaxb.CreateSheetFromXML(inputStream);
    }

    public void UpdateCellByCoordinate(Coordinate coordinate, String newValue) throws NumberFormatException, LoopConnectionException{
        sheet.UpdateCellByCoordinate(coordinate, newValue);
    }

    public SheetDto getSheet(){
        return new SheetDto(sheet);
    }

    public CellDto GetCellByCoordinate(Coordinate coordinate){
        return new CellDto(sheet.GetCell(coordinate));
    }

    public SheetDto GetSheetByVersion(int version){
        return new SheetDto(sheet.GetSheetByVersion(version));
    }

    public void AddRange(Range rangeDto){
        sheet.AddRange(rangeDto);
    }

    public Range GetRangeDto(String rangeName){
        return sheet.GetRangeDto(rangeName);
    }

    public List<Range> getRanges(){
        return sheet.GetRangesDto();
    }

    public void removeRange(String rangeName) throws Exception{
        sheet.RemoveRange(rangeName);
    }

    public SheetDto applySort(Queue<String> cols,Range range){
        return Sort.SortRange(sheet,cols,range);
    }

    public SheetDto applyDynamicCalculate(Coordinate coordinate , String numStr){
        String currentOriginalValue = sheet.GetOriginalValue(coordinate);
        sheet.applyDynamicCalculate(coordinate,numStr);
        SheetDto newSheetDto = new SheetDto(sheet);
        sheet.applyDynamicCalculate(coordinate,currentOriginalValue);

        return newSheetDto;
    }
}
