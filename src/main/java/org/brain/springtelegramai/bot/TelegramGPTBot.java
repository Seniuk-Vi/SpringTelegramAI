package org.brain.springtelegramai.bot;

import lombok.extern.slf4j.Slf4j;
import org.brain.springtelegramai.config.BotConfig;
import org.brain.springtelegramai.exception.UnchangedResponseException;
import org.brain.springtelegramai.model.ChatEntity;
import org.brain.springtelegramai.payload.response.GptResponse;
import org.brain.springtelegramai.service.ChatService;
import org.brain.springtelegramai.service.GptService;
import org.brain.springtelegramai.service.MessageService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static org.brain.springtelegramai.service.chatcomponents.Keyboards.*;

@Service
@Slf4j
public class TelegramGPTBot extends TelegramLongPollingBot {
    private final ChatService userService;
    private final MessageService messageService;
    private final GptService chatGptService;
    private static final String HELP_TEXT = """
            This bot is designed to help interact with ChatGPT
                        
            Commands:
            /start - Start using bot
            /help - Show help
            /settings - Set bot settings
            """;

    private final BotConfig botConfig;

    public TelegramGPTBot(BotConfig botConfig, ChatService userService, GptService chatGptService, MessageService messageService) {
        super(botConfig.getToken());

        this.botConfig = botConfig;
        this.userService = userService;
        this.chatGptService = chatGptService;
        this.messageService = messageService;

        setMyCommands();
    }

    private void setMyCommands() {
        List<BotCommand> botCommandList = new ArrayList<>();
        botCommandList.add(new BotCommand("/start", "Start using bot"));
        botCommandList.add(new BotCommand("/help", "Show help"));
        botCommandList.add(new BotCommand("/register", "Set bot settings"));
        executeMessage(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            onMessageReceived(update, message, chatId);
        } else if (update.hasCallbackQuery()) {
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            onCallbackReceived(update, chatId, messageId);
        }
    }

    private void onCallbackReceived(Update update, String chatId, long messageId) {
        switch (update.getCallbackQuery().getData()) {
            case REGENERATE_MESSAGE_BUTTON_DATA -> regenGptMessage(chatId, messageId);
            default -> sendMessage(chatId, "Sorry, I don't understand you.");
        }
    }

    private void onMessageReceived(Update update, String message, String chatId) {
        if (message.equals(NEW_CHAT_TEXT)) {
            createNewChat(chatId, update);
        } else {
            switch (message) {
                case "/start" -> createNewChat(chatId, update);
                case "/help" -> sendMessage(chatId, HELP_TEXT);
                case "/settings" -> sendMessage(chatId, "Not implemented yet");
                default -> newGptMessage(chatId, message);
            }
        }
    }

    private void createNewChat(String chatId, Update update) {
        userService.save(chatId, update);
        messageService.deleteAllByChatId(Long.valueOf(chatId));
        greetingMessage(chatId, update);
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    private void greetingMessage(String chatId, Update update) {
        String answer = "Hello, " +
                update.getMessage().getChat().getFirstName() +
                "! " +
                "I'm " +
                botConfig.getBotName() +
                ". " +
                "How can I help you?";
        sendMessage(chatId, answer, getReplyKeyboardMarkup());
    }

    private void newGptMessage(String chatId, String message) {
        typingAction(chatId);
        // send message to gpt
        GptResponse chatGptResponse = chatGptService.newMessage(Long.valueOf(chatId), message);
        // send response to user
        sendMessage(chatId, chatGptResponse.getChoices().get(0).getMessage().getContent(), getRegisterInlineKeyboardMarkup());
    }

    private void regenGptMessage(String chatId, Long messageId) {
        typingAction(chatId);
        // send message to gpt
        try {
            GptResponse chatGptResponse = chatGptService.regenMessage(Long.valueOf(chatId));
            // send response to user
            var response = chatGptResponse.getChoices().get(0).getMessage().getContent();
            editMessage(chatId, messageId, response);
        } catch (UnchangedResponseException e) {
            log.debug("Unchanged response from GPT: {}", e.getMessage());
        }
    }

    private void typingAction(String chatId) {
        SendChatAction sendChatAction = SendChatAction.builder()
                .chatId(chatId)
                .action(ActionType.TYPING.toString())
                .build();
        executeMessage(sendChatAction);
    }

    private void editMessage(String chatId, long messageId, String message) {
        EditMessageText editMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(Math.toIntExact(messageId))
                .text(message)
                .replyMarkup(getRegisterInlineKeyboardMarkup())
                .build();
        executeMessage(editMessage);
    }


    /**
     * This method is used to send messages to user with chatId.
     *
     * @param chatId  chatId of user
     * @param message message to send
     */
    private void sendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        executeMessage(sendMessage);
    }

    /**
     * This method is used to send messages to all users.
     */
    private void sendMessage(String message) {
        for (ChatEntity userEntity : userService.getAll()
        ) {
            sendMessage(userEntity.getChatId().toString(), message);
        }
    }

    private void sendMessage(String chatId, String message, ReplyKeyboard replyKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        executeMessage(sendMessage);
    }

    /**
     * This method is general method to send messages and catch exceptions.
     *
     * @param editMessage BotApiMethod to send
     */
    private void executeMessage(BotApiMethod<?> editMessage) {
        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Error while sending message: {}", e.getMessage());
        }
    }


}
