package org.rpg.isekai.domain.system;

import lombok.Getter;

@Getter
public enum GameStatus {
    START(true), RUNNING(true), END(false), QUIT(false);

    private final boolean isRunning;

    GameStatus(boolean isRunning) {
        this.isRunning = isRunning;
    }

}
