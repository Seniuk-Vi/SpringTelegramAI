package org.brain.springtelegramai.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginRequest {
    @Schema(description = "Email", example = "email@example.com")
    @NotBlank()
    @Email(message = "Email not valid")
    private String email;
    @Schema(description = "Password", example = "Password52")
    @NotBlank()
    private String password;
}
