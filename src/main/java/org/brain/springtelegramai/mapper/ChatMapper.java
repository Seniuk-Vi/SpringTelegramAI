package org.brain.springtelegramai.mapper;

import org.brain.springtelegramai.model.ChatEntity;
import org.brain.springtelegramai.payload.response.ChatResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ChatMapper {
    ChatMapper INSTANCE = Mappers.getMapper(ChatMapper.class);

    ChatResponse mapToChatResponse(ChatEntity request);
    List<ChatResponse> mapToChatResponses(List<ChatEntity> request);
}
