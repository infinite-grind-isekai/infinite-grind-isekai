package org.rpg.isekai.domain.character;

import lombok.NoArgsConstructor;
import org.rpg.isekai.domain.item.amorItem.ArmorItem;
import org.rpg.isekai.domain.item.Item;
import org.rpg.isekai.domain.item.amorItem.ArmorType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class ArmorSlot implements Slot {

    private Map<ArmorType, ArmorItem> armorItems = new HashMap<>();

    @Override
    public void equip(Item item, Inventory inventory) {
        if (!(item instanceof ArmorItem armorItem)) {
            throw new IllegalArgumentException("장비 아이템이 아닌 경우 장착할 수 없습니다.");
        }
        if (armorItems.containsKey(armorItem.getArmorType())) {
            inventory.add(armorItems.get(armorItem.getArmorType()));
        }
        armorItems.put(armorItem.getArmorType(), armorItem);
    }

    @Override
    public Stat getStat() {
        Stat stat = new Stat();
        for (ArmorItem armorItem : armorItems.values()) {
            stat.setDefense(stat.getDefense() + armorItem.getDefensePower());
        }
        return stat;
    }

    public boolean isEquipped(Item item) {
        return armorItems.containsValue(item);
    }

    public void unequip(Item item) {
        if (item instanceof ArmorItem armorItem) {
            armorItems.remove(armorItem.getArmorType(), armorItem);
        }
    }

    public Map<ArmorType, ArmorItem> getArmorItems() {
        return Collections.unmodifiableMap(armorItems);
    }
}
