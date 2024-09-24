package shticell.manager.sheet.sheetWithPermission;


import shticell.manager.enums.PermissionStatus;
import shticell.manager.enums.PermissionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SheetPermissionDataImpl implements SheetPermissionData {
    private final Map<String, PermissionType> permissions = new HashMap<String, PermissionType>();
    private final Map<String, PermissionRequestDto> permissionRequests = new HashMap<>();

    @Override
    public void AddPermission(String user, PermissionType permission) {
        permissions.put(user, permission);
    }

    @Override
    public void AddPermissionRequest(PermissionRequestDto permissionRequestDto) {
        permissionRequests.put(permissionRequestDto.user, permissionRequestDto);
    }

    @Override
    public PermissionType getPermission(String user) {
        return permissions.getOrDefault(user,PermissionType.DONT_HAVE);
    }

    @Override
    public void UpdateRequestStatus (String user, Boolean accept){
        permissionRequests.computeIfPresent(user,
                (k, request) -> new PermissionRequestDto(user, request.permissionType,
                        accept ? PermissionStatus.APPROVED : PermissionStatus.REJECTED));

        if(accept){
            AddPermission(user,permissionRequests.get(user).permissionType);
        }
    }

    @Override
    public String GetOwner(){
        return permissions.
                entrySet().
                stream().
                filter(entry-> entry.getValue().equals(PermissionType.OWNER)).
                map(Map.Entry::getKey).findFirst().orElse("");
    }

    @Override
    public List<PermissionRequestDto> GetPermissionRequests(){
        return new ArrayList<>(permissionRequests.values());
    }


    @Override
    public boolean isPermit(String userName, PermissionType permissionType){
        return permissions.getOrDefault(userName,PermissionType.DONT_HAVE).getPermissionLevel() >= permissionType.getPermissionLevel();
    }

    public record PermissionRequestDto(String user, PermissionType permissionType, PermissionStatus permissionStatus){};
}

