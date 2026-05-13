package org.rpg.isekai.view;

import org.rpg.isekai.domain.battle.Dungeon;
import org.rpg.isekai.domain.battle.DungeonKind;
import org.rpg.isekai.domain.character.Character;
import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.item.Item;
import org.rpg.isekai.domain.item.amorItem.ArmorItem;
import org.rpg.isekai.domain.item.amorItem.ArmorType;
import org.rpg.isekai.domain.item.weaponItem.WeaponItem;
import org.rpg.isekai.domain.skill.ActiveSkill;
import org.rpg.isekai.domain.skill.Skill;

import java.util.List;
import java.util.Map;

public class GameMenuView {

    private static final int BAR_WIDTH = 20;

    // ── 메인 메뉴 ───────────────────────────────────────────────────────────────

    public static int showMainMenu(Character ch) {
        while (true) {
            ConsoleUtils.clear();
            printStatusHeader(ch);
            System.out.println("     ╠═══════════════════════════════════════════════════════╣");
            System.out.println("     ║                                                       ║");
            System.out.println("     ║    1.  내 정보 보기                                   ║");
            System.out.println("     ║    2.  인벤토리                                       ║");
            System.out.println("     ║    3.  던전 입장                                      ║");
            System.out.println("     ║    4.  상점 입장                                      ║");
            System.out.println("     ║    5.  장착 아이템                                    ║");
            System.out.println("     ║                                                       ║");
            System.out.println("     ║    0.  게임 종료                                      ║");
            System.out.println("     ║                                                       ║");
            System.out.println("     ╚═══════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.print("     선택 > ");

            String input = ConsoleUtils.SCANNER.nextLine().trim();
            switch (input) {
                case "1", "2", "3", "4", "5", "0" -> { return Integer.parseInt(input); }
                default -> {
                    System.out.println("     [ ! ] 올바른 번호를 입력하세요.");
                    ConsoleUtils.sleep(700);
                }
            }
        }
    }

    // ── 내 정보 ─────────────────────────────────────────────────────────────────

    public static void showCharacterInfo(Character ch) {
        ConsoleUtils.clear();
        Stat total = ch.getTotalStat();

        System.out.println();
        System.out.println("     ┌───────────────────────────────────────────────────────┐");
        System.out.println("     │                   [ 소 환 자 정 보 ]                  │");
        System.out.println("     ├───────────────────────────────────────────────────────┤");
        System.out.printf( "     │   이름     :  %-38s│%n", ch.getName());
        System.out.printf( "     │   직업     :  %-38s│%n", ch.getJob() != null ? ch.getJob().getName() : "없음");
        System.out.printf( "     │   레벨     :  %-38s│%n", ch.getLevel());
        System.out.println("     ├───────────────────────────────────────────────────────┤");
        System.out.println("     │   [ 스탯 ]                                            │");
        System.out.printf( "     │   공격력   :  %-38s│%n", total.getPower());
        System.out.printf( "     │   크리티컬   :  %-38s│%n", total.getCritical());
        System.out.printf( "     │   방어력   :  %-38s│%n", total.getDefense());
        System.out.printf( "     │   HP       :  %3d / %3d  [%s]│%n",
                ch.getCurrentHp(), total.getHp(), bar(ch.getCurrentHp(), total.getHp()));
        System.out.printf( "     │   MP       :  %3d / %3d  [%s]│%n",
                ch.getCurrentMp(), total.getMp(), bar(ch.getCurrentMp(), total.getMp()));
        System.out.println("     ├───────────────────────────────────────────────────────┤");
        System.out.println("     │   [ 스킬 ]                                            │");

        List<Skill> skills = ch.getSkills();
        if (skills.isEmpty()) {
            System.out.println("     │   습득한 스킬이 없습니다.                             │");
        } else {
            for (Skill skill : skills) {
                String dmgInfo = (skill instanceof ActiveSkill a) ? "DMG " + a.getDamage() : "패시브";
                System.out.printf("     │   %-16s  MP %3d   %-22s│%n",
                        skill.getName(), skill.getMpCost(), dmgInfo);
            }
        }

        System.out.println("     ├───────────────────────────────────────────────────────┤");
        System.out.println("     ├───────────────────────────────────────────────────────┤");
        System.out.printf( "     │   Gold     :  %-38s│%n", ch.getGold() + " G");
        System.out.printf( "     │   EXP      :  [%s]  %4d / %4d        │%n",
                bar(ch.getCurrentExp(), ch.expToNextLevel()),
                ch.getCurrentExp(), ch.expToNextLevel());
        System.out.println("     └───────────────────────────────────────────────────────┘");
        System.out.println();
        System.out.print("     [ ENTER 를 눌러 돌아가기 ]");
        ConsoleUtils.SCANNER.nextLine();
    }

    // ── 장착 아이템 ─────────────────────────────────────────────────────────────

    public static void showEquipment(Character ch) {
        ConsoleUtils.clear();
        WeaponItem weapon = ch.getLoadout().getWeaponSlot().getWeapon();
        Map<ArmorType, ArmorItem> armors = ch.getLoadout().getArmorSlots().getArmorItems();

        System.out.println();
        System.out.println("     ┌───────────────────────────────────────────────────────┐");
        System.out.println("     │                 [ 장 착 아 이 템 ]                    │");
        System.out.println("     ├───────────────────────────────────────────────────────┤");
        System.out.println("     │   [ 무  기 ]                                          │");

        if (weapon == null) {
            System.out.println("     │     (미착용)                                          │");
        } else {
            System.out.printf("     │     %-14s  공격력 +%-3d  크리티컬 +%-4.0f%%         │%n",
                    weapon.getName(), weapon.getAttackPower(), weapon.getCritical());
        }

        System.out.println("     ├───────────────────────────────────────────────────────┤");
        System.out.println("     │   [ 방 어 구 ]                                        │");

        printArmorRow("머리", ArmorType.HEAD, armors);
        printArmorRow("몸통", ArmorType.CHEST, armors);
        printArmorRow("장갑", ArmorType.HAND, armors);
        printArmorRow("신발", ArmorType.FEET, armors);
        printArmorRow("방패", ArmorType.SHIELD, armors);

        System.out.println("     └───────────────────────────────────────────────────────┘");
        System.out.println();
        System.out.print("     [ ENTER 를 눌러 돌아가기 ]");
        ConsoleUtils.SCANNER.nextLine();
    }

    private static void printArmorRow(String label, ArmorType type, Map<ArmorType, ArmorItem> armors) {
        ArmorItem armor = armors.get(type);
        if (armor == null) {
            System.out.printf("     │   %-4s  :  (미착용)                                  │%n", label);
        } else {
            System.out.printf("     │   %-4s  :  %-16s  방어력 +%-3d              │%n",
                    label, armor.getName(), armor.getDefensePower());
        }
    }

    // ── 인벤토리 ────────────────────────────────────────────────────────────────

    public static int showInventory(Character ch) {
        while (true) {
            ConsoleUtils.clear();
            List<Item> items = ch.getInventory().getItems();

            System.out.println();
            System.out.println("     ┌───────────────────────────────────────────────────────┐");
            System.out.println("     │                    [ 인 벤 토 리 ]                    │");
            System.out.println("     ├──────┬────────────────────────┬────────────┬──────────┤");
            System.out.println("     │  No  │  아이템 이름           │  종류      │  가격    │");
            System.out.println("     ├──────┼────────────────────────┼────────────┼──────────┤");

            if (items.isEmpty()) {
                System.out.println("     │               보유한 아이템이 없습니다.               │");
            } else {
                for (int i = 0; i < items.size(); i++) {
                    Item item = items.get(i);
                    System.out.printf("     │  %-4d│  %-22s  │  %-8s  │  %4d G  │%n",
                            i + 1, item.getName(), item.getType().name(), item.getPrice());
                }
            }

            System.out.println("     ├──────┴────────────────────────┴────────────┴──────────┤");
            System.out.println("     │    0.  돌아가기                                        │");
            System.out.println("     └───────────────────────────────────────────────────────┘");
            System.out.println();
            System.out.print("     사용할 아이템 번호 > ");

            String input = ConsoleUtils.SCANNER.nextLine().trim();
            if (input.equals("0")) return -1;

            try {
                int idx = Integer.parseInt(input) - 1;
                if (idx >= 0 && idx < items.size()) return idx;
            } catch (NumberFormatException ignored) {}

            System.out.println("     [ ! ] 올바른 번호를 입력하세요.");
            ConsoleUtils.sleep(700);
        }
    }



    // ── 던전 목록 ───────────────────────────────────────────────────────────────

    public static DungeonKind showDungeonList(Map<DungeonKind, Dungeon> dungeons) {
        DungeonKind[] kinds = DungeonKind.values();

        while (true) {
            ConsoleUtils.clear();
            System.out.println();
            System.out.println("     ┌───────────────────────────────────────────────────────┐");
            System.out.println("     │                   [ 던 전 목 록 ]                     │");
            System.out.println("     ├──────┬──────────────────────┬────────────┬────────────┤");
            System.out.println("     │  No  │  던전 이름           │  난이도    │  스테이지  │");
            System.out.println("     ├──────┼──────────────────────┼────────────┼────────────┤");

            for (int i = 0; i < kinds.length; i++) {
                DungeonKind kind = kinds[i];
                Dungeon dungeon  = dungeons.get(kind);
                int stageCount   = dungeon != null ? dungeon.getStages().size() : 0;
                System.out.printf("     │  %-4d│  %-20s│  %-10s│  %2d 스테이지│%n",
                        i + 1, kind.getName(), kind.getDifficulty().name(), stageCount);
            }

            System.out.println("     ├──────┴──────────────────────┴────────────┴────────────┤");
            System.out.println("     │    0.  돌아가기                                        │");
            System.out.println("     └────────────────────────────────────────────────────────┘");
            System.out.println();
            System.out.print("     선택 > ");

            String input = ConsoleUtils.SCANNER.nextLine().trim();

            if (input.equals("0")) return null;

            try {
                int idx = Integer.parseInt(input) - 1;
                if (idx >= 0 && idx < kinds.length) return kinds[idx];
            } catch (NumberFormatException ignored) {}

            System.out.println("     [ ! ] 올바른 번호를 입력하세요.");
            ConsoleUtils.sleep(700);
        }
    }

    // ── 상점 목록 ───────────────────────────────────────────────────────────────

    public static int showStoreMenu(Character ch) {
        while (true) {
            ConsoleUtils.clear();
            printStatusHeader(ch);
            System.out.println("     ┌───────────────────────────────────────────────────────┐");
            System.out.println("     │                    [ 마 을 상 점 ]                    │");
            System.out.println("     ├───────────────────────────────────────────────────────┤");
            System.out.println("     │                                                       │");
            System.out.println("     │    1.  아이템 구매                                    │");
            System.out.println("     │    2.  아이템 판매                                    │");
            System.out.println("     │                                                       │");
            System.out.println("     │    0.  돌아가기                                       │");
            System.out.println("     │                                                       │");
            System.out.println("     └───────────────────────────────────────────────────────┘");
            System.out.println();
            System.out.print("     선택 > ");

            String input = ConsoleUtils.SCANNER.nextLine().trim();
            switch (input) {
                case "1", "2", "0" -> { return Integer.parseInt(input); }
                default -> {
                    System.out.println("     [ ! ] 올바른 번호를 입력하세요.");
                    ConsoleUtils.sleep(700);
                }
            }
        }
    }

    public static Item showItemStore(Character ch, List<Item> storeItems) {
        while (true) {
            ConsoleUtils.clear();
            printStatusHeader(ch);
            System.out.println("     ┌───────────────────────────────────────────────────────┐");
            System.out.println("     │               [ 마 을 상 점 - 구 매 ]                 │");
            System.out.println("     ├──────┬────────────────────────┬────────────┬──────────┤");
            System.out.println("     │  No  │  아이템 이름           │  종류      │  가격    │");
            System.out.println("     ├──────┼────────────────────────┼────────────┼──────────┤");

            for (int i = 0; i < storeItems.size(); i++) {
                Item item = storeItems.get(i);
                System.out.printf("     │  %-4d│  %-22s  │  %-8s  │  %4d G  │%n",
                        i + 1, item.getName(), item.getType().name(), item.getPrice());
            }

            System.out.println("     ├──────┴────────────────────────┴────────────┴──────────┤");
            System.out.println("     │    0.  돌아가기                                        │");
            System.out.println("     └───────────────────────────────────────────────────────┘");
            System.out.println();
            System.out.print("     구매할 아이템 번호 > ");

            String input = ConsoleUtils.SCANNER.nextLine().trim();

            if (input.equals("0")) return null;

            try {
                int idx = Integer.parseInt(input) - 1;
                if (idx >= 0 && idx < storeItems.size()) {
                    return storeItems.get(idx);
                }
            } catch (NumberFormatException ignored) {}

            System.out.println("     [ ! ] 올바른 번호를 입력하세요.");
            ConsoleUtils.sleep(700);
        }
    }

    public static Item showSellMenu(Character ch) {
        while (true) {
            ConsoleUtils.clear();
            printStatusHeader(ch);
            List<Item> items = ch.getInventory().getItems();

            System.out.println("     ┌───────────────────────────────────────────────────────┐");
            System.out.println("     │               [ 마 을 상 점 - 판 매 ]                 │");
            System.out.println("     ├──────┬────────────────────────┬────────────┬──────────┤");
            System.out.println("     │  No  │  아이템 이름           │  종류      │  판매가  │");
            System.out.println("     ├──────┼────────────────────────┼────────────┼──────────┤");

            if (items.isEmpty()) {
                System.out.println("     │               보유한 아이템이 없습니다.               │");
            } else {
                for (int i = 0; i < items.size(); i++) {
                    Item item = items.get(i);
                    // 판매가는 원가의 50%로 설정 (또는 정책에 따라 변경 가능)
                    int sellPrice = item.getPrice() / 2;
                    System.out.printf("     │  %-4d│  %-22s  │  %-8s  │  %4d G  │%n",
                            i + 1, item.getName(), item.getType().name(), sellPrice);
                }
            }

            System.out.println("     ├──────┴────────────────────────┴────────────┴──────────┤");
            System.out.println("     │    0.  돌아가기                                        │");
            System.out.println("     └───────────────────────────────────────────────────────┘");
            System.out.println();
            System.out.print("     판매할 아이템 번호 > ");

            String input = ConsoleUtils.SCANNER.nextLine().trim();

            if (input.equals("0")) return null;

            try {
                int idx = Integer.parseInt(input) - 1;
                if (idx >= 0 && idx < items.size()) {
                    return items.get(idx);
                }
            } catch (NumberFormatException ignored) {}

            System.out.println("     [ ! ] 올바른 번호를 입력하세요.");
            ConsoleUtils.sleep(700);
        }
    }


    // ── 공통 렌더링 헬퍼 ────────────────────────────────────────────────────────

    private static void printStatusHeader(Character ch) {
        Stat total   = ch.getTotalStat();
        String job   = ch.getJob() != null ? ch.getJob().getName() : "직업 없음";

        System.out.println();
        System.out.println("     ╔═══════════════════════════════════════════════════════╗");
        System.out.printf( "     ║   %s   직업: %s   Lv.%d%n", ch.getName(), job, ch.getLevel());
        System.out.printf( "     ║   HP  [%s]  %3d / %3d%n",
                bar(ch.getCurrentHp(), total.getHp()), ch.getCurrentHp(), total.getHp());
        System.out.printf( "     ║   MP  [%s]  %3d / %3d%n",
                bar(ch.getCurrentMp(), total.getMp()), ch.getCurrentMp(), total.getMp());
        System.out.printf( "     ║   Gold : %d G%n", ch.getGold());
        System.out.printf( "     ║   EXP  [%s]  %4d / %4d%n",
                bar(ch.getCurrentExp(), ch.expToNextLevel()),
                ch.getCurrentExp(), ch.expToNextLevel());
    }

    private static String bar(int current, int max) {
        int filled = (max == 0) ? 0 : Math.min((current * BAR_WIDTH) / max, BAR_WIDTH);
        return "█".repeat(filled) + "░".repeat(BAR_WIDTH - filled);
    }
}
