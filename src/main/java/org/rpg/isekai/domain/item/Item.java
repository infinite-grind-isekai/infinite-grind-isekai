package org.rpg.isekai.domain.item;

import lombok.Getter;
import lombok.Setter;
import org.rpg.isekai.domain.character.Character;

@Getter
@Setter
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
}
