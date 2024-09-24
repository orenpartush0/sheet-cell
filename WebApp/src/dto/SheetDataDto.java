package dto;

import shticell.manager.enums.PermissionType;

public record SheetDataDto(String owner, String sheetName, String SheetsSize, PermissionType permissionType) {}
