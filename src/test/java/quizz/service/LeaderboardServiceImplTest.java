package quizz.service;

import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import quizz.dto.leaderboard.MyStatsDto;
import quizz.dto.leaderboard.TotalScoreByCategoryDto;
import quizz.dto.leaderboard.TotalScoreStatsDto;
import quizz.exception.EntityNotFoundException;
import quizz.mapper.TotalScoreByCategoryMapper;
import quizz.mapper.TotalScoreMapper;
import quizz.model.Category;
import quizz.model.GamesByCategoryResult;
import quizz.model.User;
import quizz.repository.GamesByCategoryResultRepository;
import quizz.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceImplTest {
    private static final String EMAIL = "test@test.com";
    private static final Long ID_1 = 1L;
    private static final Long ID_2 = 2L;
    private static final Long ID_3 = 3L;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GamesByCategoryResultRepository gamesByCategoryResultRepository;

    @Mock
    private TotalScoreMapper totalScoreMapper;

    @Mock
    private TotalScoreByCategoryMapper totalScoreByCategoryMapper;

    @InjectMocks
    private LeaderboardServiceImpl leaderboardService;

    @Test
    void showMyStats_IdNull_ReturnsStatsOfWatcher() {
        // given
        User watcher = createUser(ID_1);
        User otherUser = createUser(ID_2);

        GamesByCategoryResult categoryResult = createCategoryResult(10L, 100);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(watcher));
        when(userRepository.findAllByScore()).thenReturn(List.of(watcher, otherUser));
        when(gamesByCategoryResultRepository.findAllByUserId(1L))
                .thenReturn(List.of(categoryResult));

        // when
        MyStatsDto actual = leaderboardService.showMyStats(null, EMAIL);

        // then
        Assertions.assertEquals("user1", actual.getNickname());
        Assertions.assertEquals(500, actual.getTotalScore());
        Assertions.assertEquals(10, actual.getTotalGamesPlayed());
        Assertions.assertEquals(1, actual.getRank());
        Assertions.assertEquals(100, actual.getScoreByCategoryId().get(10L));
        Assertions.assertEquals(true, actual.isFlag());
    }

    @Test
    void showMyStats_OtherUserId_ReturnsStatsOfOtherUser() {
        // given
        User watcher = createUser(ID_1);
        User otherUser = createUser(ID_2);

        GamesByCategoryResult categoryResult = createCategoryResult(20L, 250);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(watcher));
        when(userRepository.findAllByScore()).thenReturn(List.of(watcher, otherUser));
        when(userRepository.findById(ID_2)).thenReturn(Optional.of(otherUser));
        when(gamesByCategoryResultRepository.findAllByUserId(ID_2))
                .thenReturn(List.of(categoryResult));

        // when
        MyStatsDto actual = leaderboardService.showMyStats(ID_2, EMAIL);

        // then
        Assertions.assertEquals("user2", actual.getNickname());
        Assertions.assertEquals(500, actual.getTotalScore());
        Assertions.assertEquals(10, actual.getTotalGamesPlayed());
        Assertions.assertEquals(2, actual.getRank());
        Assertions.assertEquals(250, actual.getScoreByCategoryId().get(20L));
        Assertions.assertEquals(false, actual.isFlag());
    }

    @Test
    void showMyStats_OtherUserDoesNotExist_ThrowsException() {
        // given
        User watcher = createUser(ID_1);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(watcher));
        when(userRepository.findAllByScore()).thenReturn(List.of(watcher));
        when(userRepository.findById(ID_2)).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> leaderboardService.showMyStats(ID_2, EMAIL));
    }

    @Test
    void showMyStats_WatcherDoesNotExist_ThrowsException() {
        // given
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> leaderboardService.showMyStats(null, EMAIL));
    }

    @Test
    void showPageOfStats_ReturnsPageWithCorrectRanksAndFlags() {
        // given
        User watcher = createUser(ID_1);
        User otherUser = createUser(ID_2);

        Pageable pageable = PageRequest.of(0, 2);

        Page<User> users = new PageImpl<>(List.of(watcher, otherUser), pageable, 2);

        TotalScoreStatsDto watcherDto = new TotalScoreStatsDto();
        watcherDto.setUserId(ID_1);

        TotalScoreStatsDto otherDto = new TotalScoreStatsDto();
        otherDto.setUserId(ID_2);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(watcher));

        when(userRepository.findAllByScore(pageable)).thenReturn(users);

        when(totalScoreMapper.toDto(watcher)).thenReturn(watcherDto);

        when(totalScoreMapper.toDto(otherUser)).thenReturn(otherDto);

        // when
        Page<TotalScoreStatsDto> actual = leaderboardService.showPageOfStats(EMAIL, pageable);

        // then
        Assertions.assertEquals(2, actual.getContent().size());
        Assertions.assertEquals(1, actual.getContent().get(0).getRank());
        Assertions.assertEquals(2, actual.getContent().get(1).getRank());
        Assertions.assertEquals(true, actual.getContent().get(0).isFlag());
        Assertions.assertEquals(false, actual.getContent().get(1).isFlag());
    }

    @Test
    void showPageOfStats_SecondPage_ReturnsCorrectRanks() {
        // given
        User watcher = createUser(ID_1);
        User user2 = createUser(ID_2);
        User user3 = createUser(ID_3);

        Pageable pageable = PageRequest.of(1, 2);

        Page<User> users = new PageImpl<>(List.of(user2, user3), pageable, 4);

        TotalScoreStatsDto user3Dto = new TotalScoreStatsDto();
        user3Dto.setUserId(ID_2);

        TotalScoreStatsDto user4Dto = new TotalScoreStatsDto();
        user4Dto.setUserId(ID_3);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(watcher));
        when(userRepository.findAllByScore(pageable)).thenReturn(users);
        when(totalScoreMapper.toDto(user2)).thenReturn(user3Dto);
        when(totalScoreMapper.toDto(user3)).thenReturn(user4Dto);

        // when
        Page<TotalScoreStatsDto> actual = leaderboardService.showPageOfStats(EMAIL, pageable);

        // then
        Assertions.assertEquals(3, actual.getContent().get(0).getRank());
        Assertions.assertEquals(4, actual.getContent().get(1).getRank());
        Assertions.assertEquals(false, actual.getContent().get(0).isFlag());
        Assertions.assertEquals(false, actual.getContent().get(1).isFlag());
    }

    @Test
    void showStatsOfCategory_ReturnsPageWithCorrectRanksAndFlags() {
        // given
        User watcher = createUser(ID_1);

        Pageable pageable = PageRequest.of(0, 2);

        GamesByCategoryResult result1 = createCategoryResult(10L, 200);

        GamesByCategoryResult result2 = createCategoryResult(10L, 150);

        Page<GamesByCategoryResult> results = new PageImpl<>(
                List.of(result1, result2), pageable, 2);

        TotalScoreByCategoryDto dto1 = new TotalScoreByCategoryDto();
        dto1.setUserId(ID_1);
        TotalScoreByCategoryDto dto2 = new TotalScoreByCategoryDto();
        dto2.setUserId(ID_2);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(watcher));

        when(gamesByCategoryResultRepository.findAllByCategoryId(10L, pageable))
                .thenReturn(results);
        when(totalScoreByCategoryMapper.toDto(result1)).thenReturn(dto1);
        when(totalScoreByCategoryMapper.toDto(result2)).thenReturn(dto2);

        // when
        Page<TotalScoreByCategoryDto> actual = leaderboardService.showStatsOfCategory(
                10L, EMAIL, pageable);

        // then
        Assertions.assertEquals(2, actual.getContent().size());
        Assertions.assertEquals(1, actual.getContent().get(0).getRank());
        Assertions.assertEquals(2, actual.getContent().get(1).getRank());
        Assertions.assertEquals(true, actual.getContent().get(0).isFlag());
        Assertions.assertEquals(false, actual.getContent().get(1).isFlag());
    }

    @Test
    void showStatsOfCategory_SecondPage_ReturnsCorrectRank() {
        // given
        User watcher = createUser(ID_1);
        User otherUser = createUser(ID_2);

        Pageable pageable = PageRequest.of(1, 1);

        GamesByCategoryResult result = createCategoryResult(10L, 150);
        Page<GamesByCategoryResult> results = new PageImpl<>(List.of(result), pageable, 2);

        TotalScoreByCategoryDto dto = new TotalScoreByCategoryDto();
        dto.setUserId(2L);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(watcher));
        when(gamesByCategoryResultRepository.findAllByCategoryId(10L, pageable))
                .thenReturn(results);
        when(totalScoreByCategoryMapper.toDto(result)).thenReturn(dto);

        // when
        Page<TotalScoreByCategoryDto> actual =
                leaderboardService.showStatsOfCategory(10L, EMAIL, pageable);

        // then
        Assertions.assertEquals(1, actual.getContent().size());
        Assertions.assertEquals(2, actual.getContent().get(0).getRank());
        Assertions.assertEquals(false, actual.getContent().get(0).isFlag());
    }

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setNickname("user" + id);
        user.setEmail(EMAIL);
        user.setScore(500);
        user.setTotalGamesPlayed(10);

        return user;
    }

    private GamesByCategoryResult createCategoryResult(
            Long categoryId,
            int score) {

        Category category = new Category();
        category.setId(categoryId);

        GamesByCategoryResult result =
                new GamesByCategoryResult();

        result.setCategory(category);
        result.setCategoryScore(score);

        return result;
    }
}