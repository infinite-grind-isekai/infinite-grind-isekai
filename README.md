# 무한노가다로 시작하는 이세계정복기

> 콘솔 기반 텍스트 RPG — 턴제 배틀, 던전 공략, 레벨업, 장비, 상점

---

## 목차

1. [실행 방법](#실행-방법)
2. [게임 흐름](#게임-흐름)
3. [아키텍처](#아키텍처)
4. [다형성 & 상속 설계](#다형성--상속-설계)
5. [직업](#직업)
6. [전투 시스템](#전투-시스템)
7. [스킬 쿨타임](#스킬-쿨타임)
8. [크리티컬](#크리티컬)
9. [장비 시스템](#장비-시스템)
10. [상점](#상점)
11. [던전 & 스테이지](#던전--스테이지)
12. [몬스터](#몬스터)
13. [경험치 & 레벨업](#경험치--레벨업)
14. [보상 시스템](#보상-시스템)
15. [프로젝트 구조](#프로젝트-구조)

> 최근 추가된 기능: 전투 중 포션 사용 (9번), 장착 아이템 화면 (메뉴 5번), 무기 직업 제한, 방어구 부위별 착용 및 교체 반환, 신규 캐릭터 초기 아이템 지급 (InitialManager), 백골의 요새 던전 추가, 메가 스켈레톤 (BOSS) 추가, 초보자 방어구 3종 추가 (Circlet·Gauntlet·Greaves)

---

## 실행 방법

**요구 사항**
- Java 17 이상
- Gradle (Wrapper 포함)

```bash
./gradlew build
./gradlew run
```

> Windows: `gradlew.bat build` / `gradlew.bat run`

콘솔 출력이 깨질 경우 Run Configuration → VM options 에 `-Dfile.encoding=UTF-8` 추가.

---

## 게임 흐름

```
타이틀 화면
  └─ 새 게임 시작
       ├─ 오프닝 스크립트
       ├─ 이름 입력 (3 ~ 8 글자)
       ├─ 직업 선택
       ├─ 초기 아이템 지급 (LeatherArmor + ManaPotion + 직업 전용 무기)
       └─ 메인 메뉴 루프
            ├─ 1. 내 정보 보기
            ├─ 2. 인벤토리 (아이템 사용·장착)
            ├─ 3. 던전 입장
            │    ├─ 던전 선택
            │    ├─ 스테이지별 전투
            │    │    ├─ 스킬 선택 (쿨타임 / 크리티컬 / 드롭률 적용)
            │    │    └─ 9. 포션 사용 (턴 소모 없이 인벤토리 포션 즉시 사용)
            │    └─ 클리어 시 보상 획득
            ├─ 4. 상점 입장
            │    └─ 아이템 구매 → 인벤토리 추가
            ├─ 5. 장착 아이템 (현재 착용 무기·방어구 확인)
            └─ 0. 종료
```

---

## 아키텍처

### 레이어 구조

```
┌──────────────────────────────────────────────┐
│  view/          콘솔 UI (출력 + 입력)          │
├──────────────────────────────────────────────┤
│  controller/    게임 루프, Manager, IoC        │
├──────────────────────────────────────────────┤
│  domain/        순수 도메인 (전투·캐릭터·아이템)│
├──────────────────────────────────────────────┤
│  ioc/           경량 DI 컨테이너               │
└──────────────────────────────────────────────┘
```

### 부팅 시퀀스

```
Application.main()
  └─ System.setOut(UTF-8)
  └─ IsekaiApplication.run()
       └─ Container.reset()
            └─ IoCManager.init()
                 ├─ MonsterManager.prepare()   ← Supplier<Monster> 팩토리 등록
                 ├─ ItemManager.prepare()      ← 상점 아이템 목록 초기화
                 ├─ DungeonManager.prepare()   ← BluePrint 기반 던전 조립
                 └─ Container.register(Starter, GameController)

Container.get(Starter).start()
  └─ GameController  ← 전체 게임 루프 진입점
```

### IoC 컨테이너


| 인터페이스 | 역할 |
|-----------|------|
| `Manager` | `getOrder()` 기반 정렬 → 순서대로 `prepare()` 호출 |
| `Registerar` | `register()` 가 반환한 객체를 `Container` 에 등록 |
| `Starter` | `start()` — `Container.get(Starter.class)` 로 진입점 획득 |

```
Manager 실행 순서
  MonsterManager (order 10) → ItemManager (order 20) → DungeonManager (order 50)
```

### 주요 디자인 패턴

| 패턴 | 적용 위치 |
|------|----------|
| **Factory** | `MonsterManager` — `Supplier<Monster>` 팩토리 레지스트리 |
| **Blueprint** | `StageBlueprint` — 몬스터 팩토리 목록으로 스테이지 구성 정의 |
| **Singleton** | `Container` — 지연 초기화 싱글턴 |
| **Template Method** | `Manager` 인터페이스 — `needPrepare()` / `prepare()` 생명주기 |
| **Strategy** | `Job` 서브클래스 — 직업마다 다른 스킬 셋 제공 |
| **Context** | `StageContext`, `RewardContext`, `SkillCoolDownContext` — 상태 캡슐화 |
| **State** | `BattleStatus` — 전투 상태 전이 |
| **Sealed Types** | `Guardable`, `Endable` — 구현체 제한 |
| **Record** | `Reward`, `BattleTurn` — 불변 값 객체 |

---

## 다형성 & 상속 설계

### 인터페이스 계층

```
Deadible
  └─ Damageable          (damage 메서드 추가)
       └─ BattleParticipant  (전투 참여자 공통 계약)
            ├─ Character
            └─ Monster
```

`BattleParticipant` 는 `getAttackPower()`, `getCoolDownContext()`, `canUse()` 등을 정의하며, `Character` 와 `Monster` 가 각자 구현합니다. `Battle` 은 이 인터페이스만 알고 있어 **플레이어/몬스터를 동일하게 처리**합니다.

```java
// Battle.applyAttack — 타입 불문하고 동일한 코드로 동작
actor.consumeMp(skill.getMpCost());
actor.getCoolDownContext().register(skill);
target.damage(skill, damage);
```

### Skill 상속 트리

```
Skill (abstract, non-sealed)  ← Guardable (sealed interface)
  ├── ActiveSkill (abstract)  — 데미지 값, isGuarded() = true
  │     ├── PowerSlash / IronStrike          (전사)
  │     ├── DoubleShot / PiercingArrow       (궁수)
  │     ├── FireBall / IceSpear              (마법사)
  │     ├── RapidFire / HeadShot             (건슬링어)
  │     └── GoblinPunch / SlimeBash / ...    (몬스터)
  └── PassiveSkill (abstract) — isGuarded() = false
```

`Guardable` 이 **sealed interface (permits Skill)** 이므로 외부에서 임의로 구현하지 못합니다.  
`ActiveSkill` / `PassiveSkill` 이 sealed가 아닌 `non-sealed` 로 선언된 덕분에 하위 스킬 클래스를 자유롭게 추가할 수 있습니다.

### Item 상속 트리

```
Item (abstract)
  ├── WeaponItem  — attackPower, critical, WeaponType
  │     ├── IronSword / DoomBringer (MELEE)
  │     ├── ShadowBow               (BOW)
  │     ├── MagicStaff              (STAFF)
  │     └── ThunderstrikeGun        (RANGED)
  ├── ArmorItem   — defensePower, ArmorType
  │     ├── LeatherArmor / IronPlate / TitanArmor / DragonScaleMail  (CHEST)
  │     ├── FrostguardShield                                          (SHIELD)
  │     ├── Circlet                                                   (HEAD)
  │     ├── Gauntlet                                                  (HAND)
  │     └── Greaves                                                   (FEET)
  ├── PotionItem  — healAmount
  │     ├── HealthPotion / ManaPotion / EnergyDrink
  │     ├── HeartOfDragon / PhoenixFeather
  └── MaterialItem — (소재, use 미적용)
        ├── SlimeJelly / SkeletonBone / GoblinEar
        ├── OrcTooth / DragonScale
```

`Item.use(Character)` 가 추상 메서드이므로 각 아이템이 **자신의 효과를 직접 정의**합니다.  
`WeaponItem.use()` / `ArmorItem.use()` 는 `character.getLoadout().equip(this)` 를 호출해 장비 슬롯에 장착합니다.

### Job 상속 & 다형성

```
Job (abstract)
  ├── Warrior    — Stat(40, 0.00, 0, 120, 30) + PowerSlash, IronStrike
  ├── Mage       — Stat(20, 0.05, 0,  70, 120) + FireBall, IceSpear
  ├── Archer     — Stat(35, 0.20, 0,  90,  60) + DoubleShot, PiercingArrow
  └── Gunslinger — Stat(45, 0.10, 0,  85,  50) + RapidFire, HeadShot
```

`Job.createSkills()` 가 **Template Method** 패턴으로, `Character.setJob(job)` 호출 시 직업의 스킬 목록이 주입됩니다.

### Monster 상속 & 다형성

```
Monster  (implements BattleParticipant, Rewardable)
  ├── Slime / GiantSlime
  ├── Skeleton / Goblin / Orc
  ├── Harpy / Golem / Werewolf
  ├── Vampire      (BOSS)
  ├── AncientDragon (BOSS)
  └── MegaSkeleton  (BOSS)
```

`Monster.getDamage()` 에서 `MonsterType.BOSS` 여부에 따라 ×0.8 계수가 적용됩니다. 서브클래스는 생성자에서 스탯·스킬·리워드·EXP 만 정의하면 됩니다.

### Slot 인터페이스 — 장비 슬롯 다형성

```
Slot (interface)
  ├── WeaponSlot  — 단일 무기 슬롯
  └── ArmorSlot   — ArmorType 별 복수 슬롯 (HEAD/CHEST/HAND/FEET/SHIELD)
```

`Loadout.getItemsStat()` 은 `weaponSlot.getStat().plus(armorSlots.getStat())` 로 두 슬롯의 스탯을 합산합니다. `Slot` 인터페이스 덕분에 슬롯 구현을 교체하거나 추가할 수 있습니다.

---

## 직업

레벨업 공통 보너스: **ATK +5 / DEF +2 / HP +10 / MP +5** (레벨당)

| 직업 | ATK | 크리티컬 | DEF | HP | MP | 스킬 |
|------|-----|---------|-----|----|----|------|
| 전사 | 40 | 0% | 0 | 120 | 30 | 파워 슬래시(CD 1), 아이언 스트라이크 |
| 마법사 | 20 | 5% | 0 | 70 | 120 | 파이어 볼(CD 2), 아이스 스피어 |
| 궁수 | 35 | 20% | 0 | 90 | 60 | 더블 샷, 피어싱 애로우 |
| 건슬링어 | 45 | 10% | 0 | 85 | 50 | 래피드 파이어, 헤드샷 |

---

## 전투 시스템

### 턴 구조

```
[턴 시작]  전 참여자 MP +10, 쿨다운 tick
[플레이어] 스킬 선택 or 9번(포션 사용) → 크리티컬 판정 → 데미지 계산 → 적용 → 쿨다운 등록
[몬스터들] 사용 가능한 스킬 중 랜덤 선택 → 자동 공격 (플레이어 턴 후 연속 실행)
```

### 전투 중 포션 사용

스킬 선택 화면에서 `9` 를 입력하면 인벤토리의 포션 목록이 표시됩니다.

- `PotionItem` 타입만 필터링해 표시 (HP 포션 / MP 포션 구분 표기)
- 선택한 포션이 인벤토리에서 즉시 제거되고 효과 적용
- 포션 사용은 **턴을 소모하지 않음** — 사용 후 스킬 선택 화면으로 복귀
- 보유 포션이 없으면 안내 메시지 출력

```
스킬 선택 화면
  └─ 9 입력
       ├─ 포션 목록 표시 (HP +N / MP +N)
       ├─ 번호 선택 → ch.useItem() 호출 → 인벤토리에서 제거 + 효과 적용
       └─ 0. 돌아가기 → 스킬 선택 화면으로 복귀 (전투 상태 재렌더링)
```

### 데미지 공식

```
base   = max(1, 공격자 ATK + 스킬 데미지 - 방어자 DEF)
final  = critical ? base × 2 : base
```

크리티컬 판정은 **턴당 1회** `Character.rollCritical()` 에서 수행하며,
데미지 계산과 실제 적용에 **동일한 값**을 사용합니다.

### 전투 종료 조건

| 조건 | 결과 |
|------|------|
| 스테이지 내 모든 몬스터 사망 | 스테이지 클리어 → 다음 스테이지 |
| 마지막 스테이지 클리어 | 던전 정복 완료 + 보상 획득 |
| 플레이어 HP = 0 | 패배 → 던전 초기화, 체력 전량 회복 |

---

## 스킬 쿨타임

`SkillCoolDownContext` 가 **참여자별로** 쿨다운 상태를 관리합니다.  
`Character` 와 `Monster` 가 각각 독립적인 인스턴스를 보유합니다.

```
스킬 사용  → getCoolDownContext().register(skill)   [Battle.applyAttack]
턴 시작    → getCoolDownContext().tick()             [StageContext.progressTurn]
스테이지↑  → player.getCoolDownContext().clear()    [BattleStage.start]
           (몬스터는 스테이지마다 새 인스턴스 생성으로 자동 초기화)
```

### 쿨다운 의미 (CD = N)

| CD 값 | 효과 |
|-------|------|
| 0 | 쿨다운 없음 — 매 턴 사용 가능 |
| 1 | 사용 후 다음 턴 즉시 사용 가능 (tick 1→0으로 제거) |
| 2 | 사용 후 1턴 대기 후 사용 가능 |
| N | 사용 후 N-1턴 대기 |

스킬 선택 화면에서 쿨다운 중인 스킬은 번호 없이 `[쿨다운 N턴]` 으로 표시됩니다.

---

## 크리티컬

- `Stat.critical` (double 0.0 ~ 1.0) 로 확률 저장
- `Character.rollCritical()` — `Math.random() < critical` 로 판정
- 무기 장착 시 `WeaponItem.critical` 이 `WeaponSlot.getStat()` 을 통해 합산

| 직업 기본 | 값 |
|----------|---|
| 전사 | 0% |
| 마법사 | 5% |
| 건슬링어 | 10% |
| 궁수 | 20% |

---

## 장비 시스템

캐릭터는 `Loadout` 을 보유하며, `WeaponSlot` + `ArmorSlot` 으로 구성됩니다.

```
Loadout
  ├── WeaponSlot          — 무기 1개 (ATK, 크리티컬 제공)
  └── ArmorSlot           — 방어구 최대 5개 (ArmorType 별 슬롯)
                            HEAD / CHEST / HAND / FEET / SHIELD
```

아이템을 인벤토리에서 `사용(use)` 하면 `character.useItem()` 이 인벤토리에서 제거 후 `loadout.equip(this)` 를 호출해 장착합니다.  
이후 `Character.getTotalStat()` 이 `loadout.getItemsStat()` 을 포함해 계산합니다.

```
getTotalStat() = baseStat + levelBonus + job.getStat() + loadout.getItemsStat()
```

### 장착 교체 동작

동일 슬롯에 새 장비를 착용하면 기존 장비가 **인벤토리로 반환**됩니다.

| 슬롯 | 교체 시 동작 |
|------|------------|
| `WeaponSlot` | 기존 무기 → 인벤토리 반환 후 신규 무기 장착 |
| `ArmorSlot` (부위별) | 동일 `ArmorType` 의 기존 방어구 → 인벤토리 반환 후 신규 방어구 장착 |

### 무기 직업 제한

`WeaponItem.use()` 에서 캐릭터의 직업(`JobKind`)과 무기의 `WeaponType` 을 비교합니다.  
일치하지 않으면 장착이 거부되고 아이템은 인벤토리에 그대로 유지됩니다.

| 직업 | 사용 가능 무기 타입 |
|------|-----------------|
| 전사 (Warrior) | `MELEE` |
| 마법사 (Mage) | `STAFF` |
| 궁수 (Archer) | `BOW` |
| 건슬링어 (Gunslinger) | `RANGED` |

### 방어구 부위 제한

`ArmorSlot` 은 `ArmorType` 을 키로 사용하는 `Map<ArmorType, ArmorItem>` 으로 관리되어,  
부위당 하나의 방어구만 착용 가능합니다. 같은 부위의 새 방어구를 착용하면 기존 방어구가 인벤토리로 반환됩니다.

| ArmorType | 부위 |
|-----------|------|
| `HEAD` | 머리 |
| `CHEST` | 몸통 |
| `HAND` | 장갑 |
| `FEET` | 신발 |
| `SHIELD` | 방패 |

### 장착 아이템 확인 화면

메인 메뉴 **5번** 에서 현재 장착 중인 무기와 방어구를 확인할 수 있습니다.

- 무기 슬롯: 이름 / 공격력 / 크리티컬 표시 (미착용 시 `미착용` 표시)
- 방어구 슬롯: 5개 부위 전체 나열, 부위별 이름 / 방어력 표시

### 신규 캐릭터 초기 아이템

`InitialManager` 가 게임 시작 시 캐릭터에게 초기 아이템을 지급합니다.

| 아이템 | 조건 |
|--------|------|
| LeatherArmor (가죽 갑옷) | 모든 직업 공통 |
| ManaPotion (마나 포션) | 모든 직업 공통 |
| IronSword | 전사 (MELEE) |
| MagicStaff | 마법사 (STAFF) |
| ShadowBow | 궁수 (BOW) |
| ThunderstrikeGun | 건슬링어 (RANGED) |

`prepareForJob(job)` 이 직업의 `WeaponType` 을 기준으로 `initialWeapons` 맵에서 해당 무기를 골라 지급한다.

### 무기 목록

| 이름 | ATK | 크리티컬 | 타입 |
|------|-----|---------|------|
| IronSword | — | — | MELEE |
| DoomBringer | — | — | MELEE |
| ShadowBow | — | — | BOW |
| MagicStaff | — | — | STAFF |
| ThunderstrikeGun | — | — | RANGED |

### 방어구 목록

| 이름 | DEF | 슬롯 | 가격 |
|------|-----|------|------|
| LeatherArmor (가죽 갑옷) | 5 | CHEST | 100 G |
| IronPlate (철판 갑옷) | 15 | CHEST | 500 G |
| TitanArmor (타이탄 갑옷) | 30 | CHEST | 2000 G |
| DragonScaleMail (용린 갑옷) | 50 | CHEST | 5000 G |
| FrostguardShield (빙결 수호 방패) | 30 | SHIELD | 2000 G |
| Circlet (초보자의 서클릿) | 10 | HEAD | 200 G |
| Gauntlet (초보자의 건틀릿) | 3 | HAND | 200 G |
| Greaves (초보자의 그리브) | 5 | FEET | 100 G |

---

## 상점

`ItemManager` 가 판매 아이템 목록을 관리하며, `GameController.enterStore()` 가 구매 로직을 처리합니다.

```
메인 메뉴 → 상점
  └─ GameMenuView.showItemStore()   — 아이템 목록 표시 + 선택
  └─ GameController.enterStore()   — 골드 확인 → 차감 → 인벤토리 추가
```

- 골드가 부족하면 구매 불가 메시지 출력
- 구매한 아이템은 인벤토리에 추가됨
- 무기/방어구는 인벤토리에서 `사용` 해야 장착됨

---

## 던전 & 스테이지

| 던전 | 난이도 | 스테이지 구성 |
|------|--------|------------|
| 언노운 데이터 뱅크 | NORMAL | 자이언트 슬라임 (BOSS) |
| 백골의 요새 | NORMAL | 스켈레톤 → 스켈레톤×3 → 메가 스켈레톤 (BOSS) |
| 테스트 서버 No.4 | NORMAL | 고블린 + 슬라임 → 고대 드래곤 (BOSS) |
| 디버깅 가든 | NIGHTMARE | 스켈레톤×2 + 고블린 → 오크×2 → 고대 드래곤 (BOSS) |

| 난이도 | 드롭률 배율 |
|--------|-----------|
| EASY | × 0.8 |
| NORMAL | × 1.0 |
| HARD | × 1.3 |
| NIGHTMARE | × 1.6 |

**드롭률** — 던전 클리어 후 `Dungeon.claimRewards()` 에서 아이템마다 `Math.random() < dropRatio` 로 드롭 여부를 결정합니다 (기본 10%).

---

## 몬스터

| 몬스터 | 타입 | Lv | ATK | DEF | HP | MP | Gold | EXP |
|--------|------|----|-----|-----|----|----|------|-----|
| 슬라임 | NORMAL | 1 | 5 | 2 | 50 | 10 | 10 | 15 |
| 스켈레톤 | NORMAL | 1 | 8 | 3 | 30 | 0 | 20 | 20 |
| 자이언트 슬라임 | BOSS | 1 | 10 | 2 | 60 | 10 | 10 | 50 |
| 고블린 | NORMAL | 5 | 12 | 5 | 80 | 20 | 30 | 50 |
| 하피 | NORMAL | 15 | 35 | 15 | 300 | 100 | 100 | 150 |
| 오크 | NORMAL | 10 | 25 | 10 | 200 | 50 | 80 | 100 |
| 골렘 | NORMAL | 30 | 60 | 100 | 1500 | 0 | 200 | 500 |
| 워우울프 | NORMAL | 50 | 100 | 40 | 2500 | 200 | 300 | 800 |
| 뱀파이어 | BOSS | 80 | 120 | 60 | 4000 | 1000 | 1000 | 2000 |
| 고대 드래곤 | BOSS | 100 | 150 | 80 | 5000 | 1000 | 500 | 1000 |
| 메가 스켈레톤 | BOSS | 10 | 8 | 3 | 200 | 0 | 200 | 20 |

> BOSS 타입은 공격 데미지 **× 0.8** 감소.  
> 메가 스켈레톤은 처치 시 초보자의 그리브·건틀릿·서클릿을 드롭한다.

---

## 경험치 & 레벨업

```
필요 EXP = 현재 레벨 × 100
```

| 레벨 | 필요 EXP |
|------|---------|
| 1 → 2 | 100 |
| 2 → 3 | 200 |
| N → N+1 | N × 100 |

레벨업 효과: ATK **+5**, DEF **+2**, HP **+10**, MP **+5** (누적)  
레벨업 시 HP +10, MP +5 즉시 회복. 한 번에 여러 레벨업 가능.

---

## 보상 시스템

- 몬스터 처치마다 `RewardContext` 에 Gold + 아이템 누적
- 던전 완전 클리어 시 `claimRewards()` 호출 → 아이템에 드롭률 적용 후 캐릭터 지급
- 패배 시 누적 보상 소멸, 던전 초기화

---

## 프로젝트 구조

```
src/main/java/org/rpg/isekai/
│
├── Application.java                     # 진입점 (UTF-8 설정 포함)
│
├── ioc/
│   ├── Container.java                   # 싱글턴 컴포넌트 레지스트리
│   ├── Starter.java                     # 게임 시작 인터페이스
│   └── IsekaiBootApplication.java       # 부트 마커 애너테이션
│
├── controller/
│   ├── GameController.java              # 게임 루프, 상점 로직 (Starter 구현)
│   ├── IoCManager.java                  # Manager 순서 실행 & 컴포넌트 등록
│   ├── InitialManager.java              # 신규 캐릭터 초기 아이템 지급 목록 관리
│   ├── MonsterManager.java              # Supplier<Monster> 팩토리 레지스트리
│   ├── ItemManager.java                 # 상점 아이템 목록 관리
│   ├── DungeonManager.java              # BluePrint 기반 던전 조립 & 보관
│   ├── Manager.java                     # 매니저 생명주기 인터페이스
│   └── Registerar.java                  # 컴포넌트 등록 인터페이스
│
├── domain/
│   │
│   ├── battle/
│   │   ├── Battle.java                  # 턴 실행 엔진 (크리티컬·쿨다운 통합)
│   │   ├── BattleParticipant.java       # 전투 참여자 인터페이스 (canUse 포함)
│   │   ├── BattleStage.java             # 스테이지 (Stage 구현)
│   │   ├── BattleTurn.java              # 턴 결과 레코드 (critical 필드 포함)
│   │   ├── BattleStatus.java            # 전투 상태 enum
│   │   ├── Dungeon.java                 # 던전 (dropRatio, claimRewards)
│   │   ├── DungeonKind.java             # 던전 종류 enum
│   │   ├── DungeonDifficulty.java       # 난이도 enum (rewardMultiplier)
│   │   ├── StageBlueprint.java          # 스테이지 몬스터 팩토리 목록
│   │   ├── StageContext.java            # 배틀 상태 (MP회복·쿨다운 tick)
│   │   ├── SkillCoolDownContext.java    # 참여자별 스킬 쿨다운 추적
│   │   ├── RewardContext.java           # 던전 내 보상 누적
│   │   ├── Reward.java                  # 보상 record (gold, items)
│   │   └── Rewardable.java             # 보상 드롭 인터페이스
│   │
│   ├── character/
│   │   ├── Character.java               # 캐릭터 (EXP·레벨업·크리티컬·장비 통합)
│   │   ├── Stat.java                    # 스탯 (power, critical, defense, hp, mp)
│   │   ├── Inventory.java               # 아이템 보관
│   │   ├── Loadout.java                 # 장착 장비 (WeaponSlot + ArmorSlot)
│   │   ├── Slot.java                    # 장비 슬롯 인터페이스
│   │   ├── WeaponSlot.java              # 무기 단일 슬롯
│   │   └── ArmorSlot.java               # 방어구 복수 슬롯 (ArmorType 별)
│   │
│   ├── job/
│   │   ├── Job.java                     # 직업 추상 클래스 (createSkills 템플릿)
│   │   ├── JobKind.java                 # 직업 종류 enum (WeaponType 연결)
│   │   ├── Warrior.java / Mage.java / Archer.java / Gunslinger.java
│   │
│   ├── monster/
│   │   ├── Monster.java                 # 몬스터 기반 클래스 (BattleParticipant 구현)
│   │   ├── MonsterType.java             # NORMAL / BOSS enum
│   │   └── Slime / Skeleton / Goblin / Orc / Harpy / Golem
│   │       Werewolf / Vampire / AncientDragon / GiantSlime.java
│   │
│   ├── skill/
│   │   ├── Skill.java                   # 추상 스킬 (non-sealed, implements Guardable)
│   │   ├── Guardable.java               # sealed interface (permits Skill)
│   │   ├── ActiveSkill.java             # 데미지 스킬 추상 클래스
│   │   ├── PassiveSkill.java            # 패시브 스킬 추상 클래스
│   │   ├── character/                   # 플레이어 스킬 구현체 8종
│   │   └── monster/                     # 몬스터 스킬 구현체 9종
│   │
│   ├── item/
│   │   ├── Item.java                    # 아이템 추상 클래스 (use 추상 메서드)
│   │   ├── ItemType.java                # WEAPON / ARMOR / POTION / MATERIAL
│   │   ├── weaponItem/                  # WeaponItem + 구현체 5종 + WeaponType
│   │   ├── amorItem/                    # ArmorItem + 구현체 5종 + ArmorType
│   │   ├── potionItem/                  # PotionItem + 구현체 5종
│   │   └── materialItem/               # MaterialItem + 구현체 5종
│   │
│   └── system/
│       ├── Game.java                    # 게임 상태 (non-sealed, implements Endable)
│       ├── Endable.java                 # sealed interface (permits Game)
│       └── UsernameValidator.java       # 이름 중복·길이 검증
│
└── view/
    ├── TitleView.java                   # 타이틀 아스키 아트 & 오프닝 스크립트
    ├── CharacterSetupView.java          # 이름 입력 & 직업 선택
    ├── GameMenuView.java                # 메인 메뉴 · 정보 · 인벤토리 · 장착 아이템 · 상점
    ├── DungeonBattleView.java           # 전투 화면 (스킬 선택 · 포션 사용 · 직업별 아스키 아트)
    └── ConsoleUtils.java                # clear · typewrite · sleep · Scanner
```

---

*이 세계를 정복하려면 먼저 자신부터 갈고닦으십시오.*
