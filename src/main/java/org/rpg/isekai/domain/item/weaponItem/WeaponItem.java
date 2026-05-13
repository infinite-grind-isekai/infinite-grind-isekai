package org.rpg.isekai.domain.item.weaponItem;

import lombok.Getter;
import org.rpg.isekai.domain.character.Character;
import org.rpg.isekai.domain.item.Item;
import org.rpg.isekai.domain.item.ItemType;

@Getter
public class WeaponItem extends Item {
    private int attackPower;
    private double critical;
    private WeaponType weaponType;

    public WeaponItem(String name, int price, int attackPower, double critical, WeaponType weaponType) {
        super(name, price, ItemType.WEAPON);
        this.attackPower = attackPower;
        this.critical = critical;
        this.weaponType = weaponType;
    }

    @Override
    public void use(Character character) {
        if (character.getLoadout().isEquipped(this)) {
            throw new IllegalArgumentException("이미 장착한 아이템 입니다.");
        }

        if (!character.getJob().getJobKind().getWeaponType().equals(this.getWeaponType())) {
            throw new IllegalArgumentException("직업에 알맞은 아이템을 장착해주세요.");
        }
        character.getLoadout().equip(this, character.getInventory());
    }
}
