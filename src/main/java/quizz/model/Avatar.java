package quizz.model;

import lombok.Getter;

@Getter
public enum Avatar {
    AVATAR_1("/avatars/avatar-1.png", "avatar-1"),
    AVATAR_2("/avatars/avatar-2.png", "avatar-2"),
    AVATAR_3("/avatars/avatar-3.png", "avatar-3"),
    AVATAR_4("/avatars/avatar-4.png", "avatar-4"),
    AVATAR_5("/avatars/avatar-5.png", "avatar-5"),
    AVATAR_6("/avatars/avatar-6.png", "avatar-6"),
    AVATAR_7("/avatars/avatar-7.png", "avatar-7"),
    AVATAR_8("/avatars/avatar-8.png", "avatar-8");
    private final String id;
    private final String name;

    Avatar(String id, String name) {
        this.id = id;
        this.name = name();
    }
}
