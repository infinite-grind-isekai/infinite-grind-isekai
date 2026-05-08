package org.rpg.isekai.domain.item;

import org.rpg.isekai.domain.common.ItemType;
import org.rpg.isekai.domain.character.Character;

public class PotionItem extends Item {
    private int healAmount;

    public PotionItem(String name, int price, int healAmount) {
        super(name, price, ItemType.POTION);
        this.healAmount = healAmount;
    }

    @Override
    public void use(Character character) {
        character.setHp(character.getHp() + healAmount);
        System.out.println(name + " 사용! HP +" + healAmount);
    }
}
