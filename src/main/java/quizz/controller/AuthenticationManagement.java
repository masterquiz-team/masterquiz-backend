package quizz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import quizz.dto.auth.LoginDto;
import quizz.dto.auth.LoginRequestDto;
import quizz.dto.register.UserRequestDto;
import quizz.dto.register.UserResponseDto;
import quizz.service.interfaces.AuthenticationService;
import quizz.service.interfaces.RegistrationService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication management", description = "Authentication management")
public class AuthenticationManagement {
    private final AuthenticationService authenticationService;
    private final RegistrationService registrationService;

    @PostMapping("/login")
    @Operation(tags = "Log in a user", description = "Login a user and return a token")
    public LoginDto login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return authenticationService.authenticate(loginRequestDto);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(tags = "Register a user", description = "Register a user and return dto")
    public UserResponseDto register(@Valid @RequestBody UserRequestDto userRequestDto) {
        return registrationService.register(userRequestDto);
    }
}
