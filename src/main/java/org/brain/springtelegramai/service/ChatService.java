package org.brain.springtelegramai.service;

import org.brain.springtelegramai.model.ChatEntity;
import org.telegram.telegrambots.meta.api.objects.Chat;

import java.util.List;

public interface ChatService {
    List<ChatEntity> getAll();
    ChatEntity getByChatId(Long chatId);
    void save(Chat chat);
}
