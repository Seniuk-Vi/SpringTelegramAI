package org.brain.springtelegramai.service;

import org.brain.springtelegramai.model.UserEntity;
import org.springframework.security.core.Authentication;

public interface UserService {
    void signUp(UserEntity user);

    Authentication logIn(UserEntity user);

    UserEntity findByEmail(String username);
}
