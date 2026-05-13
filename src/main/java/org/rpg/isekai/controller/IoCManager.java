package org.rpg.isekai.controller;

import org.rpg.isekai.ioc.Container;
import org.rpg.isekai.ioc.Starter;

import java.util.Comparator;
import java.util.List;

public class IoCManager {

    private final List<Manager> managers;
    private final List<Registerar> registerars;
    private final Starter starter;

    public IoCManager() {
        MonsterManager monsterManager = new MonsterManager();
        ItemManager itemManager = new ItemManager();
        DungeonManager dungeonManager = new DungeonManager(monsterManager);
        InitialManager initialManager = new InitialManager();

        List<Manager> tmpList = List.of(monsterManager, dungeonManager, itemManager, initialManager);
        managers    = tmpList.stream().sorted(Comparator.comparingInt(Manager::getOrder)).toList();
        registerars = tmpList.stream()
                .filter(c -> c instanceof Registerar)
                .map(c -> (Registerar) c)
                .toList();

        starter = new GameController(dungeonManager, itemManager, initialManager);
    }

    public void run() {
        init();
    }

    private void init() {
        for (Manager manager : managers) {
            if (manager.needPrepare()) {
                manager.prepare();
            }
        }

        for (Registerar registerar : registerars) {
            for (Object component : registerar.register()) {
                Container.getInstance().register(component.getClass(), component);
            }
        }

        Container.getInstance().register(Starter.class, starter);
    }
}
