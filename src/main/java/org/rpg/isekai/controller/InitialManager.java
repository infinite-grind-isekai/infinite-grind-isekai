package org.rpg.isekai.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.rpg.isekai.domain.item.Item;
import org.rpg.isekai.domain.item.amorItem.LeatherArmor;
import org.rpg.isekai.domain.item.potionItem.ManaPotion;
import org.rpg.isekai.domain.item.weaponItem.*;
import org.rpg.isekai.domain.job.Job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class InitialManager implements Manager{

    private final List<Item> initialItems = new ArrayList<>();
    private final Map<WeaponType, WeaponItem> initialWeapons
            = Map.of(
                    WeaponType.MELEE, new IronSword(),
                    WeaponType.RANGED, new ThunderstrikeGun(),
                    WeaponType.STAFF, new MagicStaff(),
                    WeaponType.BOW, new ShadowBow()
            );

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean needPrepare() {
        return true;
    }

    @Override
    public void prepare() {
        initialItems.add(new LeatherArmor());
        initialItems.add(new ManaPotion());
    }

    public void prepareForJob(Job job) {
        initialItems.add(initialWeapons.get(job.getJobKind().getWeaponType()));
    }

}
