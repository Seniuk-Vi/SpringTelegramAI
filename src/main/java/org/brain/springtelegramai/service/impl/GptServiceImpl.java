package org.brain.springtelegramai.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.springtelegramai.payload.request.GptRequest;
import org.brain.springtelegramai.payload.response.GptResponse;
import org.brain.springtelegramai.service.GptService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;


@Service
@Slf4j
@RequiredArgsConstructor
public class GptServiceImpl implements GptService {
    private final WebClient webClient;
    private final RestTemplate restTemplate;

    @Value("${gpt.model}")
    private String MODEL;

    @Override
    @Transactional
    public GptResponse newMessage(GptRequest chatGptRequest) {
        log.info("New message: {}", chatGptRequest);
        // send to gpt
        var response = processRequest(chatGptRequest);
        log.debug("Response from GPT: {}", response);
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
