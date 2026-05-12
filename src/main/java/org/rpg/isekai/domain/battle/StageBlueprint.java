package org.rpg.isekai.domain.battle;

import org.rpg.isekai.domain.monster.Monster;

import java.util.List;
import java.util.function.Supplier;

public record StageBlueprint(List<Supplier<Monster>> monsterFactories) {

    public StageBlueprint {
        if (monsterFactories == null || monsterFactories.isEmpty()) {
            throw new IllegalArgumentException("스테이지에는 최소 한 마리 이상의 몬스터가 필요합니다.");
        }
        monsterFactories = List.copyOf(monsterFactories);
    }

    public List<Monster> createMonsters() {
        return monsterFactories.stream()
                .map(Supplier::get)
                .toList();
    }
}
