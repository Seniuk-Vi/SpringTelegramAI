package org.brain.springtelegramai.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.brain.springtelegramai.bot.TelegramGPTBot;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class BotInitializer {

    private final TelegramGPTBot telegramBot;
    @SneakyThrows
    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try{
            botsApi.registerBot(telegramBot);
        }catch (TelegramApiException e){
            log.error("Error while registering bot: {}", e.getMessage());
        }
    }
}
