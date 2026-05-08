package org.rpg.isekai.domain.system;

import lombok.NoArgsConstructor;

import java.util.HashSet;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class UsernameValidator {

    private static final HashSet<String> usernames = new HashSet<>();
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 8;

    public static boolean isValid(String username) {
        return username.length() >= MIN_LENGTH && username.length() <= MAX_LENGTH && !usernames.contains(username);
    }

    public static void register(String username) {
        if (isValid(username)) {
            usernames.add(username);
            return;
        };
        throw new IllegalArgumentException("적절하지 않은 유저 이름입니다.");
    }

}
