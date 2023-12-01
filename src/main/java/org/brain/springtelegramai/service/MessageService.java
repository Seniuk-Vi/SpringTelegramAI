package org.brain.springtelegramai.service;

import org.brain.springtelegramai.model.MessageEntity;

import java.util.List;

public interface MessageService {

    MessageEntity saveMessage(MessageEntity messageEntity);
    void deleteLastAssistantMessageByChatId(Long chatId);
    MessageEntity getLastAssistantMessageByChatId(Long chatId);

    List<MessageEntity> getAllGPTConversationByChatId(Long chatId);
    List<MessageEntity> getAllByChatId(Long chatId);

    void deleteAllByChatId(Long chatId);

}
