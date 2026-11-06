package quizz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import quizz.repository.GuestUserRepository;
import quizz.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final GuestUserRepository guestUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository.findByEmail(username)
                .map(user -> (UserDetails) user)
                .orElseGet(() -> guestUserRepository.findByNickname(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found")));
    }
}
