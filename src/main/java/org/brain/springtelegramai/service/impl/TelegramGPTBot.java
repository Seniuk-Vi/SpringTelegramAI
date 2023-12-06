package org.brain.springtelegramai.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.brain.springtelegramai.config.BotConfig;
import org.brain.springtelegramai.exception.UnchangedResponseException;
import org.brain.springtelegramai.model.ChatRole;
import org.brain.springtelegramai.model.MessageEntity;
import org.brain.springtelegramai.payload.GptMessage;
import org.brain.springtelegramai.payload.request.GptRequest;
import org.brain.springtelegramai.payload.response.GptResponse;
import org.brain.springtelegramai.service.ChatService;
import org.brain.springtelegramai.service.GptService;
import org.brain.springtelegramai.service.MessageService;
import org.brain.springtelegramai.service.TelegramBot;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.brain.springtelegramai.service.chatcomponents.Keyboards.*;

@Service
@Slf4j
public class TelegramGPTBot extends TelegramLongPollingBot implements TelegramBot {
    private final ChatService chatService;
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

    public TelegramGPTBot(BotConfig botConfig, ChatService chatService, GptService chatGptService, MessageService messageService) {
        super(botConfig.getToken());

        this.botConfig = botConfig;
        this.chatService = chatService;
        this.chatGptService = chatGptService;
        this.messageService = messageService;

        setMyCommands();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            onMessageReceived(update, message, chatId);
        } else if (update.hasCallbackQuery()) {
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            onCallbackReceived(update, chatId, messageId);
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }


    private void setMyCommands() {
        List<BotCommand> botCommandList = new ArrayList<>();
        botCommandList.add(new BotCommand("/start", "Start using bot"));
        botCommandList.add(new BotCommand("/help", "Show help"));
        botCommandList.add(new BotCommand("/register", "Set bot settings"));
        executeMessage(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
    }


    private void onCallbackReceived(Update update, Long chatId, long messageId) {
        switch (update.getCallbackQuery().getData()) {
            case REGENERATE_MESSAGE_BUTTON_DATA -> regenGptMessage(chatId, messageId);
            default -> sendMessage(chatId, "Sorry, I don't understand you.");
        }
    }

    private void onMessageReceived(Update update, String message, Long chatId) {
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

    private void createNewChat(Long chatId, Update update) {
        chatService.save(chatId, update);
        messageService.deleteAllByChatId(chatId);
        greetingMessage(chatId, update);
    }

    private void greetingMessage(Long chatId, Update update) {
        String answer = "Hello, " +
                update.getMessage().getChat().getFirstName() +
                "! " +
                "I'm " +
                botConfig.getBotName() +
                ". " +
                "How can I help you?";
        sendMessage(chatId, answer, getReplyKeyboardMarkup());
    }

    public void newGptMessage(Long chatId, String content) {
        typingAction(chatId);
        // gather history
        List<MessageEntity> historyMessages = messageService.getGPTConversationByChatId(chatId);
        // create request
        GptRequest chatGptRequest = GptRequest.builder()
                .messages(historyMessages
                        .stream()
                        .map(message -> new GptMessage(message.getRole(), message.getContent()))
                        .collect(Collectors.toList()))
                .build();
        chatGptRequest.getMessages().add(new GptMessage(ChatRole.user, content));
        // send message to gpt
        GptResponse chatGptResponse = chatGptService.newMessage(chatGptRequest);
        // save response and request to db
        var chat = chatService.getByChatId(chatId);
        messageService.saveMessage(MessageEntity.builder()
                .chat(chat)
                .content(content)
                .role(ChatRole.user)
                .created(LocalDateTime.now())
                .build());
        messageService.saveMessage(MessageEntity.builder()
                .chat(chat)
                .content(chatGptResponse.getChoices().get(0).getMessage().getContent())
                .role(ChatRole.assistant)
                .created(LocalDateTime.now())
                .build());
        // send response to chat
        sendMessage(chatId, chatGptResponse.getChoices().get(0).getMessage().getContent(),
                getRegisterInlineKeyboardMarkup());
    }

    public void regenGptMessage(Long chatId, Long messageId) {
        typingAction(chatId);
        // get last assistant response
        var lastAssistantMessage = messageService.getLastAssistantMessageByChatId(chatId);
        // delete last assistant response from db
        messageService.deleteLastAssistantMessageByChatId(chatId);
        // get history
        List<MessageEntity> historyMessages = messageService.getGPTConversationByChatId(chatId);
        GptRequest chatGptRequest = GptRequest.builder()
                .messages(historyMessages
                        .stream()
                        .map(message -> new GptMessage(message.getRole(), message.getContent()))
                        .collect(Collectors.toList()))
                .build();
        // send message to gpt
        GptResponse chatGptResponse = chatGptService.newMessage(chatGptRequest);
        // check if response is unchanged
        if (lastAssistantMessage.getContent().equals(chatGptResponse.getChoices().get(0).getMessage().getContent())) {
            throw new UnchangedResponseException("Gpt response is unchanged");
        }
        // save to db
        var chat = chatService.getByChatId(chatId);
        messageService.saveMessage(MessageEntity.builder()
                .chat(chat)
                .content(chatGptResponse.getChoices().get(0).getMessage().getContent())
                .role(ChatRole.assistant)
                .created(LocalDateTime.now())
                .build());
        var response = chatGptResponse.getChoices().get(0).getMessage().getContent();
        // edit chat message
        editMessage(chatId, messageId, response);
    }

    private void typingAction(Long chatId) {
        SendChatAction sendChatAction = SendChatAction.builder()
                .chatId(chatId)
                .action(ActionType.TYPING.toString())
                .build();
        executeMessage(sendChatAction);
    }

    private void editMessage(Long chatId, long messageId, String message) {
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
    @Override
    public void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        executeMessage(sendMessage);
    }

    private void sendMessage(Long chatId, String message, ReplyKeyboard replyKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        executeMessage(sendMessage);
    }

    /**
     * This method is general method to send messages and catch exceptions.
     *
     * @param message BotApiMethod to send
     */
    private void executeMessage(BotApiMethod<?> message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error while sending message: {}", e.getMessage());
        }
    }

}
