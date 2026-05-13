package org.rpg.isekai.domain.battle;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DungeonKind {
    UNKNOWN_DATA_BANK("언노운 데이터 뱅크", DungeonDifficulty.NORMAL),
    BONE_FORTRESS("백골의 요새", DungeonDifficulty.NORMAL),
    TEST_SERVER_NO4("테스트 서버 No.4", DungeonDifficulty.NORMAL),
    DEBUGGING_GARDEN("디버깅 가든", DungeonDifficulty.NIGHTMARE);

    private final String name;
    private final DungeonDifficulty difficulty;
}
