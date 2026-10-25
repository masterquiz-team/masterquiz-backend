package quizz.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import quizz.model.GamesByCategoryResult;

@Repository
public interface GamesByCategoryResultRepository extends JpaRepository<
        GamesByCategoryResult, Long> {
    Optional<GamesByCategoryResult> findByCategoryIdAndUserId(Long categoryId, Long userId);

    Optional<GamesByCategoryResult> findByCategoryIdAndGuestUserId(
            Long categoryId, Long guestUserId);

    List<GamesByCategoryResult> findAllByUserId(Long id);

    @Query("FROM GamesByCategoryResult r "
            + "WHERE r.category.id = :categoryId "
            + "ORDER BY r.categoryScore DESC, r.user.nickname")
    Page<GamesByCategoryResult> findAllByCategoryId(Long categoryId, Pageable pageable);
}
