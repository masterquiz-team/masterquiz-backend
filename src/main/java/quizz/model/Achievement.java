package quizz.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "achievements")
@Getter
@Setter
@SQLDelete(sql = "UPDATE achievements SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private String achievementImage;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Achievement that = (Achievement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
