package org.brain.springtelegramai.payload.response;

import lombok.Data;
import org.brain.springtelegramai.model.ChatRole;

import java.time.LocalDateTime;

@Data
public class MessageResponse {

    private String content;

    private ChatRole role;

    private LocalDateTime created;
}

