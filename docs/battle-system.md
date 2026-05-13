# 전투 시스템 설계 상세 문서

## 개요

전투는 `Battle` → `StageContext` → `BattleStage` → `Dungeon` 의 계층 구조로 동작한다.  
각 계층은 서로 다른 책임을 갖고, 상위 계층이 하위 계층을 조율하는 방식이다.

```
Dungeon              — 던전 전체 (다수 스테이지, 리셋 단위)
  └─ BattleStage     — 스테이지 (시작/종료/리셋 상태 관리)
       └─ StageContext — 한 스테이지의 실행 상태 (턴 로그, 보상 수집)
            └─ Battle  — 순수 턴 실행 엔진 (데미지, 쿨다운, 상태 전이)
```

---

## 1. BattleParticipant — 전투 참여자 인터페이스

`Character` 와 `Monster` 는 `BattleParticipant` 인터페이스를 공유한다.  
`Battle` 은 이 인터페이스만 바라보기 때문에, 플레이어와 몬스터를 **동일한 코드**로 처리한다.

```java
public interface BattleParticipant extends Damageable, HasLevel {
    int getAttackPower();
    int getDefensePower();
    int getCurrentMp();
    List<Skill> getSkills();
    SkillCoolDownContext getCoolDownContext();

    default boolean canUse(Skill skill) {
        return getSkills().contains(skill)
            && skill.getMpCost() <= getCurrentMp()
            && getCoolDownContext().isReady(skill);
    }

    default List<ActiveSkill> getUsableSkills() { ... }

    static int calculateDamage(BattleParticipant attacker,
                               BattleParticipant defender, ActiveSkill skill) {
        return Math.max(1, attacker.getAttackPower() + skill.getDamage()
                           - defender.getDefensePower());
    }
}
```

### 설계 결정: default 메서드로 공통 로직을 인터페이스에 배치

`canUse()` 와 `getUsableSkills()` 는 `Character` 와 `Monster` 모두 동일하게 동작한다.  
이를 추상 클래스 대신 인터페이스의 `default` 메서드로 구현한 이유:

- `Character` 와 `Monster` 는 상속 계층이 다르다. 공통 추상 클래스를 만들면 단일 상속 제약으로 인해 다른 인터페이스(`Damageable`, `HasLevel` 등)와의 조합이 복잡해진다.
- 인터페이스 `default` 로 두면 두 클래스가 독립적으로 유지되면서도 중복 없이 동일한 로직을 공유한다.
- `calculateDamage()` 는 `static` 으로 선언해 상태 없이 수식만 담당한다.

---

## 2. Battle — 턴 실행 엔진

`Battle` 은 상태(`BattleStatus`, 턴 커서, 라운드)를 관리하고,  
`nextTurn(Skill)` 한 번 호출에 **단 하나의 참여자가 행동**하도록 설계되어 있다.

```java
public BattleTurn nextTurn(Skill selectedSkill) {
    BattleParticipant actor  = getCurrentActor();   // 현재 행동할 참여자
    BattleParticipant target = resolveTarget(actor); // 공격 대상
    ActiveSkill skill        = resolveSkill(actor, selectedSkill);

    boolean critical = (actor instanceof Character ch) && ch.rollCritical();
    int damage       = calculateFinalDamage(actor, target, skill, critical);

    applyAttack(actor, skill, target, damage);
    updateStatus();
    advanceCursor();

    return new BattleTurn(round, actor, target, skill, damage, critical, targetDead, status);
}
```

### 설계 결정: 크리티컬 판정을 Battle에서 1회만 수행

```java
boolean critical = (actor instanceof Character ch) && ch.rollCritical();
int damage       = calculateFinalDamage(actor, target, skill, critical);
applyAttack(actor, skill, target, damage);
```

크리티컬 판정(`rollCritical()`)을 **데미지 계산 전 1회**만 호출하고, 같은 `critical` 값을 계산과 로그에 모두 사용한다.  
만약 판정과 적용을 분리하거나 두 번 호출하면, "크리티컬이 터졌다고 표시됐지만 데미지는 기본치" 같은 불일치가 발생할 수 있다.  
몬스터는 크리티컬 판정이 없으므로 `instanceof Character` 로 분기한다.

### 설계 결정: 턴 커서 방식의 행동 순서

```
turnOrder = [player, monster1, monster2, ...]
turnCursor → 0(player) → 1(monster1) → 2(monster2) → 0(player) → ...
```

`nextTurn()` 을 호출할 때마다 커서가 하나씩 전진하며, 사망한 참여자는 `getCurrentActor()` 에서 건너뛴다.  
`StageContext.progressTurn()` 이 플레이어 행동 후 `battle.isPlayerTurn()` 이 `false` 인 동안 `nextTurn()` 을 반복 호출해 모든 몬스터가 한 라운드 안에 행동한다.

### 설계 결정: 플레이어 턴 스킵

사용 가능한 스킬이 없으면 `showSkillMenu()` 가 `null` 을 반환하고,  
`StageContext.progressTurn(null)` 은 `battle.skipPlayerTurn()` 으로 커서만 전진시킨다.  
이때도 턴 시작 시 `tick()` 과 `recoverMp(10)` 은 정상 실행된다.

---

## 3. StageContext — 턴 진행 조율자

`StageContext` 는 `Battle` 의 순수 턴 실행 위에서 **실제 게임 턴의 흐름**을 구성한다.

```java
public List<BattleTurn> progressTurn(Skill skill) {
    // 1. 턴 시작: 전 참여자 MP 회복 + 쿨다운 감소
    battle.getTurnOrder().forEach(p -> {
        p.recoverMp(10);
        p.getCoolDownContext().tick();
    });

    // 2. 플레이어 행동
    if (skill != null) {
        BattleTurn playerTurn = battle.nextTurn(skill);
        history.add(playerTurn);
        collectIfKilled(playerTurn);
    } else {
        battle.skipPlayerTurn();
    }

    // 3. 몬스터 연속 행동 (플레이어 턴이 아닌 동안)
    while (!battle.isFinished() && !battle.isPlayerTurn()) {
        BattleTurn monsterTurn = battle.nextTurn(null);
        history.add(monsterTurn);
    }

    return turns;
}
```

### 설계 결정: tick()을 플레이어 행동 전에 실행하는 이유

`tick()` 이 플레이어가 스킬을 고르기 **이전** (스킬 메뉴 표시 전) 이 아니라,  
`dungeon.nextTurn()` 호출 후 `progressTurn()` 의 **첫 번째 단계**에서 실행된다.

```
[플레이어가 스킬을 고름]   ← 이 시점에서의 쿨다운은 "이전 progressTurn의 tick 결과"
[dungeon.nextTurn(skill)]
  └─ progressTurn()
       ├─ 1. tick()         ← 지금 쿨다운 감소
       ├─ 2. 플레이어 행동
       └─ 3. 몬스터 행동
```

이 타이밍의 의미:

- CD=1 스킬을 사용하면, 다음 `progressTurn()` 에서 tick → CD=0 → 제거된다.
- 즉 플레이어는 **다음 스킬 선택 화면에서 그 스킬을 다시 사용할 수 있다**.
- CD=N 은 "N번의 스킬 선택 화면 동안 사용 불가"가 아니라 "N번의 progressTurn 후 사용 가능"을 의미한다.

### 설계 결정: progressTurn이 List<BattleTurn>을 반환하는 이유

한 라운드에 플레이어 1회 + 몬스터 N회의 행동이 연속으로 발생한다.  
각 행동은 `BattleTurn` 레코드로 기록되며, 호출자(`GameController`)가 이 목록을 순회해 EXP 획득, 레벨업, 로그 출력 등을 처리한다.  
`StageContext` 는 전투 실행만 담당하고, 사후 처리는 컨트롤러에 위임하는 **단일 책임** 구조다.

---

## 4. SkillCoolDownContext — 쿨다운 상태 관리

```java
public class SkillCoolDownContext {
    private final Map<Skill, Integer> skillCoolDowns = new HashMap<>();

    public void register(Skill skill) {
        if (skill.getCooldown() > 0)
            skillCoolDowns.put(skill, skill.getCooldown());
    }

    public void tick() {
        skillCoolDowns.replaceAll((skill, cd) -> cd - 1);
        skillCoolDowns.entrySet().removeIf(e -> e.getValue() <= 0);
    }

    public boolean isReady(Skill skill) {
        return !skillCoolDowns.containsKey(skill);
    }
}
```

### 설계 결정: "있으면 쿨다운 중, 없으면 사용 가능" 방식

Map에 스킬이 **없으면** 사용 가능(`isReady = true`), **있으면** 쿨다운 중.  
CD=0 인 스킬은 `register()` 에서 아예 추가하지 않으므로 Map 크기가 최소화된다.  
`tick()` 이 CD를 0 이하로 만들면 즉시 제거하므로 Map은 항상 "현재 쿨다운 중인 것만" 보관한다.

### 설계 결정: Skill 객체 참조를 Key로 사용

`HashMap` 의 키는 `Skill` 객체 참조(`Object.equals` / `hashCode`)를 사용한다.  
`Character.skills` 는 `setJob()` 시 한 번 생성되고 이후 교체되지 않는다.  
따라서 `register()` 에서 넣은 객체와 `isReady()` 에서 조회하는 객체가 항상 동일한 참조를 가리킨다.

### 스테이지 전환 시 쿨다운 초기화

`BattleStage.start()` 에서 `player.getCoolDownContext().clear()` 를 호출한다.  
몬스터는 스테이지마다 새 인스턴스로 생성되므로 자동으로 초기화된다.  
플레이어만 명시적 초기화가 필요한 이유는, 플레이어는 던전 전체를 통틀어 단 하나의 인스턴스이기 때문이다.

---

## 5. 데미지 계산

```
base  = max(1, 공격자 ATK + 스킬 데미지 - 방어자 DEF)
final = critical ? base × 2 : base
```

### 설계 결정: 최솟값 1 보장

`Math.max(1, ...)` 으로 방어력이 공격력을 완전히 상쇄하더라도 최소 1의 데미지가 보장된다.  
0 데미지 턴이 무한 반복되는 상황을 방지한다.

### 설계 결정: BOSS 몬스터 데미지 0.8 계수

```java
// Monster.getDamage()
if (MonsterType.BOSS.equals(type)) {
    return (int) ((getAttackPower() + activeSkill.getDamage()) * 0.8);
}
```

BOSS 는 스탯이 높은 대신 데미지 계수를 낮춰 즉사 확률을 조정한다.  
이 계수는 `getDamage()` 내부에 캡슐화되어 있어, `Battle` 은 BOSS 여부를 알 필요가 없다.

---

## 6. BattleTurn — 불변 레코드

```java
public record BattleTurn(
    int round,
    BattleParticipant attacker,
    BattleParticipant target,
    ActiveSkill skill,
    int damage,
    boolean critical,
    boolean targetDead,
    BattleStatus status
) {}
```

### 설계 결정: record 사용 이유

- 턴 결과는 생성 후 변경될 이유가 없다. 불변성이 자연스럽다.
- `equals()`, `hashCode()`, `toString()` 이 자동 생성된다.
- `history` 목록에 쌓이는 값 객체이므로 record 가 의미적으로도 정확하다.

`targetDead` 를 별도 필드로 둔 이유: 로그 출력과 EXP 계산 시 매번 `target.isDead()` 를 호출하면,  
이후 상태 변화로 인해 결과가 달라질 수 있다. 행동 직후의 사망 여부를 스냅샷으로 보존한다.

---

## 7. 전투 종료 및 스테이지 전환

```java
// Dungeon.nextTurn()
stage.next(skill);

if (stage.isFinished()
    && stage.getContext().getBattle().isPlayerVictory()
    && hasNextStage()) {
    currentStageIndex++;
    getCurrentStage().start(player, rewardContext);  // 쿨다운 초기화 포함
}
```

스테이지 클리어 감지는 `Dungeon.nextTurn()` 내부에서 즉시 이루어지며,  
다음 스테이지 시작도 같은 메서드 호출 안에서 처리된다.  
`GameController` 는 `dungeon.isCleared()` / `dungeon.isFailed()` 를 폴링해 전체 흐름을 제어한다.

### 설계 결정: 보상 누적을 던전 전체 범위로

`RewardContext` 는 `Dungeon` 에 하나만 존재하며, 스테이지가 바뀌어도 유지된다.  
각 스테이지에서 몬스터가 죽을 때마다 `collectIfKilled()` 가 호출되어 보상을 누적하고,  
던전 완전 클리어 시 한 번에 `claimRewards()` 로 지급된다.  
패배 시 `dungeon.reset()` 이 `rewardContext.clear()` 를 호출해 누적 보상이 소멸된다.
