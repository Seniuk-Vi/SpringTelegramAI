package org.brain.springtelegramai.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.brain.springtelegramai.model.UserEntity;
import org.brain.springtelegramai.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private static final String NO_USER_WITH_EMAIL_ERROR_MESSAGE = "No user with such email => ";

    public Optional<UserEntity> findByUserName(String userEmail) {
        return userRepository.findByEmail(userEmail);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) {
        UserEntity user = findByUserName(email)
                .orElseThrow(() -> new EntityNotFoundException(NO_USER_WITH_EMAIL_ERROR_MESSAGE + email));
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList())
                .build();
    }
}
