package quizz.model;

public enum Avatar {
    AVATAR_1("avatar-1"),
    AVATAR_2("avatar-2"),
    AVATAR_3("avatar-3"),
    AVATAR_4("avatar-4"),
    AVATAR_5("avatar-5");
    private final String id;

    Avatar(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
