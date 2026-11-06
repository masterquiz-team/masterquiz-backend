package quizz.dto.userachievement;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAchievementDto {
    private Long id;
    private Long userId;
    private Long achievementId;
    private String title;
    private String description;
    private String rarity;
    private String achievementImage;
    private LocalDateTime unlockedAt;
}
