package quizz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import quizz.dto.guestuser.GuestLoginResponseDto;
import quizz.service.interfaces.GuestUserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Guest management", description = "Create guest user")
@RequestMapping("/guest")
public class GuestController {
    private final GuestUserService guestUserService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create guest user", description = "Create guest user")
    public GuestLoginResponseDto createGuestUser() {
        return guestUserService.createGuestUser();
    }
}
