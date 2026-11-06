package quizz.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import quizz.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("FROM User u ORDER BY u.score DESC, u.nickname")
    List<User> findAllByScore();

    @Query("FROM User u ORDER BY u.score DESC, u.nickname")
    Page<User> findAllByScore(Pageable pageable);
}
