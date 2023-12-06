package org.brain.springtelegramai.service;

import org.brain.springtelegramai.payload.request.GptRequest;
import org.brain.springtelegramai.payload.response.GptResponse;

public interface GptService {

    GptResponse newMessage(GptRequest chatGptRequest);

}
