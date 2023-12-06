package org.brain.springtelegramai.service;

import org.brain.springtelegramai.model.ChatEntity;
import org.brain.springtelegramai.model.MessageEntity;

import java.util.List;

public interface AdminConsoleService {
    void sendMessageToChat(Long chatId, String message);

    List<ChatEntity> getAllChats();
    List<MessageEntity> getChatMessages(Long chatId);

}
