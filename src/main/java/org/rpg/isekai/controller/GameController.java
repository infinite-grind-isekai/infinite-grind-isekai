package org.rpg.isekai.controller;

import org.rpg.isekai.domain.battle.*;
import org.rpg.isekai.domain.character.Character;
import org.rpg.isekai.domain.item.Item;
import org.rpg.isekai.domain.item.amorItem.*;
import org.rpg.isekai.domain.item.materialItem.*;
import org.rpg.isekai.domain.item.potionItem.*;
import org.rpg.isekai.domain.item.potionItem.HeartOfDragon;
import org.rpg.isekai.domain.item.potionItem.PhoenixFeather;
import org.rpg.isekai.domain.job.Job;
import org.rpg.isekai.domain.monster.Monster;
import org.rpg.isekai.domain.skill.ActiveSkill;
import org.rpg.isekai.ioc.Starter;
import org.rpg.isekai.view.*;

import java.util.List;
import java.util.Map;

public class GameController implements Starter {

    private final DungeonManager dungeonManager;
    private final ItemManager itemManager;
    private final InitialManager initialManager;

    public GameController(DungeonManager dungeonManager, ItemManager itemManager, InitialManager initialManager) {
        this.dungeonManager = dungeonManager;
        this.itemManager = itemManager;
        this.initialManager = initialManager;
    }

    @Override
    public void start() {
        TitleView.showTitle();
        mainMenuLoop();
    }

    private void mainMenuLoop() {
        while (true) {
            TitleView.showMainMenu();
            String input = ConsoleUtils.SCANNER.nextLine().trim();
            switch (input) {
                case "1" -> newGame();
                case "0" -> { return; }
            }
        }
    }

    private void newGame() {
        TitleView.showOpeningScript();

        String name  = CharacterSetupView.showUsernameInput();
        Job    job   = CharacterSetupView.showJobSelection();

        Character character = new Character(name);
        character.setJob(job);
        initialManager.prepareForJob(job);

        for (Item item : initialManager.getInitialItems()) {
            character.obtainItem(item);
        }

        gameLoop(character);
    }

    private void gameLoop(Character character) {
        while (true) {
            int choice = GameMenuView.showMainMenu(character);
            switch (choice) {
                case 1 -> GameMenuView.showCharacterInfo(character);
                case 2 -> manageInventory(character);
                case 3 -> selectAndPlayDungeon(character);
                case 4 -> enterStore(character);
                case 5 -> GameMenuView.showEquipment(character);
                case 0 -> { return; }
            }
        }
    }

    private void manageInventory(Character character) {
        while (true) {
            int itemIdx = GameMenuView.showInventory(character);
            if (itemIdx == -1) break;

            Item selected = character.getInventory().getItems().get(itemIdx);
            try {
                character.useItem(selected);
                System.out.println("     [ + ] " + selected.getName() + "을(를) 사용(장착)했습니다.");
            } catch (IllegalArgumentException e) {
                System.out.println("     [ ! ] " + e.getMessage());
            }
            ConsoleUtils.sleep(1000);
        }
    }

    private void enterStore(Character character) {
        while (true) {
            int choice = GameMenuView.showStoreMenu(character);
            switch (choice) {
                case 1 -> buyItem(character);
                case 2 -> sellItem(character);
                case 0 -> { return; }
            }
        }
    }

    private void buyItem(Character character) {
        while (true) {
            Item selected = GameMenuView.showItemStore(character, itemManager.getStoreItems());
            if (selected == null) break;

            if (character.getGold() >= selected.getPrice()) {
                character.setGold(character.getGold() - selected.getPrice());
                character.obtainItem(selected);
                System.out.println("     [ + ] " + selected.getName() + "을(를) 구매했습니다.");
            } else {
                System.out.println("     [ ! ] 골드가 부족합니다.");
            }
            ConsoleUtils.sleep(1000);
        }
    }

    private void sellItem(Character character) {
        while (true) {
            Item selected = GameMenuView.showSellMenu(character);
            if (selected == null) break;

            int sellPrice = selected.getPrice() / 2;
            character.setGold(character.getGold() + sellPrice);
            
            // 만약 장착 중인 아이템이라면 장착 해제
            if (character.getLoadout().isEquipped(selected)) {
                character.unequip(selected);
            }
            
            character.getInventory().remove(selected);
            System.out.println("     [ + ] " + selected.getName() + "을(를) " + sellPrice + " G에 판매했습니다.");
            ConsoleUtils.sleep(1000);
        }
    }

    private void selectAndPlayDungeon(Character character) {
        Map<DungeonKind, Dungeon> dungeons = dungeonManager.getDungeons();
        DungeonKind kind = GameMenuView.showDungeonList(dungeons);
        if (kind == null) return;

        Dungeon dungeon = dungeonManager.getDungeon(kind);
        playDungeon(character, dungeon);
    }

    private void playDungeon(Character character, Dungeon dungeon) {
        dungeon.start(character);

        while (true) {
            BattleStage stage = dungeon.getCurrentStage();

            // 스테이지 첫 진입 시 인트로 표시
            if (!stage.isStarted()) {
                DungeonBattleView.showStageIntro(dungeon);
            }

            // 전투 상태 렌더
            DungeonBattleView.showBattleState(dungeon);

            // 플레이어 스킬 선택 (null = 사용 가능한 스킬 없음 → 턴 스킵)
            ActiveSkill skill = DungeonBattleView.showSkillMenu(dungeon, character);

            // 턴 실행 (플레이어 -> 몬스터 자동)
            StageContext ctx     = stage.getContext();
            int          prev    = ctx.getHistory().size();
            dungeon.nextTurn(skill);
            List<BattleTurn> newTurns = List.copyOf(
                    ctx.getHistory().subList(prev, ctx.getHistory().size()));

            DungeonBattleView.showTurnLog(newTurns);

            // EXP 획득 (처치한 몬스터 처리)
            int prevLevel = character.getLevel();
            for (BattleTurn turn : newTurns) {
                if (turn.targetDead() && turn.target() instanceof Monster m) {
                    character.gainExp(m.getExp());
                }
            }
            if (character.getLevel() > prevLevel) {
                DungeonBattleView.showLevelUp(character, prevLevel);
            }

            // 패배 처리
            if (dungeon.isFailed()) {
                DungeonBattleView.showDefeat();
                dungeon.reset();
                character.recoverFullHealth();
                return;
            }

            // 스테이지 클리어 알림
            if (stage.isFinished()) {
                DungeonBattleView.showStageVictory(
                        stage.getNumber(), dungeon.getStages().size());
            }

            // 던전 완전 클리어
            if (dungeon.isCleared()) {
                Reward reward = dungeon.claimRewards();
                character.obtainReward(reward);
                DungeonBattleView.showDungeonClear(dungeon, reward);
                dungeon.reset();
                character.recoverFullHealth();
                return;
            }
        }
    }
}
