package com.chupaniko.exceptions;

public class InvalidHttpMethodException extends RuntimeException {
    public InvalidHttpMethodException() {
        super("Неверно указан метод HTTP-запроса");
    }
}
