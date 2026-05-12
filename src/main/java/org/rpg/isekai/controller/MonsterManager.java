package org.rpg.isekai.controller;

import org.rpg.isekai.domain.monster.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class MonsterManager implements Manager {

    private final Map<Class<? extends Monster>, Supplier<Monster>> factories = new HashMap<>();

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public boolean needPrepare() {
        return true;
    }

    @Override
    public void prepare() {
        factories.put(Slime.class, Slime::new);
        factories.put(Skeleton.class, Skeleton::new);
        factories.put(Goblin.class, Goblin::new);
        factories.put(Orc.class, Orc::new);
        factories.put(AncientDragon.class, AncientDragon::new);
    }

    public Supplier<Monster> getFactory(Class<? extends Monster> type) {
        Supplier<Monster> factory = factories.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("등록되지 않은 몬스터 타입입니다: " + type.getSimpleName());
        }
        return factory;
    }

}
