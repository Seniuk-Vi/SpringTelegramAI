package org.brain.springtelegramai.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.brain.springtelegramai.payload.GptMessage;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GptRequest {
    private String model;
    private List<GptMessage> messages;
    @JsonProperty("max_tokens")
    private final int maxTokens = 1000;
}
