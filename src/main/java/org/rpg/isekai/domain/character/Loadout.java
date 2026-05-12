package org.rpg.isekai.domain.character;

import lombok.Getter;
import org.rpg.isekai.domain.item.ArmorItem;
import org.rpg.isekai.domain.item.Item;
import org.rpg.isekai.domain.item.WeaponItem;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Loadout {
    private ArmorSlot armorSlots;
    private WeaponSlot weaponSlot;

    public Loadout() {
        this.armorSlots = new ArmorSlot();
        this.weaponSlot = new WeaponSlot();
    }

    public Stat getItemsStat() {
        return armorSlots.getStat().plus(weaponSlot.getStat());
    }

    public void equip(Item item) {
        if (item instanceof WeaponItem) {
            weaponSlot = new WeaponSlot((WeaponItem) item);
            return;
        }

        if (item instanceof ArmorItem) {
            armorSlots.equip(item);
        }
    }


}
