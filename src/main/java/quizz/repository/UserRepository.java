package quizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quizz.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
