package quizz.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quizz.model.GuestUser;

@Repository
public interface GuestUserRepository extends JpaRepository<GuestUser, Long> {
    boolean existsByNickname(String nickname);

    Optional<GuestUser> findByNickname(String nickname);
}

