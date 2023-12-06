package org.brain.springtelegramai.mapper;

import org.brain.springtelegramai.model.MessageEntity;
import org.brain.springtelegramai.payload.response.MessageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    MessageResponse mapToMessageResponse(MessageEntity request);
    List<MessageResponse> mapToMessageResponses(List<MessageEntity> request);
}
