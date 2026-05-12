package org.rpg.isekai.controller;

import java.util.List;

public class GameManger implements Manager{
    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public boolean needPrepare() {
        return false;
    }

    @Override
    public void prepare() {

    }

}
