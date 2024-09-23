package dto;

import shticell.sheet.range.Range;

public record PutRangeDto(Range range, AuthDto authDto) { }
