package org.brain.springtelegramai.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.springtelegramai.model.ChatEntity;
import org.brain.springtelegramai.repository.ChatRepository;
import org.brain.springtelegramai.service.ChatService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.Timestamp;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository userRepository;

    @Override
    public List<ChatEntity> getAll() {
        return userRepository.findAll();
    }

    @Override
    public ChatEntity getByChatId(Long chatId) {
        return userRepository.findByChatId(chatId).orElseThrow();
    }

    public void save(Long chatId, Update update) {
        userRepository.findByChatId(chatId)
                .ifPresentOrElse(
                        userEntity -> {
                            userEntity.setFirstName(update.getMessage().getChat().getFirstName());
                            userEntity.setLastName(update.getMessage().getChat().getLastName());
                            userEntity.setUserName(update.getMessage().getChat().getUserName());
                            userRepository.save(userEntity);
                        },
                        () -> userRepository.save(
                                new ChatEntity(
                                        chatId,
                                        update.getMessage().getChat().getFirstName(),
                                        update.getMessage().getChat().getLastName(),
                                        update.getMessage().getChat().getUserName(),
                                        new Timestamp(System.currentTimeMillis())
                                )
                        )
                );
    }

}
