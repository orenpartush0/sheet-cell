package dto;

import shticell.manager.enums.PermissionType;

public record AddRequestDto(int reqId, PermissionType permissionType) {}
