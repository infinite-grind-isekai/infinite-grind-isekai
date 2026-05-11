package org.rpg.isekai.domain.character;

import lombok.Getter;
import org.rpg.isekai.domain.item.Item;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Inventory {
    private final List<Item> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    public void add(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("아이템은 null일 수 없습니다.");
        }
        items.add(item);
    }

    public void addAll(List<Item> items) {
        if (items == null) {
            throw new IllegalArgumentException("아이템 목록은 null일 수 없습니다.");
        }
        items.forEach(this::add);
    }

    public boolean remove(Item item) {
        return items.remove(item);
    }

    public boolean contains(Item item) {
        return items.contains(item);
    }

    public List<Item> getItems() {
        return List.copyOf(items);
    }
}
