package org.rpg.isekai.controller;

import lombok.Getter;
import org.rpg.isekai.domain.item.Item;
import org.rpg.isekai.domain.item.amorItem.*;
import org.rpg.isekai.domain.item.materialItem.*;
import org.rpg.isekai.domain.item.potionItem.*;
import org.rpg.isekai.domain.item.weaponItem.*;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ItemManager implements Manager{
    private final List<Item> storeItems;

    public ItemManager() {
        storeItems = List.of(
                // 포션류
                new HealthPotion(),
                new ManaPotion(),
                new EnergyDrink(),
                new HeartOfDragon(),
                new PhoenixFeather(),
                // 무기류
                new IronSword(),
                new MagicStaff(),
                new DoomBringer(),
                new ShadowBow(),
                new ThunderstrikeGun(),
                // 방어구류
                new LeatherArmor(),
                new IronPlate(),
                new TitanArmor(),
                new FrostguardShield(),
                new DragonScaleMail(),

                new Circlet(),
                new Greaves(),
                new Gauntlet(),

                // 재료류
                new SlimeJelly(),
                new SkeletonBone(),
                new GoblinEar(),
                new OrcTooth(),
                new DragonScale()
        );
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean needPrepare() {
        return false;
    }

    @Override
    public void prepare() {
    }
}
