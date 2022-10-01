package ru.practicum.shareit.exc;

public class InvalidParamException extends ValidationException {
    public InvalidParamException(String message, String className) {
        super(message, className);
    }
}
