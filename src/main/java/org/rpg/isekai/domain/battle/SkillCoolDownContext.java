package org.rpg.isekai.domain.battle;

import org.rpg.isekai.domain.skill.Skill;

import java.util.HashMap;
import java.util.Map;

public class SkillCoolDownContext {

    private final Map<Skill, Integer> skillCoolDowns = new HashMap<>();

    /** 스킬 사용 후 쿨다운 등록. cooldown = 0 이면 등록하지 않는다. */
    public void register(Skill skill) {
        if (skill.getCooldown() > 0) {
            skillCoolDowns.put(skill, skill.getCooldown());
        }
    }

    /** 매 턴 시작 시 호출. 쿨다운 1 감소, 0 이하가 된 항목은 제거. */
    public void tick() {
        skillCoolDowns.replaceAll((skill, cd) -> cd - 1);
        skillCoolDowns.entrySet().removeIf(e -> e.getValue() <= 0);
    }

    /** 쿨다운이 없으면 사용 가능. */
    public boolean isReady(Skill skill) {
        return !skillCoolDowns.containsKey(skill);
    }

    /** 남은 쿨다운 턴 수. 쿨다운 없으면 0. */
    public int getRemainingCooldown(Skill skill) {
        return skillCoolDowns.getOrDefault(skill, 0);
    }

    /** 스테이지 전환 시 전체 초기화. */
    public void clear() {
        skillCoolDowns.clear();
    }
}
