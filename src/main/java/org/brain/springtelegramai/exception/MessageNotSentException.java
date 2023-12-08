package org.brain.springtelegramai.exception;

public class MessageNotSentException extends RuntimeException{
    public MessageNotSentException(String message, Throwable cause) {
        super(message, cause);
    }
}
