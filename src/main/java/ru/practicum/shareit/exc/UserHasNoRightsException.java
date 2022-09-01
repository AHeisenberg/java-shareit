package ru.practicum.shareit.exc;

public class UserHasNoRightsException extends ValidationException {
    public UserHasNoRightsException(String message, String className) {
        super(message, className);
    }
}