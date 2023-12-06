package org.brain.springtelegramai.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.springtelegramai.model.MessageEntity;
import org.brain.springtelegramai.repository.MessageRepository;
import org.brain.springtelegramai.service.MessageService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    @Override
    public void saveMessage(MessageEntity messageEntity) {
        messageRepository.save(messageEntity);
    }

    @Override
    public void deleteLastAssistantMessageByChatId(Long chatId) {
        MessageEntity lastMessage = getLastAssistantMessageByChatId(chatId);
        messageRepository.delete(lastMessage);
    }

    @Override
    public MessageEntity getLastAssistantMessageByChatId(Long chatId) {
        return messageRepository.findLatestAssistantMessageByChatId(chatId).orElseThrow();
    }

    @Override
    public List<MessageEntity> getGPTConversationByChatId(Long chatId) {
        return messageRepository.findGPTConversationByUser_ChatIdOrderByCreatedAsc(
                chatId);
    }

    @Override
    public List<MessageEntity> getAllConversationByChatId(Long chatId) {
        return messageRepository.findByChat_ChatIdOrderByCreatedAsc(
                chatId, Sort.by(Sort.Direction.ASC, "created"));
    }


    @Override
    @Transactional
    public void deleteAllByChatId(Long chatId) {
        messageRepository.deleteByChat_ChatId(chatId);
    }
}
