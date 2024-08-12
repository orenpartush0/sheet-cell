package Operation.Interface;

import Operation.Exceptions.OperationException;
import sheet.Exception.LoopConnectionException;

public interface Operation
{
    String eval(String... args) throws OperationException, LoopConnectionException;
}