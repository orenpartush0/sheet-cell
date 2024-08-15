package shticell.operation;

import shticell.operation.Exceptions.NumberOperationException;
import shticell.operation.Exceptions.OperationException;
import shticell.sheet.exception.LoopConnectionException;

public interface Operation
{
    String eval(String... args) throws LoopConnectionException, OperationException, NumberOperationException;
}