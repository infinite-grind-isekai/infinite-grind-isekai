package org.rpg.isekai.domain.battle;

import org.rpg.isekai.domain.item.Item;

import java.util.List;

public record Reward(
        int gold,
        List<Item> items
) {
    public Reward {
        items = List.copyOf(items);
    }

    public static Reward empty() {
        return new Reward(0, List.of());
    }
}
