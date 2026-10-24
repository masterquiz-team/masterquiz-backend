package quizz.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import quizz.dto.leaderboard.MyStatsDto;
import quizz.dto.leaderboard.TotalScoreByCategoryDto;
import quizz.dto.leaderboard.TotalScoreStatsDto;
import quizz.exception.EntityNotFoundException;
import quizz.mapper.TotalScoreByCategoryMapper;
import quizz.mapper.TotalScoreMapper;
import quizz.model.GamesByCategoryResult;
import quizz.model.User;
import quizz.repository.GamesByCategoryResultRepository;
import quizz.repository.UserRepository;
import quizz.service.interfaces.LeaderboardService;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {
    private final UserRepository userRepository;
    private final GamesByCategoryResultRepository gamesByCategoryResultRepository;
    private final TotalScoreMapper totalScoreMapper;
    private final TotalScoreByCategoryMapper totalScoreByCategoryMapper;

    @Override
    public MyStatsDto showMyStats(Long id, String name) {
        User watcher = findByEmail(name);

        int rank = 0;
        List<GamesByCategoryResult> result;
        List<User> users = userRepository.findAllByScore();

        User userById;

        if (id == null) {
            userById = watcher;
        } else {
            userById = userRepository.findById(id).orElseThrow(
                    () -> new EntityNotFoundException(
                            "User with id: " + id + " doesn't exist"));
        }

        if (watcher.getId().equals(userById.getId())) {
            rank = findRank(users, watcher);
            result = gamesByCategoryResultRepository.findAllByUserId(watcher.getId());
            return getStats(result, watcher, rank, true);
        } else {
            rank = findRank(users, userById);
            result = gamesByCategoryResultRepository.findAllByUserId(userById.getId());
            return getStats(result, userById, rank, false);
        }
    }

    @Override
    public Page<TotalScoreStatsDto> showPageOfStats(String email, Pageable pageable) {
        User user = findByEmail(email);
        Page<User> users = userRepository.findAllByScore(pageable);

        Page<TotalScoreStatsDto> page = users.map(totalScoreMapper::toDto);

        for (int i = 0; i < page.getContent().size(); i++) {
            TotalScoreStatsDto t = page.getContent().get(i);

            t.setFlag(t.getUserId().equals(user.getId()));

            int rank = (int) page.getPageable().getOffset() + i + 1;
            t.setRank(rank);
        }
        return page;
    }

    @Override
    public Page<TotalScoreByCategoryDto> showStatsOfCategory(
            Long categoryId,
            String email,
            Pageable pageable) {

        User user = findByEmail(email);

        Page<GamesByCategoryResult> byCategoryId = gamesByCategoryResultRepository
                .findAllByCategoryId(categoryId, pageable);
        Page<TotalScoreByCategoryDto> totalScore = byCategoryId
                .map(totalScoreByCategoryMapper::toDto);

        for (int i = 0; i < totalScore.getContent().size(); i++) {
            TotalScoreByCategoryDto g = totalScore.getContent().get(i);
            g.setFlag(g.getUserId().equals(user.getId()));
            int rank = (int) totalScore.getPageable().getOffset() + i + 1;
            g.setRank(rank);
        }
        return totalScore;
    }

    private MyStatsDto getStats(
            List<GamesByCategoryResult> results,
            User watcher,
            int rank,
            boolean flag) {

        Map<Long, Integer> scoreOfCategories = new HashMap<>();

        for (GamesByCategoryResult g : results) {
            scoreOfCategories.put(g.getCategory().getId(), g.getCategoryScore());
        }

        MyStatsDto myStatsDto = new MyStatsDto();
        myStatsDto.setNickname(watcher.getNickname());
        myStatsDto.setTotalScore(watcher.getScore());
        myStatsDto.setTotalGamesPlayed(watcher.getTotalGamesPlayed());
        myStatsDto.setRank(rank);
        myStatsDto.setScoreByCategoryId(scoreOfCategories);
        myStatsDto.setFlag(flag);

        return myStatsDto;
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email: " + email + " doesn't exist"));
    }

    private int findRank(List<User> users, User user) {
        int rank = 0;
        for (User u : users) {
            if (u.getId().equals(user.getId())) {
                rank = users.indexOf(u) + 1;
            }
        }
        return rank;
    }
}
