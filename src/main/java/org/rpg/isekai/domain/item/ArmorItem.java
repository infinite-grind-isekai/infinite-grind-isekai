package org.rpg.isekai.domain.item;

import lombok.Getter;
import org.rpg.isekai.domain.character.Character;

@Getter
public class ArmorItem extends Item {
    private int defensePower;

    public ArmorItem(String name, int price, int defensePower) {
        super(name, price, ItemType.ARMOR);
        this.defensePower = defensePower;
    }

    @Override
    public void use(Character character) {
        character.getTotalStat().setDefense(character.getTotalStat().getDefense() + defensePower);
        System.out.println(name + " 장착! 방어력 +" + defensePower);
    }
}
