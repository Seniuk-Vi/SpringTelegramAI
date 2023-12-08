package org.brain.springtelegramai.service;

import org.brain.springtelegramai.model.MessageEntity;

import java.util.List;
import java.util.Optional;

public interface MessageService {

    void saveMessage(MessageEntity messageEntity);
    Optional<MessageEntity> getLastAssistantMessageByChatId(Long chatId);

    List<MessageEntity> getGPTConversationByChatId(Long chatId);
    List<MessageEntity> getAllConversationByChatId(Long chatId);

    void deleteAllByChatId(Long chatId);

}
