package org.rpg.isekai.domain.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.rpg.isekai.domain.item.weaponItem.WeaponType;

@Getter
@AllArgsConstructor
public enum JobKind {
    ARCHER("아처", WeaponType.BOW),
    GUNSLINGER("건슬링어", WeaponType.RANGED),
    MAGE("마법사", WeaponType.STAFF),
    WARRIOR("전사", WeaponType.RANGED);

    private String name;
    private WeaponType weaponType;

}
