package org.brain.springtelegramai.service;

public interface TelegramBot {
    void sendMessage(Long chatId, String message);
}
