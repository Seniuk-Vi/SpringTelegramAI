package org.brain.springtelegramai.repository;

import org.brain.springtelegramai.model.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
    Optional<ChatEntity> findByChatId(Long chatId);
}
