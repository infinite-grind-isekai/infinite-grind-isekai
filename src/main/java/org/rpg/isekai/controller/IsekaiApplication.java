package org.rpg.isekai.controller;

import org.rpg.isekai.ioc.Container;
import org.rpg.isekai.ioc.Starter;

public class IsekaiApplication {

    public static void run(Class<?> primaryClass) {
        Container.getInstance().reset();
        Container.getInstance().get(Starter.class).start();
    }

}
