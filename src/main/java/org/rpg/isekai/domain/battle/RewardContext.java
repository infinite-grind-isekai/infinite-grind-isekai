package org.rpg.isekai.domain.battle;

import org.rpg.isekai.domain.item.Item;

import java.util.ArrayList;
import java.util.List;

public class RewardContext {
    private int totalGold;
    private final List<Item> collectedItems = new ArrayList<>();

    public void collect(Reward reward) {
        totalGold += reward.gold();
        collectedItems.addAll(reward.items());
    }

    public Reward peek() {
        return new Reward(totalGold, List.copyOf(collectedItems));
    }

    public Reward claim() {
        Reward result = new Reward(totalGold, List.copyOf(collectedItems));
        clear();
        return result;
    }

    public void clear() {
        totalGold = 0;
        collectedItems.clear();
    }
}
