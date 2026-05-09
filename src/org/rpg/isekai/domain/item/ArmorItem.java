package org.rpg.isekai.domain.item;

import org.rpg.isekai.domain.common.ItemType;
import org.rpg.isekai.domain.character.Character;

public class ArmorItem extends Item {

    private int defensePower;

    public ArmorItem(String name, int price, int defensePower) {
        super(name, price, ItemType.ARMOR);
        this.defensePower = defensePower;
    }

    @Override
    public void use(Character character) {
        character.setDefensePower(character.getDefensePower() + defensePower);
        System.out.println(name + " 장착! 방어력 +" + defensePower);
    }
}
