package org.brain.springtelegramai.model;

import jakarta.persistence.*;
import lombok.*;
import org.brain.springtelegramai.payload.GptRole;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Builder
public class MessageEntity {

    @Id
    @GeneratedValue(generator = "messages_seq")
    @SequenceGenerator(name = "messages_seq", sequenceName = "messages_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatEntity chat;
    @Column(columnDefinition = "TEXT")
    private String content;
    private GptRole role;
    private LocalDateTime created;
}
