package org.brain.springtelegramai.service.chatcomponents;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class Keyboards {
    public static final String NEW_CHAT_TEXT = "new chat";
    public static final String REGENERATE_MESSAGE_BUTTON_DATA = "REGENERATE_MESSAGE_BUTTON";
    public static final String REGENERATE_MESSAGE_BUTTON = "regenerate";

    private Keyboards() {
    }

    public static ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(NEW_CHAT_TEXT);
        return ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .keyboardRow(keyboardRow)
                .isPersistent(true)
                .build();
    }

    public static InlineKeyboardMarkup getRegisterInlineKeyboardMarkup() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder()
                .text(REGENERATE_MESSAGE_BUTTON)
                .callbackData(REGENERATE_MESSAGE_BUTTON_DATA)
                .build());
        return InlineKeyboardMarkup.builder()
                .keyboardRow(row)
                .build();
    }
}
