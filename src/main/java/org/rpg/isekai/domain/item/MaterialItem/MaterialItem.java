package org.rpg.isekai.domain.item.MaterialItem;

import org.rpg.isekai.domain.character.Character;
import org.rpg.isekai.domain.item.Item;
import org.rpg.isekai.domain.item.ItemType;

public class MaterialItem extends Item {

    public MaterialItem(String name, int price) {
        super(name, price, ItemType.MATERIAL);
    }

    @Override
    public void use(Character character) {
        System.out.println(name + "은(는) 제작 재료입니다.");
    }
}
