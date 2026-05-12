package org.rpg.isekai.domain.item.AmorItem;

import lombok.Getter;
import org.rpg.isekai.domain.character.ArmorType;
import org.rpg.isekai.domain.character.Character;
import org.rpg.isekai.domain.item.Item;
import org.rpg.isekai.domain.item.ItemType;

@Getter
public class ArmorItem extends Item {
    private int defensePower;
    private ArmorType armorType;

    public ArmorItem(String name, int price, int defensePower, ArmorType armorType) {
        super(name, price, ItemType.ARMOR);
        this.defensePower = defensePower;
        this.armorType = armorType;
    }

    @Override
    public void use(Character character) {
        character.getTotalStat().setDefense(character.getTotalStat().getDefense() + defensePower);
        System.out.println(name + " 장착! 방어력 +" + defensePower);
    }
}
