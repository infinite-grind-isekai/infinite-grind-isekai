# 컨트롤러 / 매니저 시스템 상세 문서

## 개요

이 프로젝트는 스프링 없이 **수동 DI(Dependency Injection)** 를 구현한다.  
`Container` (싱글톤) → `IoCManager` (조율자) → `Manager` 구현체들의 계층으로 구동된다.

```
Application.main()
  └─ Container.getInstance().reset()
       └─ IoCManager.run()
            └─ IoCManager.init()
                 ├─ 1단계: 모든 Manager를 getOrder() 순서로 prepare()
                 └─ 2단계: Registerar 구현체의 register() 결과를 Container에 등록
```

---

## 1. Container — 싱글톤 레지스트리

```java
public class Container {
    private static Container INSTANCE;
    private final IoCManager iocManager = new IoCManager();
    private final Map<Class<?>, List<Object>> components = new HashMap<>();

    public static Container getInstance() {
        if (INSTANCE == null) INSTANCE = new Container();
        return INSTANCE;
    }

    public void reset()  { iocManager.run(); }

    public void register(Class<?> componentClass, Object component) {
        components.put(componentClass, List.of(component));
    }

    public <T> T get(Class<T> componentClass) {
        return (T) components.get(componentClass).get(0);
    }
}
```

### 역할

- 애플리케이션 전역에서 컴포넌트를 타입으로 조회하는 레지스트리다.
- `reset()` 을 호출하면 `IoCManager.run()` 이 실행되어 모든 매니저가 초기화되고 컴포넌트가 등록된다.
- `get(Starter.class)` 로 게임 진입점(`GameController`)을 꺼내 `start()` 를 호출한다.

### 설계 결정: 지연 초기화 싱글톤

`INSTANCE` 가 `null` 일 때만 생성한다(Lazy Initialization).  
멀티스레드 안전성은 고려하지 않았는데, 이 게임은 단일 스레드로 동작하므로 충분하다.  
`reset()` 으로 재초기화가 가능해, 테스트나 게임 재시작 시 유용하다.

### 설계 결정: Class<?> 를 Key로 사용하는 이유

스프링의 `@Autowired` 와 유사하게, **타입으로 컴포넌트를 조회**한다.  
문자열 이름 대신 타입을 Key로 쓰면 오타 위험이 없고 컴파일 타임에 안전하다.  
`components` 값이 `List<Object>` 인 이유는 동일 타입의 복수 컴포넌트를 지원하기 위한 구조적 여지이나,  
현재 구현에서는 항상 크기 1의 리스트로 저장한다.

---

## 2. Manager 인터페이스 — 생명주기 계약

```java
public interface Manager {
    int getOrder();
    boolean needPrepare();
    void prepare();
}
```

| 메서드 | 역할 |
|--------|------|
| `getOrder()` | 준비 실행 순서. 낮을수록 먼저 실행된다 |
| `needPrepare()` | `false` 이면 `prepare()` 를 건너뛴다 |
| `prepare()` | 실제 초기화 로직 (팩토리 등록, 데이터 조립 등) |

### 설계 결정: needPrepare() 로 선택적 초기화

`ItemManager` 는 생성자에서 상점 목록을 바로 구성하므로 `prepare()` 가 필요 없다(`needPrepare() = false`).  
`MonsterManager`, `DungeonManager`, `InitialManager` 는 `prepare()` 에서 실제 데이터를 구성한다.  
인터페이스가 `prepare()` 를 강제하되, `needPrepare()` 로 호출 여부를 선택할 수 있게 해  
매니저마다 초기화 방식이 달라도 통일된 생명주기로 관리한다.

---

## 3. IoCManager — 조율자

```java
public class IoCManager {

    private final List<Manager> managers;
    private final List<Registerar> registerars;
    private final Starter starter;

    public IoCManager() {
        MonsterManager monsterManager = new MonsterManager();
        ItemManager    itemManager    = new ItemManager();
        DungeonManager dungeonManager = new DungeonManager(monsterManager);
        InitialManager initialManager = new InitialManager();

        List<Manager> tmpList = List.of(monsterManager, dungeonManager, itemManager, initialManager);

        managers    = tmpList.stream()
                             .sorted(Comparator.comparingInt(Manager::getOrder))
                             .toList();
        registerars = tmpList.stream()
                             .filter(c -> c instanceof Registerar)
                             .map(c -> (Registerar) c)
                             .toList();

        starter = new GameController(dungeonManager, itemManager, initialManager);
    }

    private void init() {
        // 1단계: 순서대로 prepare
        for (Manager manager : managers) {
            if (manager.needPrepare()) manager.prepare();
        }

        // 2단계: Registerar가 반환한 객체를 Container에 등록
        for (Registerar registerar : registerars) {
            for (Object component : registerar.register()) {
                Container.getInstance().register(component.getClass(), component);
            }
        }

        // 3단계: 게임 진입점 등록
        Container.getInstance().register(Starter.class, starter);
    }
}
```

### 실행 순서

```
MonsterManager.prepare()    (order=10)  ← Supplier<Monster> 팩토리 등록
InitialManager.prepare()    (order=0)   ← 초기 지급 아이템 구성
ItemManager    (needPrepare=false)      ← 생성자에서 이미 완료, skip
DungeonManager.prepare()    (order=50)  ← 팩토리를 참조해 던전 조립
```

> **주의**: `DungeonManager` 는 `MonsterManager` 의 팩토리를 사용하므로 반드시 `MonsterManager` 이후에 실행되어야 한다. `getOrder()` 값으로 이 의존 순서를 보장한다.

### 설계 결정: 생성자에서 의존성 직접 연결

`DungeonManager(monsterManager)` 처럼 생성자 주입으로 의존성을 명시한다.  
스프링의 `@Autowired` 없이도 컴파일 타임에 의존 관계가 드러난다.  
`GameController` 도 동일하게 `dungeonManager`, `itemManager`, `initialManager` 를 주입받는다.

### 설계 결정: 두 단계 분리 (prepare → register)

모든 매니저가 `prepare()` 를 완료한 뒤에 `register()` 가 실행된다.  
만약 한 매니저가 `prepare()` 와 `register()` 를 섞어서 실행하면,  
다른 매니저의 초기화가 완료되지 않은 상태에서 Container에 등록이 시도될 수 있다.  
단계를 분리함으로써 **초기화 완료 보장 후 등록**이 이루어진다.

---

## 4. 각 Manager 상세

### MonsterManager (order=10)

```java
private final Map<Class<? extends Monster>, Supplier<Monster>> factories = new HashMap<>();

public void prepare() {
    factories.put(Slime.class,        Slime::new);
    factories.put(Skeleton.class,     Skeleton::new);
    factories.put(Goblin.class,       Goblin::new);
    // ...
}

public Supplier<Monster> getFactory(Class<? extends Monster> type) {
    Supplier<Monster> factory = factories.get(type);
    if (factory == null) throw new IllegalArgumentException(...);
    return factory;
}
```

- `Class<? extends Monster>` 를 Key로 `Supplier<Monster>` 를 값으로 보관한다.
- `Supplier` 는 **지연 생성**을 담당한다. 팩토리를 호출할 때마다 새 몬스터 인스턴스가 만들어진다.
- `Manager` 만 구현하고 `Registerar` 는 구현하지 않는다. Container에 직접 등록할 것이 없고, `DungeonManager` 가 팩토리를 참조해 사용하기 때문이다.

### ItemManager (order=0, needPrepare=false)

```java
public class ItemManager implements Manager {
    private final List<Item> storeItems;

    public ItemManager() {
        storeItems = List.of(
            new HealthPotion(), new ManaPotion(), /* ... */
            new IronSword(), new MagicStaff(),    /* ... */
            new LeatherArmor(), new IronPlate(),  /* ... */
        );
    }
}
```

- 상점 아이템 목록은 정적이므로 생성자에서 한 번만 구성한다.
- `prepare()` 가 불필요해 `needPrepare() = false`.
- `GameController` 가 직접 주입받아 `showItemStore()` 에 전달한다.

### DungeonManager (order=50)

```java
public void prepare() {
    Map<DungeonKind, List<StageBlueprint>> blueprints = buildBlueprints();
    for (DungeonKind kind : DungeonKind.values()) {
        List<BattleStage> stages = /* 블루프린트 → BattleStage 변환 */;
        dungeons.put(kind, new Dungeon(kind.getName(), kind.getDifficulty(), stages));
    }
}

private Map<DungeonKind, List<StageBlueprint>> buildBlueprints() {
    Supplier<Monster> goblin = monsterManager.getFactory(Goblin.class);
    // ...
    return Map.of(
        DungeonKind.DEBUGGING_GARDEN, List.of(
            new StageBlueprint(List.of(skeleton, skeleton, goblin)),
            new StageBlueprint(List.of(orc, orc)),
            new StageBlueprint(List.of(dragon))
        )
    );
}
```

- `MonsterManager` 에서 팩토리(`Supplier`)를 꺼내 `StageBlueprint` 에 등록한다.
- `prepare()` 완료 후 `dungeons` 맵이 완성되며, 이후 조회만 허용한다(`getDungeons()` 는 unmodifiableMap 반환).
- `Registerar` 를 구현해 완성된 던전 목록을 Container에 등록하지만, 현재 게임 코드에서 Container를 통해 던전을 꺼내지는 않는다. `GameController` 가 직접 주입받은 `dungeonManager` 를 사용한다.

### InitialManager (order=0)

```java
public class InitialManager implements Manager {
    private final List<Item> initialItems = new ArrayList<>();

    public void prepare() {
        initialItems.add(new LeatherArmor());
    }
}
```

- 신규 캐릭터 생성 시 지급할 아이템 목록을 보관한다.
- `GameController.newGame()` 에서 `initialManager.getInitialItems()` 를 순회해 `character.obtainItem()` 으로 지급한다.
- 초기 지급 아이템을 추가하거나 변경하려면 `prepare()` 만 수정하면 된다.

---

## 5. Registerar 인터페이스

```java
public interface Registerar {
    List<Object> register();
}
```

`Manager` 중 `Registerar` 를 함께 구현한 것은 `DungeonManager` 뿐이다.  
`IoCManager.init()` 2단계에서 `Registerar` 구현체만 필터링해 `register()` 를 호출한다.  
`Manager` 와 `Registerar` 를 분리한 이유: 모든 매니저가 컴포넌트를 등록할 필요는 없다.  
`MonsterManager` 와 `ItemManager` 는 외부에서 Container를 통해 조회되지 않으므로 `Registerar` 가 불필요하다.

---

## 6. Starter 인터페이스

```java
public interface Starter {
    void start();
}
```

`GameController` 가 `Starter` 를 구현하며, `Container.get(Starter.class).start()` 로 게임 루프가 시작된다.  
`GameController` 의 구체 타입을 외부에 노출하지 않고 인터페이스만 노출함으로써,  
진입점 구현체를 교체하더라도 부팅 코드(`IsekaiApplication`)는 변경할 필요가 없다.
