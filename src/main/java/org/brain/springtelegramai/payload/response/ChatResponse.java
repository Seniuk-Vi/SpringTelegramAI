package org.brain.springtelegramai.payload.response;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ChatResponse {

    private Long chatId;

    private String firstName;

    private String lastName;

    private String userName;

    private Timestamp registeredAt;

}
