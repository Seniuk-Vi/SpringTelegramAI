package org.brain.springtelegramai.exception.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Error {
    private String message;
    private ErrorType errorType;
    private LocalDateTime timestamp;
}
