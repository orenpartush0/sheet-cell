package shticell.manager.sheet.sheetWithPermission;


import shticell.manager.enums.PermissionStatus;
import shticell.manager.enums.PermissionType;
import java.util.*;
import java.util.stream.Collectors;

public class SheetPermissionDataImpl implements SheetPermissionData {
    private final Map<String, PermissionType> permissions = new HashMap<String, PermissionType>();
    private final Map<Integer, PermissionRequestDto> permissionRequests = new TreeMap<>();

    private final Object lock = new Object();

    @Override
    public void AddPermission(String user, PermissionType permission) {
        permissions.put(user, permission);
    }

    @Override
    public void AddPermissionRequest( String userName, PermissionType permissionType, PermissionStatus permissionStatus) {
        synchronized (lock) {
            int reqId = permissionRequests.keySet().size() + 1;
            permissionRequests.put(reqId , new PermissionRequestDto(reqId, userName, permissionType, permissionStatus));
        }
    }

    @Override
    public PermissionType getPermission(String user) {
        return permissions.getOrDefault(user,PermissionType.DONT_HAVE);
    }

    @Override
    public synchronized void UpdateRequestStatus(int reqId, Boolean accept) {
        if(isPending(reqId)) {
            permissionRequests.computeIfPresent(reqId,
                    (k, val) -> new PermissionRequestDto(reqId, val.user, val.permissionType,
                            accept ? PermissionStatus.APPROVED : PermissionStatus.REJECTED));


            if (permissionRequests.containsKey(reqId) && accept) {
                String userAskPermission = permissionRequests.get(reqId).user;
                AddPermission(userAskPermission, permissionRequests.get(reqId).permissionType);
            }
        }
        else {
            throw new RuntimeException();
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

    public record PermissionRequestDto(int reqId,String user, PermissionType permissionType, PermissionStatus permissionStatus){};

    @Override
    public synchronized boolean isPending(int reqId){
        return permissionRequests.get(reqId).permissionStatus.equals(PermissionStatus.PENDING);
    }
}

