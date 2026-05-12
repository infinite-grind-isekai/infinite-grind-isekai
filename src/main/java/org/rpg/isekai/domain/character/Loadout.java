package org.rpg.isekai.domain.character;

import lombok.Getter;

import org.rpg.isekai.domain.item.amorItem.ArmorItem;
import org.rpg.isekai.domain.item.Item;
import org.rpg.isekai.domain.item.weaponItem.WeaponItem;

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

    public boolean isEquipped(Item item) {
        if (item instanceof WeaponItem) return weaponSlot.isEquipped(item);
        if (item instanceof ArmorItem) return armorSlots.isEquipped(item);
        return false;
    }

    public void unequip(Item item) {
        if (item instanceof WeaponItem && weaponSlot.isEquipped(item)) {
            weaponSlot.unequip();
        } else if (item instanceof ArmorItem) {
            armorSlots.unequip(item);
        }
    }


}
