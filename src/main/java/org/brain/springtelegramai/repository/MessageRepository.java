package org.brain.springtelegramai.repository;

import org.brain.springtelegramai.model.MessageEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    List<MessageEntity> findByChat_ChatIdOrderByCreatedAsc(Long chatId, Sort sort);
    /**
     * find latest message by chatId and role == assistant.
     *
     * @param chatId chatId to search
     * @return Optional <{@link MessageEntity}>
     */
    @Query("""
                    SELECT m FROM MessageEntity m\s
                    WHERE m.chat.chatId = :chatId\s
                            AND m.role = org.brain.springtelegramai.payload.GptRole.assistant\s
                    ORDER BY m.created DESC\s
                    LIMIT 1
            """)
    Optional<MessageEntity> findLatestAssistantMessageByChatId(@Param("chatId") Long chatId);

    /**
     * find by chatId and (role == assistant or role == user).
     * sort by created asc.
     *
     */
    @Query("""
                    SELECT m FROM MessageEntity m\s
                    WHERE m.chat.chatId = :chatId\s
                            AND (m.role = org.brain.springtelegramai.payload.GptRole.assistant\s
                            OR m.role = org.brain.springtelegramai.payload.GptRole.user)\s
                    ORDER BY m.created ASC
            """)
    List<MessageEntity> findGPTConversationByUser_ChatIdOrderByCreatedAsc(Long chatId);

    void deleteByChat_ChatId(Long chatId);
}
