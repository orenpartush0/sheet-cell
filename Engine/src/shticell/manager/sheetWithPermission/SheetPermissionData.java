package shticell.manager.sheetWithPermission;

import shticell.manager.enums.PermissionType;

import java.util.List;

public interface SheetPermissionData {

    void AddPermission(String user, PermissionType permission);

    void AddPermissionRequest(SheetPermissionDataImpl.PermissionRequestDto permissionRequestDto);

    PermissionType getPermission(String user);

    void UpdateRequestStatus (String user, Boolean answer);

    String GetOwner();

    List<SheetPermissionDataImpl.PermissionRequestDto> GetPermissionRequests();
}
