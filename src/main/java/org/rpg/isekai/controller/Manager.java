package org.rpg.isekai.controller;

import java.util.List;

public interface Manager {
    int getOrder();
    boolean needPrepare();
    void prepare();
}
