package ru.practicum.shareit.exc;

public class EmailAlreadyExistsException extends ValidationException {
    public EmailAlreadyExistsException(String message, String className) {
        super(message, className);
    }
}