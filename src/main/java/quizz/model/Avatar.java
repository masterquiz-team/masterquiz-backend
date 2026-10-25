package quizz.model;

import lombok.Getter;

@Getter
public enum Avatar {
    AVATAR_1("/avatars/avatar-1.jpg"),
    AVATAR_2("/avatars/avatar-2.jpg"),
    AVATAR_3("/avatars/avatar-3.jpg"),
    AVATAR_4("/avatars/avatar-4.jpg"),
    AVATAR_5("/avatars/avatar-5.jpg");
    private final String id;

    Avatar(String id) {
        this.id = id;
    }
}
