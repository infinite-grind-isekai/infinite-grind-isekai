package org.rpg.isekai.view;

import org.rpg.isekai.domain.battle.*;
import org.rpg.isekai.domain.character.Character;
import org.rpg.isekai.domain.item.Item;
import org.rpg.isekai.domain.item.potionItem.ManaPotion;
import org.rpg.isekai.domain.item.potionItem.PotionItem;
import org.rpg.isekai.domain.monster.*;

import static org.rpg.isekai.domain.character.Character.*;
import org.rpg.isekai.domain.skill.ActiveSkill;

import java.util.List;

public class DungeonBattleView {

    private static final int MONSTER_BAR = 12;
    private static final int PLAYER_BAR  = 18;
    private static final int ART_PAD     = 18;

    // ── 스테이지 입장 ──────────────────────────────────────────────────────────

    public static void showStageIntro(Dungeon dungeon) {
        ConsoleUtils.clear();
        BattleStage stage = dungeon.getCurrentStage();
        int cur = stage.getNumber();
        int tot = dungeon.getStages().size();

        System.out.println();
        System.out.println("     ╔═══════════════════════════════════════════════════════╗");
        System.out.printf( "     ║  ⚔  %s%n", dungeon.getName());
        System.out.println("     ║");
        System.out.printf( "     ║     STAGE  %d  /  %d%n", cur, tot);
        System.out.println("     ╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("     [ 등장 몬스터 ]");
        System.out.println();

        for (Monster m : stage.getMonsters()) {
            String bossTag = m.getType() == MonsterType.BOSS ? "  ★ BOSS" : "";
            System.out.printf("       %s  %s  Lv.%d%s%n",
                    miniIcon(m), m.getName(), m.getLevel(), bossTag);
        }

        System.out.println();
        System.out.print("     [ ENTER 로 전투 시작 ]");
        ConsoleUtils.SCANNER.nextLine();
    }

    // ── 전투 상태 렌더 ─────────────────────────────────────────────────────────

    public static void showBattleState(Dungeon dungeon) {
        ConsoleUtils.clear();
        StageContext ctx = dungeon.getCurrentStage().getContext();

        printDungeonHeader(dungeon);
        System.out.println("     ║");
        System.out.println("     ║  < 몬스터 >");
        System.out.println("     ║");
        printMonsters(ctx.getMonsters());
        System.out.println("     ║");
        System.out.println("     ╠═══════════════════════════════════════════════════════╣");
        System.out.println("     ║");
        printPlayer(ctx.getPlayer());
        System.out.println("     ║");
        System.out.println("     ╚═══════════════════════════════════════════════════════╝");
    }

    // ── 플레이어 스킬 선택 ─────────────────────────────────────────────────────

    public static ActiveSkill showSkillMenu(Dungeon dungeon, Character ch) {
        while (true) {
            System.out.println();
            System.out.println("     ══════════════════  ★  당 신 의 턴  ★  ══════════════════");
            System.out.println();

            List<ActiveSkill> usable    = ch.getUsableSkills();
            List<ActiveSkill> allActive = ch.getSkills().stream()
                    .filter(ActiveSkill.class::isInstance)
                    .map(ActiveSkill.class::cast)
                    .toList();

            java.util.Map<Integer, ActiveSkill> selectable = new java.util.LinkedHashMap<>();
            int selectIdx = 1;

            for (ActiveSkill skill : allActive) {
                if (usable.contains(skill)) {
                    System.out.printf("       %d.  %-16s  MP %3d   DMG %3d%n",
                            selectIdx, skill.getName(), skill.getMpCost(), skill.getDamage());
                    selectable.put(selectIdx++, skill);
                } else {
                    int cd = ch.getCoolDownContext().getRemainingCooldown(skill);
                    String reason = cd > 0 ? "쿨다운 " + cd + "턴" : "MP 부족";
                    System.out.printf("       -   %-16s  [%s]%n", skill.getName(), reason);
                }
            }

            System.out.println();
            System.out.println("       9.  포션 사용");

            if (selectable.isEmpty()) {
                System.out.println("     [ ! ] 사용 가능한 스킬이 없습니다.");
            }

            System.out.println();
            System.out.print("     스킬 선택 > ");
            String input = ConsoleUtils.SCANNER.nextLine().trim();

            if (input.equals("9")) {
                showPotionMenu(ch);
                showBattleState(dungeon);
                continue;
            }

            if (selectable.isEmpty()) {
                ConsoleUtils.sleep(1200);
                return null;
            }

            try {
                int idx = Integer.parseInt(input);
                if (selectable.containsKey(idx)) return selectable.get(idx);
            } catch (NumberFormatException ignored) {}
            System.out.println("     [ ! ] 올바른 번호를 입력하세요.");
        }
    }

    // ── 포션 사용 ──────────────────────────────────────────────────────────────

    private static void showPotionMenu(Character ch) {
        List<Item> potions = ch.getInventory().getItems().stream()
                .filter(item -> item instanceof PotionItem)
                .toList();

        System.out.println();
        System.out.println("     ─────────────────────────────────────────────────────────");
        System.out.println("     [ 포 션 사 용 ]");
        System.out.println();

        if (potions.isEmpty()) {
            System.out.println("     [ ! ] 보유한 포션이 없습니다.");
            ConsoleUtils.sleep(700);
            System.out.println("     ─────────────────────────────────────────────────────────");
            return;
        }

        for (int i = 0; i < potions.size(); i++) {
            PotionItem p = (PotionItem) potions.get(i);
            String stat = (p instanceof ManaPotion) ? "MP" : "HP";
            System.out.printf("       %d.  %-20s  %s +%d%n",
                    i + 1, p.getName(), stat, p.getHealAmount());
        }
        System.out.println("       0.  돌아가기");
        System.out.println();
        System.out.print("     포션 선택 > ");

        String input = ConsoleUtils.SCANNER.nextLine().trim();
        System.out.println("     ─────────────────────────────────────────────────────────");

        if (input.equals("0")) return;

        try {
            int idx = Integer.parseInt(input) - 1;
            if (idx >= 0 && idx < potions.size()) {
                ch.useItem(potions.get(idx));
            }
        } catch (NumberFormatException ignored) {}
    }

    // ── 턴 결과 로그 ───────────────────────────────────────────────────────────

    public static void showTurnLog(List<BattleTurn> turns) {
        System.out.println();
        System.out.println("     ─────────────────────────────────────────────────────────");
        for (BattleTurn turn : turns) {
            boolean isMonster = turn.attacker() instanceof Monster;
            String tag  = isMonster ? "⚔ 몬스터  " : "★ 플레이어";
            String crit = turn.critical()    ? "  ★ CRITICAL!" : "";
            String dead = turn.targetDead()  ? "  ☠ 사망!"     : "";
            System.out.printf("     %s │ %s → %s │ %s │ 데미지 %d%s%s%n",
                    tag,
                    turn.attacker().getName(),
                    turn.target().getName(),
                    turn.skill().getName(),
                    turn.damage(),
                    crit,
                    dead);
        }
        System.out.println("     ─────────────────────────────────────────────────────────");
        ConsoleUtils.sleep(700);
    }

    // ── 스테이지 클리어 ────────────────────────────────────────────────────────

    public static void showStageVictory(int stageNum, int stageTotal) {
        System.out.println();
        System.out.println("     ┌─────────────────────────────────────────────────────────┐");
        System.out.printf( "     │   ★  STAGE %d / %d  클리어!                             │%n",
                stageNum, stageTotal);
        System.out.println("     └─────────────────────────────────────────────────────────┘");
        ConsoleUtils.sleep(800);
        if (stageNum < stageTotal) {
            System.out.println();
            System.out.print("     [ ENTER 로 다음 스테이지 ]");
            ConsoleUtils.SCANNER.nextLine();
        }
    }

    // ── 던전 클리어 ────────────────────────────────────────────────────────────

    public static void showDungeonClear(Dungeon dungeon, Reward reward) {
        ConsoleUtils.clear();
        System.out.println();
        System.out.println("     ╔═══════════════════════════════════════════════════════╗");
        System.out.println("     ║                                                       ║");
        System.out.println("     ║       ★ ★ ★   던 전 정 복 완 료   ★ ★ ★           ║");
        System.out.println("     ║                                                       ║");
        System.out.printf( "     ║   %s  클리어!%n", dungeon.getName());
        System.out.println("     ║                                                       ║");
        System.out.println("     ╠═══════════════════════════════════════════════════════╣");
        System.out.println("     ║   [ 총 획득 보상 ]                                    ║");
        System.out.println("     ║                                                       ║");
        System.out.printf( "     ║   Gold   :  %d G%n", reward.gold());
        if (reward.items().isEmpty()) {
            System.out.println("     ║   아이템 :  없음");
        } else {
            reward.items().forEach(item ->
                System.out.printf("     ║   아이템 :  %s%n", item.getName()));
        }
        System.out.println("     ║                                                       ║");
        System.out.println("     ╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.print("     [ ENTER 를 눌러 계속 ]");
        ConsoleUtils.SCANNER.nextLine();
    }

    // ── 레벨업 ────────────────────────────────────────────────────────────────

    public static void showLevelUp(Character ch, int prevLevel) {
        int gained = ch.getLevel() - prevLevel;
        System.out.println();
        System.out.println("     ╔═══════════════════════════════════════════════════════╗");
        System.out.println("     ║                                                       ║");
        System.out.println("     ║          ★ ★ ★   L E V E L   U P !   ★ ★ ★        ║");
        System.out.println("     ║                                                       ║");
        System.out.printf( "     ║          Lv. %-3d   →   Lv. %-3d%n", prevLevel, ch.getLevel());
        System.out.println("     ║                                                       ║");
        System.out.printf( "     ║   ATK  +%-3d   DEF  +%-3d   HP  +%-3d   MP  +%-3d%n",
                gained * LV_ATK, gained * LV_DEF, gained * LV_HP, gained * LV_MP);
        System.out.println("     ║                                                       ║");
        System.out.println("     ╚═══════════════════════════════════════════════════════╝");
        ConsoleUtils.sleep(1800);
    }

    // ── 패배 ──────────────────────────────────────────────────────────────────

    public static void showDefeat() {
        ConsoleUtils.clear();
        System.out.println();
        System.out.println("     ╔═══════════════════════════════════════════════════════╗");
        System.out.println("     ║                                                       ║");
        System.out.println("     ║         ☠   당 신 은 쓰 러 졌 습 니 다   ☠          ║");
        System.out.println("     ║                                                       ║");
        System.out.println("     ║    무한 노가다란 이런 것...                           ║");
        System.out.println("     ║    다시 일어나 갈고닦으십시오.                        ║");
        System.out.println("     ║                                                       ║");
        System.out.println("     ╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.print("     [ ENTER 를 눌러 계속 ]");
        ConsoleUtils.SCANNER.nextLine();
    }

    // ── 내부 렌더링 ────────────────────────────────────────────────────────────

    private static void printDungeonHeader(Dungeon dungeon) {
        int cur = dungeon.getCurrentStageIndex() + 1;
        int tot = dungeon.getStages().size();
        System.out.println();
        System.out.println("     ╔═══════════════════════════════════════════════════════╗");
        System.out.printf( "     ║  ⚔  %s   [%d / %d]   %s%n",
                dungeon.getName(), cur, tot, dungeon.getDifficulty().name());
    }

    private static void printMonsters(List<Monster> monsters) {
        boolean isSingleBoss = monsters.size() == 1
                && monsters.get(0).getType() == MonsterType.BOSS;

        if (isSingleBoss) {
            printMonsterWithArt(monsters.get(0), true);
        } else {
            for (Monster m : monsters) {
                printMonsterWithArt(m, false);
                System.out.println("     ║");
            }
        }
    }

    private static void printMonsterWithArt(Monster m, boolean isBoss) {
        String[] art = fullArt(m);

        if (m.isDead()) {
            System.out.printf("     ║   %-" + ART_PAD + "s  %s  Lv.%d   ☠ 사망%n",
                    art[0], m.getName(), m.getLevel());
            for (int i = 1; i < art.length; i++) {
                System.out.printf("     ║   %-" + ART_PAD + "s%n", art[i]);
            }
            return;
        }

        String bossTag = isBoss ? "  ★ BOSS ★" : "";
        String nameLabel = String.format("%s  Lv.%d%s", m.getName(), m.getLevel(), bossTag);
        String hpLine = String.format("HP  [%s]  %4d / %4d",
                bar(m.getHealth(), m.getStat().getHp(), MONSTER_BAR),
                m.getHealth(), m.getStat().getHp());
        String mpLine = String.format("MP  [%s]  %4d / %4d",
                bar(m.getCurrentMp(), m.getStat().getMp(), MONSTER_BAR),
                m.getCurrentMp(), m.getStat().getMp());

        String[] statLines = { nameLabel, hpLine, mpLine };

        int rows = Math.max(art.length, statLines.length);
        for (int i = 0; i < rows; i++) {
            String artPart  = i < art.length       ? art[i]        : "";
            String statPart = i < statLines.length  ? statLines[i]  : "";
            System.out.printf("     ║   %-" + ART_PAD + "s  %s%n", artPart, statPart);
        }
    }

    private static void printPlayer(Character ch) {
        String job    = ch.getJob() != null ? ch.getJob().getName() : "???";
        String[] art  = characterArt(ch);

        String nameLabel = String.format("%s  |  %s  Lv.%d", ch.getName(), job, ch.getLevel());
        String hpLine    = String.format("HP  [%s]  %4d / %4d",
                bar(ch.getCurrentHp(), ch.getTotalStat().getHp(), PLAYER_BAR),
                ch.getCurrentHp(), ch.getTotalStat().getHp());
        String mpLine    = String.format("MP  [%s]  %4d / %4d",
                bar(ch.getCurrentMp(), ch.getTotalStat().getMp(), PLAYER_BAR),
                ch.getCurrentMp(), ch.getTotalStat().getMp());

        String[] statLines = { nameLabel, hpLine, mpLine };

        System.out.println("     ║  < 플레이어 >");
        int rows = Math.max(art.length, statLines.length);
        for (int i = 0; i < rows; i++) {
            String artPart  = i < art.length       ? art[i]       : "";
            String statPart = i < statLines.length  ? statLines[i] : "";
            System.out.printf("     ║   %-" + ART_PAD + "s  %s%n", artPart, statPart);
        }
    }

    // ── 캐릭터 ASCII 아트 ──────────────────────────────────────────────────────

    private static String[] characterArt(Character ch) {
        if (ch.getJob() == null) return new String[]{"   ( ? )"};
        return switch (ch.getJob().getJobKind().getName()) {
            case "전사" -> new String[]{
                "    _O_",
                "   [/|\\]",
                "   (=|=)",
                "   /_|_\\",
                "    | |",
                "   _| |_"
            };
            case "마법사" -> new String[]{
                "    ,*,",
                "   (o_o)",
                "    \\|/",
                "   (*|*)",
                "    \\|/",
                "   /   \\"
            };
            case "아처" -> new String[]{
                "    (O)",
                "  <--\\|",
                "    --o",
                "     |\\",
                "     |",
                "    / \\"
            };
            case "건슬링어" -> new String[]{
                "    (O)",
                "   -|==-",
                "   (_|_)",
                "    \\|/",
                "     |",
                "    /|\\"
            };
            default -> new String[]{"   ( ? )"};
        };
    }

    // ── 몬스터 ASCII 아트 ──────────────────────────────────────────────────────

    private static String miniIcon(Monster m) {
        return switch (m.getClass().getSimpleName()) {
            case "Slime"         -> "(.~.)";
            case "GiantSlime"    -> "(o~o)";
            case "Skeleton"      -> "(x_x)";
            case "Goblin"        -> "(>.>)";
            case "Orc"           -> "(O_O)";
            case "AncientDragon" -> "(◆_◆)";
            default              -> "( ? )";
        };
    }

    private static String[] fullArt(Monster m) {
        return switch (m.getClass().getSimpleName()) {
            case "Slime" -> new String[]{
                "   .~~~~~.",
                "  ( ^   ^ )",
                "  (  ---  )",
                "   '~~~~~'"
            };
            case "GiantSlime" -> new String[]{
                "   .~xxx~.",
                "  ( x   x )",
                "  (  ---  )",
                "   '~~~~~'"
            };
            case "Skeleton" -> new String[]{
                "    _____",
                "   (x   x)",
                "    --^--",
                "   /|   |\\",
                "    |___|"
            };
            case "Goblin" -> new String[]{
                "   /\\  /\\",
                "  ( o  o )",
                "  (  >-< )",
                "   \\---/",
                "   /| |\\"
            };
            case "Orc" -> new String[]{
                "   ,------,",
                "  /  O  O  \\",
                " |  ======  |",
                "  \\_________/",
                "     |  |"
            };
            case "AncientDragon" -> new String[]{
                "    /\\    /\\",
                "   /  \\  /  \\",
                "  / ◆  \\/  ◆ \\",
                " /  ANCIENT   \\",
                "/___DRAGON_____\\",
                "      |    |",
                "  ~FIRE BREATH~"
            };
            default -> new String[]{"   [ ??? ]"};
        };
    }

    // ── 바 렌더링 ──────────────────────────────────────────────────────────────

    private static String bar(int cur, int max, int width) {
        int filled = (max == 0) ? 0 : Math.min((cur * width) / max, width);
        return "█".repeat(filled) + "░".repeat(width - filled);
    }
}
