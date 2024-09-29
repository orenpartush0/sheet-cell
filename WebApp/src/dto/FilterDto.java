package dto;

import shticell.sheet.range.Range;

import java.util.List;
import java.util.Map;

public record FilterDto(Range range , Map<Integer, List<String>> filters) {}
