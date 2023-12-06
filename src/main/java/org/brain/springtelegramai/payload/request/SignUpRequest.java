package org.brain.springtelegramai.payload.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.brain.springtelegramai.validation.EqualFields;

@Builder
@Data
@EqualFields(message = "Passwords didn't match", value = {"password", "confirmPassword"})
public class SignUpRequest {
    /**
     * At least 1 digit.
     * At least 1 lowercase letter.
     * At least 1 uppercase letter.
     * No white-space characters or other symbol.
     * At least 8 characters long.
     */
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
    @Schema(description = "Email", example = "email@example.com")
    @NotBlank()
    @Email(message = "Email not valid")
    private String email;
    @Schema(description = "Password", example = "Password52")
    @NotBlank()
    @Pattern(message = "Password not valid", regexp = PASSWORD_PATTERN)
    private String password;
    @Schema(description = "Confirm password", example = "Password52")
    @Pattern(message = "Confirm password not valid", regexp = PASSWORD_PATTERN)
    @NotBlank()
    private String confirmPassword;
}

