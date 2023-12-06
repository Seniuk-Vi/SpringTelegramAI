package org.brain.springtelegramai.mapper;

import org.brain.springtelegramai.model.UserEntity;
import org.brain.springtelegramai.payload.request.LoginRequest;
import org.brain.springtelegramai.payload.request.SignUpRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserEntity mapToUser(SignUpRequest request);
    UserEntity mapToUser(LoginRequest request);

}
