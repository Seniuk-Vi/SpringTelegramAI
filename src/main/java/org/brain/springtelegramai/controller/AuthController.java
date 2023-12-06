package org.brain.springtelegramai.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.springtelegramai.api.AuthApi;
import org.brain.springtelegramai.mapper.UserMapper;
import org.brain.springtelegramai.model.UserEntity;
import org.brain.springtelegramai.payload.request.LoginRequest;
import org.brain.springtelegramai.payload.request.SignUpRequest;
import org.brain.springtelegramai.payload.response.LoginResponse;
import org.brain.springtelegramai.service.UserService;
import org.brain.springtelegramai.utils.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class AuthController implements AuthApi {
    private UserService userService;
    private JwtUtils jwtUtils;

    @Override
    public ResponseEntity<Void> signUp(SignUpRequest request) {
        log.info("new user registration {}", request);
        UserEntity user = UserMapper.INSTANCE.mapToUser(request);
        userService.signUp(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<LoginResponse> logIn(LoginRequest request) {
        log.info("request for login");
        UserEntity user = UserMapper.INSTANCE.mapToUser(request);
        Authentication authentication = userService.logIn(user);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtils.generateToken(userDetails);
        user = userService.findByEmail(userDetails.getUsername()); // get id
        log.debug("user logged in: " + userDetails.getUsername());
        LoginResponse response = LoginResponse.builder()
                .id(user.getId())
                .email(userDetails.getUsername())
                .token(token)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
