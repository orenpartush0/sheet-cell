package shticell.function;

import shticell.expression.Enum.Operation;
import shticell.sheet.cell.value.EffectiveValue;

import java.util.List;

public record OperationAndArguments(Operation operation, List<EffectiveValue> arguments){}
