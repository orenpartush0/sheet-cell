package connector;

import dto.CellDto;
import shticell.jaxb.SchemBaseJaxb;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.api.Sheet;
import shticell.sheet.impl.SheetImpl;
import dto.SheetDto;
import shticell.sheet.range.Range;
import shticell.sheet.util.Filter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Connector {
    private Sheet sheet;
    private Filter filter;

    public void SetSheet(SheetDto sheetDto) {
        sheet = new SheetImpl(sheetDto.Name(),sheetDto.numberOfRows(),sheetDto.numberOfColumns(),sheetDto.rowsHeight(),sheetDto.colsWidth());
    }

    public void SetSheet(String path) throws Exception {
        sheet = path.endsWith(".xml")
                ? GetSheetFromXML(path)
                : GetSheetFromBinaryFile(path);
    }

    public List<String> createNewFilter(Coordinate start,Coordinate end, String col) {
        this.filter = new Filter(col,start,end);
        return filter.getValuesInColumn();
    }

    public List<String> setFilterCol(String col) {
        this.filter.Setcol(col);
        return filter.getValuesInColumn();
    }

    public List<CellDto> applyFilter(String ... values){
        filter.AddFilterValues(values);
        List<Coordinate> filterdCordinates = filter.getFilterdCordinates();
        return filterdCordinates.stream()
                .map(coordinate -> new CellDto(sheet.GetCell(coordinate)))
                .collect(Collectors.toList());
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

    private Sheet GetSheetFromBinaryFile(String filePath) throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(filePath);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        return (Sheet) in.readObject();
    }

    public void AddRange(Range rangeDto){
        sheet.AddRange(rangeDto);
    }

    public Range GetRangeDto(String rangeName){
        return sheet.GetRangeDto(rangeName);
    }

    public Range GetRange(String rangeName) {
        return sheet.GetRangeDto(rangeName);
    }

    public List<Range> getRanges(){
        return sheet.GetRangesDto();
    }

}
