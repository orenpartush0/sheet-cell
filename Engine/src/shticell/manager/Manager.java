package shticell.manager;

import dto.CellDto;
import dto.SheetDataDto;
import dto.SheetDto;
import shticell.manager.enums.PermissionStatus;
import shticell.manager.enums.PermissionType;
import shticell.manager.sheetWithPermission.SheetPermissionData;
import shticell.manager.sheetWithPermission.SheetPermissionDataImpl;
import shticell.sheet.api.Sheet;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.impl.SheetImpl;
import shticell.sheet.range.Range;
import shticell.util.Sort;

import java.util.*;

public class Manager {
    Set<String> users = new HashSet<>();
    private final Map<String, Sheet> sheets = new HashMap<>();
    private final Map<String, SheetPermissionData> sheetPermissionDataMap = new HashMap<>();

    public void addUser(String user){
        if (!users.add(user)) {
            throw new RuntimeException("User already exists");
        }
    }

    public void SetSheet(String userName, SheetDto sheetDto) {
        Sheet sheet = new SheetImpl(sheetDto.Name(),sheetDto.numberOfRows(),sheetDto.numberOfColumns(),sheetDto.rowsHeight(),sheetDto.colsWidth());
        if(sheets.containsKey(sheet.GetSheetName())) {
            throw new RuntimeException("Sheet already exists");
        }
        sheets.put(sheetDto.Name(), sheet);
        sheetPermissionDataMap.put(sheetDto.Name(),new SheetPermissionDataImpl());
        sheetPermissionDataMap.get(sheetDto.Name()).AddPermission(userName,PermissionType.OWNER);
    }

    public void UpdateCellByCoordinate(String sheetName,Coordinate coordinate, String newValue) throws LoopConnectionException {
        sheets.get(sheetName).UpdateCellByCoordinate(coordinate,newValue);
    }

    public SheetDto getSheet(String sheetName){
        return new SheetDto(sheets.get(sheetName));
    }

    public CellDto GetCellByCoordinate(String sheetName,Coordinate coordinate){
        return new CellDto(sheets.get(sheetName).GetCell(coordinate));
    }

    public SheetDto GetSheetByVersion(String sheetName,int version){
        return new SheetDto(sheets.get(sheetName).GetSheetByVersion(version));
    }

    public void AddRange(String sheetName,Range rangeDto){
        sheets.get(sheetName).AddRange(rangeDto);
    }

    public Range GetRangeDto(String sheetName,String rangeName){
        return sheets.get(sheetName).GetRangeDto(rangeName);
    }

    public List<Range> GetRanges(String sheetName){
        return sheets.get(sheetName).GetRangesDto();
    }

    public void removeRange(String sheetName, String rangeName) throws Exception{
        sheets.get(sheetName).RemoveRange(rangeName);
    }

    public SheetDto applySort(String sheetName,Queue<String> cols,Range range){
        return Sort.SortRange(sheets.get(sheetName),cols,range);
    }

    public SheetDto applyDynamicCalculate(String sheetName,Coordinate coordinate , String numStr){
        Sheet sheet = sheets.get(sheetName);
        String currentOriginalValue = sheet.GetOriginalValue(coordinate);
        sheet.applyDynamicCalculate(coordinate,numStr);
        SheetDto newSheetDto = new SheetDto(sheet);
        sheet.applyDynamicCalculate(coordinate,currentOriginalValue);

        return newSheetDto;
    }

    public List<SheetDataDto> GetSheetsDashBoard(String user){
        return sheets.keySet().stream().map(sheetName ->{
            PermissionType permissionType = sheetPermissionDataMap.get(sheetName).getPermission(user);
            String sheetSize = sheets.get(sheetName).GetNumberOfRows() + "X" + sheets.get(sheetName).GetNumberOfColumns();
            String owner = sheetPermissionDataMap.get(sheetName).GetOwner();
            return new SheetDataDto(owner,sheetName,sheetSize,permissionType);
        }).toList();
    }

    public List<SheetPermissionDataImpl.PermissionRequestDto> GetRequestDashBoard(String sheetName){
        return sheetPermissionDataMap.get(sheetName).GetPermissionRequests();
    }

    public void AddRequestPermission(String sheetName, String userName, PermissionType permissionType){
        sheetPermissionDataMap.get(sheetName).AddPermissionRequest(new SheetPermissionDataImpl.PermissionRequestDto(userName, permissionType, PermissionStatus.PENDING));
    }

    public void UpdateRequestStatus(String sheetName,String user, Boolean accept){
        sheetPermissionDataMap.get(sheetName).UpdateRequestStatus(user,accept);
    }
}
