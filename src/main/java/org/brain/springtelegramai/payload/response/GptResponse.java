package org.brain.springtelegramai.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.brain.springtelegramai.payload.GptMessage;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GptResponse {
    private List<ChatGptChoice> choices;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ChatGptChoice {
        private int index;
        private GptMessage message;
    }
}
