package shticell.manager;

import dto.CellDto;
import dto.SheetDto;
import shticell.manager.enums.PermissionType;
import shticell.manager.sheetWithPermission.SheetWithPermission;
import shticell.sheet.api.Sheet;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.impl.SheetImpl;
import shticell.sheet.range.Range;
import shticell.util.Sort;

import java.util.*;

public class Manager {
    Set<String> users = new HashSet<>();
    private final Map<String, List<SheetWithPermission>> userToSheetMap = new HashMap<>();
    //private final List<PermissionRequest>

    public void addUser(String user){
        if (!users.add(user)) {
            throw new RuntimeException("User already exists");
        }
    }

    public void SetSheet(String userName, SheetDto sheetDto) {
        Sheet sheet = new SheetImpl(sheetDto.Name(),sheetDto.numberOfRows(),sheetDto.numberOfColumns(),sheetDto.rowsHeight(),sheetDto.colsWidth());
        userToSheetMap.computeIfAbsent(userName,v -> new ArrayList<>()).add(new SheetWithPermission(sheet, PermissionType.OWNER));
    }

    private Sheet getSheetFromMap(String userName,String sheetName){
        return userToSheetMap.
                get(userName).
                stream().
                filter(v->v.getSheet().GetSheetName().equals(sheetName)).
                findFirst().get().getSheet();
    }

    public void UpdateCellByCoordinate(String userName,String sheetName,Coordinate coordinate, String newValue) {
        userToSheetMap.
                get(userName).
                stream().
                filter(v->v.getSheet().GetSheetName().equals(sheetName)).
                findFirst().
                ifPresent(v->
                {try { v.getSheet().UpdateCellByCoordinate(coordinate,newValue); } catch (LoopConnectionException e) {throw new RuntimeException(e);}
        });
    }

    public SheetDto getSheet(String userName,String sheetName){
        Sheet sheet = getSheetFromMap(userName,sheetName);
        return new SheetDto(sheet);
    }

    public CellDto GetCellByCoordinate(String userName,String sheetName,Coordinate coordinate){
        Sheet sheet = getSheetFromMap(userName,sheetName);
        return new CellDto(sheet.GetCell(coordinate));
    }

    public SheetDto GetSheetByVersion(String userName,String sheetName,int version){
        Sheet sheet = getSheetFromMap(userName,sheetName);
        return new SheetDto(sheet.GetSheetByVersion(version));
    }

    public void AddRange(String userName,String sheetName,Range rangeDto){
        Sheet sheet = getSheetFromMap(userName,sheetName);
        sheet.AddRange(rangeDto);
    }

    public Range GetRangeDto(String userName,String sheetName,String rangeName){
        Sheet sheet = getSheetFromMap(userName,sheetName);
        return sheet.GetRangeDto(rangeName);
    }

    public List<Range> getRanges(String userName,String sheetName){
        Sheet sheet = getSheetFromMap(userName,sheetName);
        return sheet.GetRangesDto();
    }

    public void removeRange(String userName,String sheetName,String rangeName) throws Exception{
        Sheet sheet = getSheetFromMap(userName,sheetName);
        sheet.RemoveRange(rangeName);
    }

    public SheetDto applySort(String userName,String sheetName,Queue<String> cols,Range range){
        Sheet sheet = getSheetFromMap(userName,sheetName);
        return Sort.SortRange(sheet,cols,range);
    }

    public SheetDto applyDynamicCalculate(String userName,String sheetName,Coordinate coordinate , String numStr){
        Sheet sheet = getSheetFromMap(userName,sheetName);
        String currentOriginalValue = sheet.GetOriginalValue(coordinate);
        sheet.applyDynamicCalculate(coordinate,numStr);
        SheetDto newSheetDto = new SheetDto(sheet);
        sheet.applyDynamicCalculate(coordinate,currentOriginalValue);

        return newSheetDto;
    }

//    public void addToPending(String ownerName, String userName, String sheetName){
//        Sheet sheet = getSheetFromMap(ownerName,sheetName);
//        userToSheetMap.computeIfAbsent(userName,v->new ArrayList<>()).add(new SheetWithPermission(sheet, PermissionType.);
//    }


}
