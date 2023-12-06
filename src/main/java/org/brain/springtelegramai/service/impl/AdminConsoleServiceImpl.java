package org.brain.springtelegramai.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.springtelegramai.model.ChatEntity;
import org.brain.springtelegramai.model.ChatRole;
import org.brain.springtelegramai.model.MessageEntity;
import org.brain.springtelegramai.service.AdminConsoleService;
import org.brain.springtelegramai.service.ChatService;
import org.brain.springtelegramai.service.MessageService;
import org.brain.springtelegramai.service.TelegramBot;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminConsoleServiceImpl implements AdminConsoleService {
    private final TelegramBot telegramBot;
    private final MessageService messageService;
    private final ChatService chatService;

    private static final String ADMIN_HEADER_TEXT = "Admin message: \n";

    @Override
    public void sendMessageToChat(Long chatId, String message) {
        log.info("Send message to chat: {}, message: {}", chatId, message);
        // send message to chat
        message = ADMIN_HEADER_TEXT + message;
        telegramBot.sendMessage(chatId, message);
        // save message to db
        var chat = chatService.getByChatId(chatId);
        messageService.saveMessage(MessageEntity.builder()
                .content(message)
                .role(ChatRole.admin)
                .chat(chat)
                .created(LocalDateTime.now())
                .build());
    }

    @Override
    public List<ChatEntity> getAllChats() {
        return chatService.getAll();
    }

    @Override
    public List<MessageEntity> getChatMessages(Long chatId) {
        return messageService.getAllConversationByChatId(chatId);
    }
}
