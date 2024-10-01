package shticell.manager.sheet;

import dto.CellDto;
import dto.SheetDataDto;
import dto.SheetDto;
import shticell.jaxb.SchemBaseJaxb;
import shticell.manager.enums.PermissionStatus;
import shticell.manager.enums.PermissionType;
import shticell.manager.sheet.sheetWithPermission.SheetPermissionData;
import shticell.manager.sheet.sheetWithPermission.SheetPermissionDataImpl;
import shticell.sheet.api.Sheet;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.impl.SheetImpl;
import shticell.sheet.range.Range;
import shticell.util.Filter;
import shticell.util.Sort;

import java.io.InputStream;
import java.util.*;


public class SheetManager {
    private final Map<String, Sheet> sheets = new LinkedHashMap<>();
    private final Map<String, SheetPermissionData> sheetPermissionDataMap = new HashMap<>();

    public final Object SHEET_LOCK = new Object();

    public void SetSheet(String userName, SheetDto sheetDto) {
        Sheet sheet = new SheetImpl(sheetDto.Name(),sheetDto.numberOfRows(),sheetDto.numberOfColumns(),sheetDto.rowsHeight(),sheetDto.colsWidth());
        synchronized (SHEET_LOCK) {
            if (sheets.containsKey(sheetDto.Name())) {
                throw new RuntimeException("Sheet already exists");
            }
            sheets.put(sheetDto.Name(), sheet);
        }
        sheetPermissionDataMap.put(sheetDto.Name(),new SheetPermissionDataImpl());
        sheetPermissionDataMap.get(sheetDto.Name()).AddPermission(userName,PermissionType.OWNER);
    }

    public void UploadSheetXml(String userName, InputStream in) throws Exception {
        Sheet sheet =  SchemBaseJaxb.CreateSheetFromXML(in);
        String sheetName = sheet.GetSheetName();
        synchronized (SHEET_LOCK) {
            if (sheets.containsKey(sheetName)) {
                throw new RuntimeException("Sheet already exists");
            }
            sheets.put(sheet.GetSheetName(), sheet);
        }
        sheetPermissionDataMap.put(sheetName,new SheetPermissionDataImpl());
        sheetPermissionDataMap.get(sheetName).AddPermission(userName,PermissionType.OWNER);
    }

    public int GetNumOfChanges(String sheetName){
       return sheets.get(sheetName).GetNumOfChanges();
    }

    public void UpdateCellByCoordinate(String sheetName,Coordinate coordinate, String newValue,String userName) throws LoopConnectionException {
        sheets.get(sheetName).UpdateCellByCoordinate(coordinate,newValue,userName);
    }

    public SheetDto getSheet(String sheetName){
        return new SheetDto(sheets.get(sheetName));
    }

    public Sheet GetSheetToFilter(String sheetName){
        return sheets.get(sheetName);
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

    public SheetDto applyFilter(String sheetName,Range range,Map<Integer,List<String>> filters){
        return Filter.getFilteredSheetDto(sheets.get(sheetName),range,filters);
    }

    public SheetDto applyDynamicCalculate(String sheetName,Coordinate coordinate , String numStr){
        Sheet sheet = sheets.get(sheetName);
        sheet.GetSheetReadWriteLock().writeLock().lock();
        try {
            String currentOriginalValue = sheet.GetOriginalValue(coordinate);
            sheet.applyDynamicCalculate(coordinate, numStr);
            SheetDto newSheetDto = new SheetDto(sheet);
            sheet.applyDynamicCalculate(coordinate, currentOriginalValue);


            return newSheetDto;
        }finally {
            sheet.GetSheetReadWriteLock().writeLock().unlock();
        }
    }

    public List<SheetDataDto> GetSheetsDashBoard(String user){
        return sheets.keySet().stream().map(sheetName ->{
            PermissionType permissionType = sheetPermissionDataMap.get(sheetName).getPermission(user);
            String sheetSize = sheets.get(sheetName).GetNumberOfRows() + "X" + sheets.get(sheetName).GetNumberOfColumns();
            String owner = sheetPermissionDataMap.get(sheetName).GetOwner();
            return new SheetDataDto(owner,sheetName,sheetSize,permissionType);
        }).toList();
    }

    public String getSheetOwner(String sheetName){
        return sheetPermissionDataMap.get(sheetName).GetOwner();
    }

    public List<SheetPermissionDataImpl.PermissionRequestDto> GetRequestDashBoard(String sheetName){
        return sheetPermissionDataMap.get(sheetName).GetPermissionRequests();
    }

    public void AddRequestPermission(String sheetName, String userName, PermissionType permissionType){
        sheetPermissionDataMap.get(sheetName).AddPermissionRequest(userName, permissionType, PermissionStatus.PENDING);
    }

    public void UpdateRequestStatus(String sheetName,int reqId, Boolean accept){
        sheetPermissionDataMap.get(sheetName).UpdateRequestStatus(reqId,accept);
    }

    public boolean isReqPending(String sheetName,int reqId){
        return sheetPermissionDataMap.get(sheetName).isPending(reqId);
    }

    public boolean isPermit(String sheetName, String user, PermissionType permissionType){
        return sheetPermissionDataMap.get(sheetName).isPermit(user,permissionType);
    }

    public boolean isRangeInUse(String sheetName,String rangeName){
        return sheets.get(sheetName).IsRangeInUse(rangeName);
    }
}
