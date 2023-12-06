package org.brain.springtelegramai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.springtelegramai.api.AdminConsoleApi;
import org.brain.springtelegramai.mapper.ChatMapper;
import org.brain.springtelegramai.mapper.MessageMapper;
import org.brain.springtelegramai.payload.response.ChatResponse;
import org.brain.springtelegramai.payload.response.MessageResponse;
import org.brain.springtelegramai.service.AdminConsoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AdminConsoleController implements AdminConsoleApi {
    private final AdminConsoleService adminConsoleService;
    @Override
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<List<ChatResponse>> getAllChats() {
        log.info("get all chats");
        var resp = ChatMapper.INSTANCE.mapToChatResponses(adminConsoleService.getAllChats());
        return ResponseEntity.ok(resp);
    }

    @Override
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<List<MessageResponse>> getChatMessages(Long chatId) {
        log.info("get chat messages {}", chatId);
        var resp = MessageMapper.INSTANCE.mapToMessageResponses(adminConsoleService.getChatMessages(chatId));
        return ResponseEntity.ok(resp);
    }

    @Override
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Void> sendMessageToChat(Long chatId, String message) {
        log.info("send message to chat {}", chatId);
        adminConsoleService.sendMessageToChat(chatId, message);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
