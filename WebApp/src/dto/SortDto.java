package dto;

import shticell.sheet.range.Range;
import java.util.Queue;

public record SortDto(Range range, Queue<String> cols) {}
