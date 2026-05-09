package org.rpg.isekai.domain.item;

import org.rpg.isekai.domain.common.ItemType;
import org.rpg.isekai.domain.character.Character;

public class WeaponItem extends Item {

    private int attackPower;

    public WeaponItem(String name, int price, int attackPower) {
        super(name, price, ItemType.WEAPON);
        this.attackPower = attackPower;
    }

    @Override
    public void use(Character character) {
        character.setAttackPower(character.getAttackPower() + attackPower);
        System.out.println(name + " 장착! 공격력 +" + attackPower);
    }
}
