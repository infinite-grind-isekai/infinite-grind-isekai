package org.rpg.isekai.domain.item;

import org.rpg.isekai.domain.common.ItemType;
import org.rpg.isekai.domain.character.Character;

public abstract class Item {

    protected String name;
    protected int price;
    protected ItemType type;

    public Item(String name, int price, ItemType type) {
        this.name = name;
        this.price = price;
        this.type = type;
    }

    public abstract void use(Character character);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }
}
