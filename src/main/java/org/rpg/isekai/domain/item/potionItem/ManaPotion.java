package org.rpg.isekai.domain.item.potionItem;

import org.rpg.isekai.domain.character.Character;

public class ManaPotion extends PotionItem {
    public ManaPotion() {
        super("마나 포션", 50, 30);
    }

    @Override
    public void use(Character character) {
        character.getTotalStat().setMp(character.getTotalStat().getMp() + getHealAmount());
        System.out.println(getName() + " 사용! MP +" + getHealAmount());
    }
}
