package org.brain.springtelegramai.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.brain.springtelegramai.model.ChatRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GptMessage {
    private ChatRole role;
    private String content;
}
