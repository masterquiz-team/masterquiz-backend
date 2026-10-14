package quizz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import quizz.dto.user.UserDto;
import quizz.dto.user.UserUpdateDto;
import quizz.service.interfaces.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for user profile")
@RequestMapping("/user-profile")
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get user profile", description = "Get user profile by his email")
    public UserDto getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getProfile(userDetails.getUsername());
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update user profile", description = "Update user profile by DTO")
    public UserDto updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestBody @Valid UserUpdateDto userUpdateDto) {
        return userService.updateProfile(userDetails.getUsername(), userUpdateDto);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Delete user profile", description = "Delete user profile")
    public void deleteProfile(@AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteProfile(userDetails.getUsername());
    }
}
