package shticell.manager.enums;

public enum PermissionType {
    DONT_HAVE(1),
    READER(2),
    WRITER(3),
    OWNER(4);

    private final int permissionLevel;

    PermissionType(int _permissionLevel){
        this.permissionLevel = _permissionLevel;
    }

    public int getPermissionLevel(){
        return this.permissionLevel;
    }
}
