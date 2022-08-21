package ru.practicum.shareit.exc;

public class ObjectNotFoundException extends ValidationException {
    public ObjectNotFoundException(String message, String className) {
        super(message, className);
    }
}
