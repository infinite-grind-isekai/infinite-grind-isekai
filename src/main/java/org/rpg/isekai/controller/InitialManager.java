package org.rpg.isekai.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.rpg.isekai.domain.item.Item;
import org.rpg.isekai.domain.item.amorItem.LeatherArmor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class InitialManager implements Manager{

    private final List<Item> initialItems = new ArrayList<>();

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean needPrepare() {
        return true;
    }

    @Override
    public void prepare() {
        initialItems.add(new LeatherArmor());
    }

}
