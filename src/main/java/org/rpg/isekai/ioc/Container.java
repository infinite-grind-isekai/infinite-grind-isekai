package org.rpg.isekai.ioc;

import lombok.NoArgsConstructor;
import org.rpg.isekai.controller.IoCManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Container {
    private static Container INSTANCE;
    private final IoCManager iocManager = new IoCManager();
    private final Map<Class<?>, List<Object>> components = new HashMap<>();

    public static Container getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Container();
        }
        return INSTANCE;
    }

    public void reset() {
        iocManager.run();
    }

    public void register(Class<?> componentClass, Object component) {
        components.put(componentClass, List.of(component));
    }

    public <T> T get(Class<T> componentClass) {
        return (T) components.get(componentClass).get(0);
    }

}
