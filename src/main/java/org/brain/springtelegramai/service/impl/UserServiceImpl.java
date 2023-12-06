package org.brain.springtelegramai.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.brain.springtelegramai.model.UserEntity;
import org.brain.springtelegramai.repository.UserRepository;
import org.brain.springtelegramai.service.RoleService;
import org.brain.springtelegramai.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public record UserServiceImpl(UserRepository userRepository,
                              AuthenticationManager authenticationManager,
                              PasswordEncoder passwordEncoder,
                              RoleService roleService) implements UserService {


    private static final String ACCOUNT_ALREADY_EXISTS = "Account with this email already exists!";

    public void signUp(UserEntity user)   {
        log.info("processing signUp");
        if (userRepository.existsByEmail(user.getEmail())) {
            log.error(ACCOUNT_ALREADY_EXISTS);
            throw new EntityNotFoundException(ACCOUNT_ALREADY_EXISTS);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(roleService.adminRole()));
        userRepository.save(user);
    }

    @Override
    public Authentication logIn(UserEntity user)  {
        log.info("processing login");
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword()
        ));
    }

    @Override
    public UserEntity findByEmail(String username) {
        return userRepository.findByEmail(username).orElseThrow(EntityNotFoundException::new);
    }
}
