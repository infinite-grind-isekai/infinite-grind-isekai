package org.rpg.isekai.domain.item;

import org.rpg.isekai.domain.common.ItemType;
import org.rpg.isekai.domain.character.Character;

public class MaterialItem extends Item {

    public MaterialItem(String name, int price) {
        super(name, price, ItemType.MATERIAL);
    }

    @Override
    public void use(Character character) {
        System.out.println(name + "은(는) 제작 재료입니다.");
    }
}
