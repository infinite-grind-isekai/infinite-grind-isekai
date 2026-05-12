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
    public void equip(Item item) {
        if (item instanceof WeaponItem) {
            weapon = (WeaponItem) item;
            return;
        }
        throw new IllegalArgumentException("무기 아이템이 아닌 경우 장착할 수 없습니다.");
    }
}
