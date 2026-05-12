package org.rpg.isekai.domain.character;

import org.rpg.isekai.domain.item.Item;

public interface Slot {
    Stat getStat();
    void equip(Item item);
}
