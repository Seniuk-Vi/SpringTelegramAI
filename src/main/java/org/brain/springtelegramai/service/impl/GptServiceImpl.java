package org.brain.springtelegramai.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.springtelegramai.exception.UnchangedResponseException;
import org.brain.springtelegramai.model.MessageEntity;
import org.brain.springtelegramai.payload.GptMessage;
import org.brain.springtelegramai.payload.GptRole;
import org.brain.springtelegramai.payload.request.GptRequest;
import org.brain.springtelegramai.payload.response.GptResponse;
import org.brain.springtelegramai.service.ChatService;
import org.brain.springtelegramai.service.GptService;
import org.brain.springtelegramai.service.MessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GptServiceImpl implements GptService {
    private final WebClient webClient;
    private final RestTemplate restTemplate;
    private final MessageService messageService;
    private final ChatService userService;
    @Value("${gpt.model}")
    private String MODEL;


    @Override
    @Transactional
    public GptResponse newMessage(Long chatId, String content) {
        log.info("New message from chat: {} - {}", chatId, content);
        // gather history
        List<MessageEntity> historyMessages = messageService.getAllByChatId(chatId);
        // create request
        GptRequest chatGptRequest = GptRequest.builder()
                .messages(historyMessages
                        .stream()
                        .map(message -> new GptMessage(message.getRole(), message.getContent()))
                        .collect(Collectors.toList()))
                .build();
        chatGptRequest.getMessages().add(new GptMessage(GptRole.user, content));
        // send to gpt
//
        var response = processRequest(chatGptRequest);
        log.debug("Response from GPT: {}", response);
        // save to db
        var chat = userService.getByChatId(chatId);
        messageService.saveMessage(MessageEntity.builder()
                .chat(chat)
                .content(content)
                .role(GptRole.user)
                .created(LocalDateTime.now())
                .build());
        messageService.saveMessage(MessageEntity.builder()
                .chat(chat)
                .content(response.getChoices().get(0).getMessage().getContent())
                .role(GptRole.assistant)
                .created(LocalDateTime.now())
                .build());
        return response;
    }

    @Override
    @Transactional(rollbackFor = UnchangedResponseException.class)
    public GptResponse regenMessage(Long chatId) throws UnchangedResponseException {
        log.info("regen message from chat: {}", chatId);
        // get last assistant response
        var lastAssistantMessage = messageService.getLastAssistantMessageByChatId(chatId);
        // delete last assistant response from db
        messageService.deleteLastAssistantMessageByChatId(chatId);
        // get history
        List<MessageEntity> historyMessages = messageService.getAllByChatId(chatId);
        GptRequest chatGptRequest = GptRequest.builder()
                .messages(historyMessages
                        .stream()
                        .map(message -> new GptMessage(message.getRole(), message.getContent()))
                        .collect(Collectors.toList()))
                .build();
        // send to gpt
        var response = processRequest(chatGptRequest);
        log.debug("Response from GPT: {}", response);
        // check if response is unchanged
        if (lastAssistantMessage.getContent().equals(response.getChoices().get(0).getMessage().getContent())) {
            throw new UnchangedResponseException("GPT response is the same as old");
        }
        // save to db
        var chat = userService.getByChatId(chatId);
        messageService.saveMessage(MessageEntity.builder()
                .chat(chat)
                .content(response.getChoices().get(0).getMessage().getContent())
                .role(GptRole.assistant)
                .created(LocalDateTime.now())
                .build());
        return response;
    }

    private GptResponse processRequest(GptRequest chatGptRequest) {
        chatGptRequest.setModel(MODEL);
        return webClient.post()
                .bodyValue(chatGptRequest)
                .retrieve()
                .bodyToMono(GptResponse.class)
                .block();
    }

}
