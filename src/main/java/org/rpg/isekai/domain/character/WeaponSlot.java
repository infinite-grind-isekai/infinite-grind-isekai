package org.rpg.isekai.domain.character;

import lombok.NoArgsConstructor;
import org.rpg.isekai.domain.item.Item;
import org.rpg.isekai.domain.item.ItemType;
import org.rpg.isekai.domain.item.weaponItem.WeaponItem;

@NoArgsConstructor
public class WeaponSlot implements Slot{

    private WeaponItem weapon;

    public WeaponSlot(WeaponItem weapon) {
        if (weapon == null || !weapon.getType().equals(ItemType.WEAPON)) {
            throw new IllegalArgumentException("올바른 아이템을 장착해주세요.");
        }

        this.weapon = weapon;
    }

    @Override
    public Stat getStat() {
        if (weapon == null) return new Stat();
        return new Stat(weapon.getAttackPower(), weapon.getCritical(), 0, 0, 0);
    }

    @Override
    public void equip(Item item, Inventory inventory) {
        if (!(item instanceof WeaponItem)) {
            throw new IllegalArgumentException("무기 아이템이 아닌 경우 장착할 수 없습니다.");
        }
        if (weapon == null) {
            weapon = (WeaponItem) item;
            return;
        }
        inventory.add(weapon);
        weapon = (WeaponItem) item;
        return;
    }

    public boolean isEquipped(Item item) {
        return weapon != null && weapon.equals(item);
    }

    public void unequip() {
        this.weapon = null;
    }

    public WeaponItem getWeapon() {
        return weapon;
    }
}
