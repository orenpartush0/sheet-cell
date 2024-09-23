package shticell.manager.sheetWithPermission;

import shticell.manager.enums.PermissionType;
import shticell.sheet.api.Sheet;

public class SheetWithPermission {
    private final Sheet sheet;
    private PermissionType permission;

    public SheetWithPermission(Sheet sheet, PermissionType permission) {
        this.sheet = sheet;
        this.permission = permission;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public PermissionType getPermission() {
        return permission;
    }

    public void setPermission(PermissionType permission) {
        this.permission = permission;
    }


}

