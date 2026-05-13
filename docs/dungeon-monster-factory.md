# 던전 / 몬스터 관리 상세 문서 (팩토리 & 싱글톤)

## 개요

던전과 몬스터는 **"설계도(Blueprint/Factory) → 런타임 인스턴스"** 의 두 단계로 분리되어 관리된다.  
설계도는 부팅 시 한 번 구성되고, 인스턴스는 실제 전투가 시작될 때마다 새로 생성된다.

```
[부팅 시]
MonsterManager.prepare()
  └─ factories: Map<Class, Supplier<Monster>> 등록

DungeonManager.prepare()
  └─ buildBlueprints()
       └─ StageBlueprint(List<Supplier<Monster>>)   ← 팩토리 묶음
  └─ dungeons: Map<DungeonKind, Dungeon> 완성

[게임 플레이 시]
Dungeon.start(player)
  └─ BattleStage.start(player, rewardContext)
       └─ StageBlueprint.createMonsters()
            └─ Supplier::get() × N  ← 새 Monster 인스턴스 생성
```

---

## 1. MonsterManager — 팩토리 레지스트리

```java
public class MonsterManager implements Manager {
    private final Map<Class<? extends Monster>, Supplier<Monster>> factories = new HashMap<>();

    @Override
    public void prepare() {
        factories.put(Slime.class,        Slime::new);
        factories.put(Skeleton.class,     Skeleton::new);
        factories.put(Goblin.class,       Goblin::new);
        factories.put(Orc.class,          Orc::new);
        factories.put(AncientDragon.class, AncientDragon::new);
        factories.put(GiantSlime.class,   GiantSlime::new);
        factories.put(Harpy.class,        Harpy::new);
        factories.put(Golem.class,        Golem::new);
        factories.put(Werewolf.class,     Werewolf::new);
        factories.put(Vampire.class,      Vampire::new);
    }

    public Supplier<Monster> getFactory(Class<? extends Monster> type) {
        Supplier<Monster> factory = factories.get(type);
        if (factory == null)
            throw new IllegalArgumentException("등록되지 않은 몬스터 타입: " + type.getSimpleName());
        return factory;
    }
}
```

### 팩토리 패턴 적용 이유

몬스터 인스턴스를 미리 만들어 두지 않고 `Supplier<Monster>` (팩토리)를 등록하는 이유:

**1. 가변 상태 격리**  
`Monster` 는 `currentHp`, `currentMp`, `SkillCoolDownContext` 등 변경 가능한 상태를 가진다.  
하나의 인스턴스를 재사용하면 던전을 리셋할 때 상태가 섞인다.  
팩토리를 사용하면 전투 시작 시마다 깨끗한 인스턴스가 생성된다.

**2. 동일 몬스터 복수 등장**  
```java
new StageBlueprint(List.of(skeleton, skeleton, goblin))
```
같은 `skeleton` 팩토리를 두 번 넣으면 별개의 `Skeleton` 인스턴스 두 개가 생성된다.  
인스턴스를 직접 넣었다면 같은 객체를 두 번 참조하는 문제가 생긴다.

**3. 타입 기반 조회**  
`Class<? extends Monster>` 를 키로 사용하므로 오타 없이 타입 안전하게 팩토리를 꺼낼 수 있다.  
새로운 몬스터를 추가할 때 `factories.put(NewMonster.class, NewMonster::new)` 한 줄만 추가하면 된다.

### Supplier 메서드 참조

`Slime::new` 는 `() -> new Slime()` 과 동일하다.  
람다 대신 메서드 참조를 쓰는 이유는 가독성과 간결함이다.  
각 몬스터 생성자는 매개변수가 없으므로 `Supplier<Monster>` 인터페이스와 정확히 맞아떨어진다.

---

## 2. StageBlueprint — 스테이지 설계도

```java
public record StageBlueprint(List<Supplier<Monster>> monsterFactories) {

    public StageBlueprint {
        if (monsterFactories == null || monsterFactories.isEmpty())
            throw new IllegalArgumentException("최소 한 마리 이상의 몬스터가 필요합니다.");
        monsterFactories = List.copyOf(monsterFactories);  // 불변 복사본
    }

    public List<Monster> createMonsters() {
        return monsterFactories.stream()
                .map(Supplier::get)  // 각 팩토리를 호출해 새 인스턴스 생성
                .toList();
    }
}
```

### record 사용 이유

`StageBlueprint` 는 "몬스터 팩토리 목록" 이라는 순수한 값 객체다.  
생성 후 변경이 없고, 동등 비교(equals)나 출력(toString)이 필요하지 않지만  
record 의 컴팩트 생성자(`public StageBlueprint { ... }`)로 입력 검증을 깔끔하게 처리할 수 있다.

### 불변 방어 복사

```java
monsterFactories = List.copyOf(monsterFactories);
```

외부에서 전달된 리스트가 나중에 변경되어도 `StageBlueprint` 내부 목록은 영향받지 않는다.  
이 복사본은 `List.of()` 로 생성된 것과 동일하게 불변이다.

### createMonsters() 가 매번 새 인스턴스를 반환하는 이유

`BattleStage.reset()` 은 `this.monsters = blueprint.createMonsters()` 를 호출해 몬스터를 재생성한다.  
던전을 반복 도전하거나 패배 후 리셋해도 항상 새 몬스터로 시작한다.

---

## 3. DungeonManager — 던전 조립 파이프라인

```java
private Map<DungeonKind, List<StageBlueprint>> buildBlueprints() {
    // 1. MonsterManager에서 팩토리 수집
    Supplier<Monster> goblin    = monsterManager.getFactory(Goblin.class);
    Supplier<Monster> slime     = monsterManager.getFactory(Slime.class);
    Supplier<Monster> skeleton  = monsterManager.getFactory(Skeleton.class);
    Supplier<Monster> orc       = monsterManager.getFactory(Orc.class);
    Supplier<Monster> dragon    = monsterManager.getFactory(AncientDragon.class);
    Supplier<Monster> giantSlime = monsterManager.getFactory(GiantSlime.class);

    // 2. 던전별 블루프린트 정의
    return Map.of(
        DungeonKind.UNKNOWN_DATA_BANK, List.of(
            new StageBlueprint(List.of(giantSlime))
        ),
        DungeonKind.TEST_SERVER_NO4, List.of(
            new StageBlueprint(List.of(goblin, slime)),
            new StageBlueprint(List.of(dragon))
        ),
        DungeonKind.DEBUGGING_GARDEN, List.of(
            new StageBlueprint(List.of(skeleton, skeleton, goblin)),
            new StageBlueprint(List.of(orc, orc)),
            new StageBlueprint(List.of(dragon))
        )
    );
}

public void prepare() {
    Map<DungeonKind, List<StageBlueprint>> blueprints = buildBlueprints();

    // 3. 블루프린트 → BattleStage → Dungeon 변환
    for (DungeonKind kind : DungeonKind.values()) {
        List<StageBlueprint> stageBlueprints = blueprints.get(kind);
        List<BattleStage> stages = new ArrayList<>();
        for (int i = 0; i < stageBlueprints.size(); i++) {
            stages.add(new BattleStage(i + 1, stageBlueprints.get(i)));
        }
        dungeons.put(kind, new Dungeon(kind.getName(), kind.getDifficulty(), stages));
    }
}
```

### 조립 파이프라인 흐름

```
Supplier<Monster>           ← MonsterManager가 보유
  ↓ (팩토리 참조)
StageBlueprint              ← 팩토리 목록을 담은 설계도
  ↓ (번호 부여)
BattleStage                 ← 설계도 + 스테이지 번호 + 상태(started/finished)
  ↓ (이름 + 난이도)
Dungeon                     ← 전체 던전 (다수 BattleStage 포함)
  ↓
dungeons: Map<DungeonKind, Dungeon>
```

각 변환 단계가 책임을 명확히 분리한다:
- `StageBlueprint`: "어떤 몬스터가 나오는가" (설계)
- `BattleStage`: "스테이지가 시작/종료되었는가" (상태)
- `Dungeon`: "전체 진행과 리셋을 관리" (흐름)

### DungeonKind enum과 연결

```java
public enum DungeonKind {
    UNKNOWN_DATA_BANK("언노운 데이터 뱅크", DungeonDifficulty.NORMAL),
    TEST_SERVER_NO4("테스트 서버 No.4",   DungeonDifficulty.NORMAL),
    DEBUGGING_GARDEN("디버깅 가든",        DungeonDifficulty.NIGHTMARE);

    private final String name;
    private final DungeonDifficulty difficulty;
}
```

`DungeonKind` 가 이름과 난이도를 가지므로, `DungeonManager` 는 종류만 열거하면 나머지 메타데이터를 enum에서 읽어온다.  
새 던전을 추가할 때는 enum에 항목을 추가하고 `buildBlueprints()` 에 해당 블루프린트를 정의하면 된다.

---

## 4. Dungeon — 리셋과 재사용

```java
public void reset() {
    currentStageIndex = 0;
    player = null;
    rewardContext.clear();
    stages.forEach(BattleStage::reset);  // 각 스테이지도 리셋
}
```

```java
// BattleStage.reset()
public void reset() {
    this.monsters = blueprint.createMonsters();  // 새 몬스터 인스턴스
    this.context  = null;
    this.started  = false;
    this.finished = false;
}
```

### 설계 결정: 던전 객체를 재사용하는 이유

부팅 시 생성된 `Dungeon` 객체는 게임 내내 **동일한 인스턴스**를 사용한다.  
패배하거나 클리어하면 `dungeon.reset()` 으로 초기 상태로 돌아간다.  
새 인스턴스를 만드는 대신 리셋하는 이유:

- `DungeonManager.dungeons` 맵이 참조하는 인스턴스가 유지된다. 매번 재생성하면 맵의 참조를 갱신해야 한다.
- `BattleStage` 는 `blueprint.createMonsters()` 로 몬스터를 재생성하므로 상태 오염이 없다.
- `RewardContext.clear()` 로 누적 보상을 초기화한다.

---

## 5. Monster — 도메인 객체 설계

```java
public class Monster implements HasLevel, Attackable<Skill>,
                                Damageable, BattleParticipant, Rewardable {
    // 불변 필드 (생성자에서 확정)
    private final String name;
    private final int level;
    private final MonsterType type;
    private final Stat stat;
    private final List<Skill> skills;
    private final int exp;

    // 가변 상태 (전투 중 변화)
    private int currentHp;
    private int currentMp;
    private final SkillCoolDownContext coolDownContext = new SkillCoolDownContext();
}
```

### 생성자 체인

```java
// 최소 정보로도 생성 가능하도록 기본값을 제공하는 생성자 체인
Monster(name, level, type, stat)
  └─ Monster(name, level, type, stat, List.of())
       └─ Monster(name, level, type, stat, skills, Reward.empty())
            └─ Monster(name, level, type, stat, skills, reward, 0 /*exp*/)
                 └─ Monster(name, level, type, stat, skills, reward, exp,
                            stat.getHp(), stat.getMp())   ← 최종 생성자
```

서브클래스(`Slime`, `Goblin` 등)는 이 체인 중 적절한 것을 호출해 필요한 값만 정의한다.  
`currentHp` / `currentMp` 는 마지막 생성자에서 `stat.getHp()` / `stat.getMp()` 로 초기화된다.

### BOSS 데미지 계수 캡슐화

```java
// Monster.getDamage()
if (MonsterType.BOSS.equals(type)) {
    return (int) ((getAttackPower() + activeSkill.getDamage()) * 0.8);
}
return getAttackPower() + activeSkill.getDamage();
```

BOSS 판별 로직을 `Monster` 내부에 두었다.  
`Battle` 은 `BattleParticipant.calculateDamage()` 를 사용하지 않고, 각 참여자의 `getDamage()` 를 직접 호출하는 방식으로 이 캡슐화를 활용한다.

### SkillCoolDownContext 인스턴스 소유

각 `Monster` 인스턴스가 `final SkillCoolDownContext coolDownContext = new SkillCoolDownContext()` 로  
자신만의 쿨다운 컨텍스트를 보유한다.  
몬스터 인스턴스가 스테이지 리셋 시 새로 생성되므로 쿨다운도 자동 초기화된다.

---

## 6. 전체 의존 그래프

```
Container (싱글톤)
  └─ IoCManager
       ├─ MonsterManager ─────────────────────────────┐
       │    └─ factories: Map<Class, Supplier>         │
       │                                               ▼
       ├─ DungeonManager ← monsterManager.getFactory()
       │    └─ dungeons: Map<DungeonKind, Dungeon>
       │         └─ Dungeon
       │              └─ BattleStage
       │                   └─ StageBlueprint
       │                        └─ List<Supplier<Monster>>
       │
       ├─ ItemManager
       │    └─ storeItems: List<Item>
       │
       ├─ InitialManager
       │    └─ initialItems: List<Item>
       │
       └─ GameController (Starter)
            ├─ dungeonManager
            ├─ itemManager
            └─ initialManager
```
