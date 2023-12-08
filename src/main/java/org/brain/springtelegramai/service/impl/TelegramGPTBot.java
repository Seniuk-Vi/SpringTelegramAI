package org.brain.springtelegramai.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.brain.springtelegramai.config.BotConfig;
import org.brain.springtelegramai.exception.MessageNotSentException;
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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.brain.springtelegramai.service.chatcomponents.Keyboards.*;

@Service
@Slf4j
public class TelegramGPTBot extends TelegramLongPollingBot implements TelegramBot {
    private final ChatService chatService;
    private final MessageService messageService;
    private final GptService chatGptService;

    private final TransactionTemplate transactionTemplate;

    private static final String HELP_TEXT = """
            This bot is designed to help interact with ChatGPT
                        
            Commands:
            /start - Start using bot
            /help - Show help
            /settings - Set bot settings
            """;
    private final BotConfig botConfig;

    public TelegramGPTBot(BotConfig botConfig, ChatService chatService, GptService chatGptService, MessageService messageService, TransactionTemplate transactionTemplate) {
        super(botConfig.getToken());

        this.botConfig = botConfig;
        this.chatService = chatService;
        this.chatGptService = chatGptService;
        this.messageService = messageService;
        this.transactionTemplate = transactionTemplate;

        setMyCommands();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            onMessageReceived(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            onCallbackReceived(update);
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


    private void onCallbackReceived(Update update) {
        switch (update.getCallbackQuery().getData()) {
            case REGENERATE_MESSAGE_BUTTON_DATA ->
                    regenGptMessage(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
           default ->
                    sendMessage(update.getCallbackQuery().getMessage().getChatId(), "Sorry, I don't understand you.");
        }
    }


    private void onMessageReceived(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();
        if (text.equals(NEW_CHAT_TEXT)) {
            createNewChat(message.getChat());
        } else {
            switch (text) {
                case "/start" -> createNewChat(message.getChat());
                case "/help" -> sendMessage(chatId, HELP_TEXT);
                case "/settings" -> sendMessage(chatId, "Not implemented yet");
                default -> newGptMessage(message);
            }
        }
    }

    private void createNewChat(Chat chat) {
        chatService.save(chat);
        messageService.deleteAllByChatId(chat.getId());
        greetingMessage(chat);
    }

    private void greetingMessage(Chat chat) {
        String message = "Hello, " +
                chat.getFirstName() +
                "! " +
                "I'm " +
                botConfig.getBotName() +
                ". " +
                "How can I help you?";
        sendMessage(chat.getId(), message, getReplyKeyboardMarkup());
    }

    public void newGptMessage(Message message) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                typingAction(message.getChatId());
                // remove inline keyboard from last assistant message
                removeInlineKeyboardFromLastAssistantMessage(message);
                // gather history
                List<MessageEntity> historyMessages = messageService.getGPTConversationByChatId(message.getChatId());
                // create request
                GptRequest chatGptRequest = GptRequest.builder()
                        .messages(historyMessages
                                .stream()
                                .map(historyMessage -> new GptMessage(historyMessage.getRole(), historyMessage.getContent()))
                                .collect(Collectors.toList()))
                        .build();
                chatGptRequest.getMessages().add(new GptMessage(ChatRole.user, message.getText()));
                // send message to gpt
                GptResponse chatGptResponse = chatGptService.newMessage(chatGptRequest);
                // send response to chat
                Message sentMessage = sendMessage(message.getChatId(), chatGptResponse.getChoices().get(0).getMessage().getContent(),
                        getRegisterInlineKeyboardMarkup());
                // save response and request to db
                var chat = chatService.getByChatId(message.getChatId());
                messageService.saveMessage(MessageEntity.builder()
                        .chat(chat)
                        .chatMessageId(message.getMessageId())
                        .content(message.getText())
                        .role(ChatRole.user)
                        .created(LocalDateTime.now())
                        .build());
                messageService.saveMessage(MessageEntity.builder()
                        .chat(chat)
                        .chatMessageId(sentMessage.getMessageId())
                        .content(chatGptResponse.getChoices().get(0).getMessage().getContent())
                        .role(ChatRole.assistant)
                        .created(LocalDateTime.now())
                        .build());
            }
        });
    }

    private void removeInlineKeyboardFromLastAssistantMessage(Message message) {
        Optional<MessageEntity> lastMessageOptional = messageService.getLastAssistantMessageByChatId(message.getChatId());
        if (lastMessageOptional.isEmpty()) {
            return;
        }
        MessageEntity lastMessage = lastMessageOptional.get();
        editMessage(message.getChatId(), lastMessage.getChatMessageId(), lastMessage.getContent(), null);
    }

    public void regenGptMessage(Long chatId, Integer messageId) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                typingAction(chatId);
                // get last assistant response
                MessageEntity lastAssistantMessage = messageService.getLastAssistantMessageByChatId(chatId).orElseThrow();
                // get history
                List<MessageEntity> historyMessages = messageService.getGPTConversationByChatId(chatId);
                GptRequest chatGptRequest = GptRequest.builder()
                        .messages(historyMessages
                                .stream()
                                .map(message -> new GptMessage(message.getRole(), message.getContent()))
                                .collect(Collectors.toList()))
                        .build();
                // remove last assistant message
                chatGptRequest.getMessages().remove(chatGptRequest.getMessages().size() - 1);
                // send message to gpt
                GptResponse chatGptResponse = chatGptService.newMessage(chatGptRequest);
                // check if response is unchanged
                if (lastAssistantMessage.getContent().equals(chatGptResponse.getChoices().get(0).getMessage().getContent())) {
                    throw new UnchangedResponseException("Gpt response is unchanged");
                }
                // edit chat message
                var response = chatGptResponse.getChoices().get(0).getMessage().getContent();
                editMessage(chatId, messageId, response, getRegisterInlineKeyboardMarkup());
                // update to db
                lastAssistantMessage.setContent(response);
                lastAssistantMessage.setCreated(LocalDateTime.now());
                messageService.saveMessage(lastAssistantMessage);
            }
        });
    }

    private void typingAction(Long chatId) {
        SendChatAction sendChatAction = SendChatAction.builder()
                .chatId(chatId)
                .action(ActionType.TYPING.toString())
                .build();
        executeMessage(sendChatAction);
    }

    private void editMessage(Long chatId, long messageId, String message, InlineKeyboardMarkup replyKeyboardMarkup) {
        EditMessageText editMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(Math.toIntExact(messageId))
                .text(message)
                .replyMarkup(replyKeyboardMarkup)
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

    private Message sendMessage(Long chatId, String message, ReplyKeyboard replyKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return executeMessage(sendMessage);
    }

    /**
     * This method is general method to send messages.
     *
     * @param sendMessage SendMessage to send
     * @return Message that was sent
     * @throws MessageNotSentException if message was not sent
     */
    private Message executeMessage(SendMessage sendMessage) {
        try {
            return execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error while sending message: {}", e.getMessage());
            throw new MessageNotSentException("Error while sending message", e);
        }
    }

    /**
     * This method is general method to send messages.
     *
     * @param message BotApiMethod to send
     */
    private void executeMessage(BotApiMethod<?> message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error while sending message: {}", e.getMessage());
            throw new MessageNotSentException("Error while sending message", e);
        }
    }

}
