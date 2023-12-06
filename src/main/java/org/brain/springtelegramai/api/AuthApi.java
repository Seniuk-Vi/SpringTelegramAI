package org.brain.springtelegramai.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.brain.springtelegramai.payload.request.LoginRequest;
import org.brain.springtelegramai.payload.request.SignUpRequest;
import org.brain.springtelegramai.payload.response.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Authentication service", description = "Authentication management API")
@RequestMapping("/api/v1/auth")
@Validated
public interface AuthApi {
    @Operation(summary = "Sign-up a default user")
    @ApiResponses({
            @ApiResponse(responseCode = "201")})
    @PostMapping(value = "/signup")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<Void> signUp(@Validated() @RequestBody SignUpRequest request);

    @Operation(summary = "Log-in and get token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = LoginResponse.class), mediaType = "application/json")})})
    @PostMapping(value = "/login")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<LoginResponse> logIn(@Validated() @RequestBody LoginRequest request);
}
