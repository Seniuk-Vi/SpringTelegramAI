package org.brain.springtelegramai.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.springtelegramai.model.ChatEntity;
import org.brain.springtelegramai.repository.ChatRepository;
import org.brain.springtelegramai.service.ChatService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;

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

    public void save(Chat chat) {
        userRepository.findByChatId(chat.getId())
                .ifPresentOrElse(
                        userEntity -> {
                            userEntity.setFirstName(chat.getFirstName());
                            userEntity.setLastName(chat.getLastName());
                            userEntity.setUserName(chat.getUserName());
                            userRepository.save(userEntity);
                        },
                        () -> userRepository.save(
                                new ChatEntity(
                                        chat.getId(),
                                        chat.getFirstName(),
                                        chat.getLastName(),
                                        chat.getUserName(),
                                        new Timestamp(System.currentTimeMillis())
                                )
                        )
                );
    }

}
