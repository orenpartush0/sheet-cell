package shticell.operation.Interface;

import shticell.operation.Exceptions.NumberOperationException;
import shticell.operation.Exceptions.OperationException;
import shticell.exception.LoopConnectionException;

public interface Operation
{
    String eval(String... args) throws LoopConnectionException, OperationException, NumberOperationException;
}