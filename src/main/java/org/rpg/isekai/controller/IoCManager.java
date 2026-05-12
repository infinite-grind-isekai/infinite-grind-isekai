package org.rpg.isekai.controller;

import org.rpg.isekai.ioc.Container;

import java.util.Comparator;
import java.util.List;

public class IoCManager {

    private final List<Manager> managers;
    private final List<Registerar> registerars;

    public IoCManager() {
        List<Manager> tmpList;
        MonsterManager monsterManager = new MonsterManager();
        tmpList =  List.of(
                monsterManager,
                new DungeonManager(monsterManager)
        );
        managers = tmpList.stream().sorted(Comparator.comparingInt(Manager::getOrder)).toList();
        registerars = tmpList.stream().filter(c -> c instanceof Registerar).map(c -> (Registerar) c).toList();
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
            for (Object component: registerar.register()) {
                Container.getInstance().register(component.getClass(), component);
            }
        }
    }
}
