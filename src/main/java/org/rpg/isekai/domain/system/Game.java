package org.rpg.isekai.domain.system;

import lombok.Getter;

@Getter
public non-sealed class Game implements Endable {

    private GameStatus gameStatus;

    @Override
    public void end() {
        gameStatus = GameStatus.END;
    }

    @Override
    public boolean isEnd() {
        return gameStatus == GameStatus.END;
    }
}
