package org.brain.springtelegramai.service;

import org.brain.springtelegramai.model.ChatEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface ChatService {
    List<ChatEntity> getAll();
    ChatEntity getByChatId(Long chatId);
    void save(Long chatId, Update update);
}
