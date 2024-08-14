package operation.Interface;

import operation.Exceptions.NumberOperationException;
import operation.Exceptions.OperationException;
import sheet.exception.LoopConnectionException;

public interface Operation
{
    String eval(String... args) throws LoopConnectionException, OperationException, NumberOperationException;
}