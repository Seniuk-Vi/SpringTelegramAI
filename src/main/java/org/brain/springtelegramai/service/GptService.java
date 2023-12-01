package org.brain.springtelegramai.service;

import org.brain.springtelegramai.exception.UnchangedResponseException;
import org.brain.springtelegramai.payload.response.GptResponse;

public interface GptService {

    GptResponse newMessage(Long chatId, String content);

    GptResponse regenMessage(Long chatId) throws UnchangedResponseException;
}
