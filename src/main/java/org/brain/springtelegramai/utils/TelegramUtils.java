package org.brain.springtelegramai.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TelegramUtils {
    public String escapeMarkdownV2(String text) {
        String[] specialCharacters = new String[] {"_", "*", "[", "]", "(", ")", "~", ">", "#", "+", "-", "=", "|", "{", "}", ".", "!"};
        for (String character : specialCharacters) {
            text = text.replace(character, "\\" + character);
        }
        return text;
    }
}
