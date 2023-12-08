package org.brain.springtelegramai.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity(name = "messages")
public class MessageEntity {

    @Id
    @GeneratedValue(generator = "messages_seq")
    @SequenceGenerator(name = "messages_seq", sequenceName = "messages_seq", allocationSize = 1)
    private Long id;
    private Integer chatMessageId;
    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatEntity chat;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Enumerated(EnumType.STRING)
    private ChatRole role;
    private LocalDateTime created;
}
