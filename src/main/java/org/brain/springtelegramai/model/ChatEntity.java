package org.brain.springtelegramai.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class ChatEntity {

    @Id
    private Long chatId;

    private String firstName;

    private String lastName;

    private String userName;

    private Timestamp registeredAt;
}
