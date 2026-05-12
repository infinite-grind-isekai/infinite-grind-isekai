package org.rpg.isekai.domain.item.potionItem;

import lombok.Getter;
import org.rpg.isekai.domain.character.Character;
import org.rpg.isekai.domain.item.Item;
import org.rpg.isekai.domain.item.ItemType;

@Getter
public class PotionItem extends Item {
    private int healAmount;

    public PotionItem(String name, int price, int healAmount) {
        super(name, price, ItemType.POTION);
        this.healAmount = healAmount;
    }

    @Override
    public void use(Character character) {
        character.getTotalStat().setHp(character.getTotalStat().getHp() + healAmount);
        System.out.println(name + " 사용! HP +" + healAmount);
    }
}
