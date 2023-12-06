package org.brain.springtelegramai.exception;

public class UnchangedResponseException extends RuntimeException{
    public UnchangedResponseException(String message) {
        super(message);
    }
}
