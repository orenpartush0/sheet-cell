package Interfaces;

import Operation.Exceptions.OperationException;
import sheet.Exceptions.LoopConnectionException;

public interface Operation
{
    String eval(String... args) throws OperationException, LoopConnectionException;
}