package quizz.model;

import lombok.Getter;

@Getter
public enum Avatar {
    AVATAR_1("/avatars/avatar-1.png"),
    AVATAR_2("/avatars/avatar-2.png"),
    AVATAR_3("/avatars/avatar-3.png"),
    AVATAR_4("/avatars/avatar-4.png"),
    AVATAR_5("/avatars/avatar-5.png"),
    AVATAR_6("/avatars/avatar-6.png"),
    AVATAR_7("/avatars/avatar-7.png"),
    AVATAR_8("/avatars/avatar-8.png");
    private final String id;

    Avatar(String id) {
        this.id = id;
    }
}
