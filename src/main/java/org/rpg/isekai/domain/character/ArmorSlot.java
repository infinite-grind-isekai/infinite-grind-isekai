package org.rpg.isekai.domain.character;

import lombok.NoArgsConstructor;
import org.rpg.isekai.domain.item.ArmorItem;
import org.rpg.isekai.domain.item.Item;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class ArmorSlot implements Slot{
    private Map<ArmorType, ArmorItem> armorItems = Map.of();

    public void equip(Item item) {
        if (item instanceof ArmorItem) {
            ArmorItem armorItem = (ArmorItem) item;
            armorItems.put(armorItem.getArmorType(), armorItem);
            return;
        }
        throw new IllegalArgumentException("장비 아이템이 아닌 경우 장착할 수 없습니다.");
    }

    public Stat getStat() {
        Stat stat = new Stat();
        for (ArmorItem armorItem : armorItems.values()) {
            stat.setHp(stat.getDefense() + armorItem.getDefensePower());
        }
        return stat;
    }

}
