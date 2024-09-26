package shticell.manager.sheet.sheetWithPermission;

import shticell.manager.enums.PermissionType;

import java.util.List;

public interface SheetPermissionData {

    void AddPermission(String user, PermissionType permission);

    void AddPermissionRequest(SheetPermissionDataImpl.PermissionRequestDto permissionRequestDto);

    PermissionType getPermission(String user);

    void UpdateRequestStatus(int reqId, Boolean accept);

    String GetOwner();

    List<SheetPermissionDataImpl.PermissionRequestDto> GetPermissionRequests();

    boolean isPermit(String userName, PermissionType permissionType);

    boolean isPending(int reqId);
}
