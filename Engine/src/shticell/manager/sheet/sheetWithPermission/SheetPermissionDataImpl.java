package shticell.manager.sheet.sheetWithPermission;


import shticell.manager.enums.PermissionStatus;
import shticell.manager.enums.PermissionType;

import java.util.*;
import java.util.stream.Collectors;

public class SheetPermissionDataImpl implements SheetPermissionData {
    private final Map<String, PermissionType> permissions = new HashMap<String, PermissionType>();
    private final Map<Integer, PermissionRequestDto> permissionRequests = new TreeMap<>();

    @Override
    public void AddPermission(String user, PermissionType permission) {
        permissions.put(user, permission);
    }

    @Override
    public void AddPermissionRequest(PermissionRequestDto permissionRequestDto) {
        permissionRequests.put(permissionRequests.keySet().size() , permissionRequestDto);
    }

    @Override
    public PermissionType getPermission(String user) {
        return permissions.getOrDefault(user,PermissionType.DONT_HAVE);
    }

    @Override
    public void UpdateRequestStatus (String user,int reqId, Boolean accept){
        permissionRequests.computeIfPresent(reqId,
                (k, request) -> new PermissionRequestDto(user, request.permissionType,
                        accept ? PermissionStatus.APPROVED : PermissionStatus.REJECTED));

        if(permissionRequests.containsKey(reqId) && accept && permissionRequests.get(reqId).user.equals(user)){
            AddPermission(user,permissionRequests.get(user).permissionType);
        }
    }

    @Override
    public String GetOwner(){
        return permissions.
                entrySet().
                stream().
                filter(entry-> entry.getValue().equals(PermissionType.OWNER)).
                map(Map.Entry::getKey).
                findFirst().
                orElse("");
    }

    @Override
    public List<PermissionRequestDto> GetPermissionRequests(){
        return permissionRequests.
                entrySet().
                stream().
                sorted(Map.Entry.comparingByKey()).
                map(Map.Entry::getValue).
                collect(Collectors.toList());
    }


    @Override
    public boolean isPermit(String userName, PermissionType permissionType){
        return permissions.getOrDefault(userName,PermissionType.DONT_HAVE).getPermissionLevel() >= permissionType.getPermissionLevel();
    }

    public record PermissionRequestDto(String user, PermissionType permissionType, PermissionStatus permissionStatus){};
}

