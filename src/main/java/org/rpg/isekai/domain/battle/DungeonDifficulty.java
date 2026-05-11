package org.rpg.isekai.domain.battle;

public enum DungeonDifficulty {
    EASY(0.8),
    NORMAL(1.0),
    HARD(1.3),
    NIGHTMARE(1.6);

    private final double rewardMultiplier;

    DungeonDifficulty(double rewardMultiplier) {
        this.rewardMultiplier = rewardMultiplier;
    }

    public double getRewardMultiplier() {
        return rewardMultiplier;
    }
}
