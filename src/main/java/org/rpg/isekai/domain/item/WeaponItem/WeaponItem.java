package org.rpg.isekai.domain.item.WeaponItem;

import lombok.Getter;
import org.rpg.isekai.domain.character.Character;
import org.rpg.isekai.domain.item.Item;
import org.rpg.isekai.domain.item.ItemType;

@Getter
public class WeaponItem extends Item {
    private int attackPower;

    public WeaponItem(String name, int price, int attackPower) {
        super(name, price, ItemType.WEAPON);
        this.attackPower = attackPower;
    }

    @Override
    public void use(Character character) {
        character.getTotalStat().setPower(character.getTotalStat().getPower() + attackPower);
        System.out.println(name + " 장착! 공격력 +" + attackPower);
    }
}
