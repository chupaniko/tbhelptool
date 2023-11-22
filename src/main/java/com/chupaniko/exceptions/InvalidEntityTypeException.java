package com.chupaniko.exceptions;

public class InvalidEntityTypeException extends Exception {
    public InvalidEntityTypeException() {
        super("Неверно указан тип сущности Thingsboard!");
    }
}
