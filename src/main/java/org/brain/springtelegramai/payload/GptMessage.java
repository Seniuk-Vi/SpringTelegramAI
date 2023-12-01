package org.brain.springtelegramai.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GptMessage {
    private GptRole role;
    private String content;
}
