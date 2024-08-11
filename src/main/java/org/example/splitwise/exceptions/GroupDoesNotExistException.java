package org.example.splitwise.exceptions;

public class GroupDoesNotExistException extends Exception {
    public GroupDoesNotExistException(String message) {
        super(message);
    }
}
