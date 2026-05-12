package org.rpg.isekai.domain.battle;

import lombok.Getter;
import org.rpg.isekai.domain.character.Character;
import org.rpg.isekai.domain.monster.Monster;
import org.rpg.isekai.domain.skill.ActiveSkill;
import org.rpg.isekai.domain.skill.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class Battle {
    private final Character player;
    private final List<Monster> monsters;
    private final List<BattleParticipant> turnOrder;
    private BattleStatus status;
    private int round;
    private int turnCursor;

    public Battle(Character player, List<Monster> monsters) {
        if (player == null) {
            throw new IllegalArgumentException("플레이어는 필수입니다.");
        }
        if (monsters == null || monsters.isEmpty()) {
            throw new IllegalArgumentException("전투에는 최소 한 마리 이상의 몬스터가 필요합니다.");
        }
        this.player = player;
        this.monsters = List.copyOf(monsters);
        this.turnOrder = new ArrayList<>();
        this.turnOrder.add(player);
        this.turnOrder.addAll(monsters);
        this.status = BattleStatus.READY;
        this.round = 1;
        this.turnCursor = 0;
    }

    public BattleTurn nextTurn(Skill selectedSkill) {
        if (isFinished()) {
            throw new IllegalStateException("이미 종료된 전투입니다.");
        }

        if (status == BattleStatus.READY) {
            status = BattleStatus.IN_PROGRESS;
        }

        BattleParticipant actor = getCurrentActor();
        BattleParticipant target = resolveTarget(actor);
        ActiveSkill skill = resolveSkill(actor, selectedSkill);
        int damage = BattleParticipant.calculateDamage(actor, target, skill);

        performAttack(actor, skill, target);
        boolean targetDead = target.isDead();
        updateStatus();

        BattleTurn turn = new BattleTurn(round, actor, target, skill, damage, targetDead, status);
        advanceCursor();
        return turn;
    }

    public boolean isFinished() {
        return status == BattleStatus.PLAYER_VICTORY || status == BattleStatus.MONSTER_VICTORY;
    }

    public boolean isPlayerVictory() {
        return status == BattleStatus.PLAYER_VICTORY;
    }

    public boolean isMonsterVictory() {
        return status == BattleStatus.MONSTER_VICTORY;
    }

    public void skipPlayerTurn() {
        advanceCursor();
    }

    public boolean isPlayerTurn() {
        int cursor = turnCursor;
        for (int i = 0; i < turnOrder.size(); i++) {
            BattleParticipant p = turnOrder.get(cursor);
            if (!p.isDead()) {
                return p == player;
            }
            cursor = (cursor + 1) % turnOrder.size();
        }
        return false;
    }

    public List<Monster> getAliveMonsters() {
        return monsters.stream()
                .filter(monster -> !monster.isDead())
                .toList();
    }

    private BattleParticipant getCurrentActor() {
        int checked = 0;
        while (checked < turnOrder.size()) {
            BattleParticipant participant = turnOrder.get(turnCursor);
            if (!participant.isDead()) {
                return participant;
            }
            advanceCursor();
            checked++;
        }
        throw new IllegalStateException("행동 가능한 참여자가 없습니다.");
    }

    private BattleParticipant resolveTarget(BattleParticipant actor) {
        if (actor == player) {
            return getAliveMonsters().stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("살아있는 몬스터가 없습니다."));
        }
        if (player.isDead()) {
            throw new IllegalStateException("플레이어가 이미 사망했습니다.");
        }
        return player;
    }

    private void performAttack(BattleParticipant actor, ActiveSkill skill, BattleParticipant target) {
        if (actor instanceof Character character) {
            character.attack(skill, target);
            return;
        }
        if (actor instanceof Monster monster) {
            monster.attack(skill, target);
            return;
        }
        throw new IllegalStateException("지원하지 않는 전투 참여자입니다.");
    }

    private ActiveSkill resolveSkill(BattleParticipant actor, Skill selectedSkill) {
        if (actor == player) {
            if (!(selectedSkill instanceof ActiveSkill activeSkill)) {
                throw new IllegalArgumentException("플레이어 턴에는 액티브 스킬을 선택해야 합니다.");
            }
            if (!actor.canUse(activeSkill)) {
                throw new IllegalStateException("현재 사용할 수 없는 플레이어 스킬입니다.");
            }
            return activeSkill;
        }

        List<ActiveSkill> usable = actor.getUsableSkills();
        if (usable.isEmpty()) {
            throw new IllegalStateException("몬스터가 사용할 수 있는 액티브 스킬이 없습니다.");
        }
        return usable.get(new Random().nextInt(usable.size()));
    }

    private void updateStatus() {
        if (player.isDead()) {
            status = BattleStatus.MONSTER_VICTORY;
            return;
        }
        if (getAliveMonsters().isEmpty()) {
            status = BattleStatus.PLAYER_VICTORY;
        }
    }

    private void advanceCursor() {
        turnCursor++;
        if (turnCursor >= turnOrder.size()) {
            turnCursor = 0;
            round++;
        }
    }
}
