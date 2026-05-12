package org.rpg.isekai.view;

import java.util.Scanner;

public class TitleView {

    private static final Scanner SCANNER = new Scanner(System.in);

    // ── 타이틀 아스키 아트 ──────────────────────────────────────────────────────

    private static final String[] PORTAL_ART = {
        "                  *    .       .    *       .    *",
        "              .       *    .       *    .       *   .",
        "           .     .·´¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯`·.     .",
        "         *     ·´                             `·     *",
        "       .      /    *   .   *    .   *    .   *  \\     .",
        "      *      |                                   |     *",
        "      .      |        이  세  계  의              |     .",
        "      *      |                                   |     *",
        "      .      |    ⚔    문  이  열  린  다    ⚔   |     .",
        "      *      |                                   |     *",
        "       .      \\    *   .   *    .   *    .   *  /     .",
        "         *     `·.                           .·´     *",
        "           .     `·._____________________  .·´     .",
        "              .       *    .       *    .       *   .",
        "                  *    .       .    *       .    *",
    };

    private static final String[] TITLE_BOX = {
        "        ╔══════════════════════════════════════════════════════╗",
        "        ║                                                      ║",
        "        ║   ⚔   무한노가다로 시작하는 이세계정복기   ⚔          ║",
        "        ║                                                      ║",
        "        ║          Infinite Grind  Isekai  Chronicles          ║",
        "        ║                                                      ║",
        "        ╚══════════════════════════════════════════════════════╝",
    };

    // ── 오프닝 스크립트 ─────────────────────────────────────────────────────────

    private static final String[][] SCRIPT_SCENES = {
        {
            "  ──────────────────────────────────────────────",
            "                      # 서 장",
            "  ──────────────────────────────────────────────",
        },
        {
            "",
            "  2XX4년, 봄.",
            "",
            "  야근을 마치고 퇴근하던 평범한 직장인 A씨.",
            "  피곤한 눈을 비비며 횡단보도를 건너던 중...",
        },
        {
            "",
            "  ...트럭.",
            "",
        },
        {
            "  ──────────────────────────────────────────────",
            "  [ SYSTEM ] 치명적 손상이 감지되었습니다.",
            "  [ SYSTEM ] 사망 판정 처리 중 . . .",
            "  ──────────────────────────────────────────────",
        },
        {
            "",
            "  [ SYSTEM ] 이세계 소환 조건 충족.",
            "  [ SYSTEM ] 대상자를 이세계로 이동합니다.",
            "  [ SYSTEM ] 소환 처리 중 . . .",
            "",
        },
        {
            "  ──────────────────────────────────────────────",
            "  [ SYSTEM ] 이동 완료.",
            "  [ SYSTEM ] 신규 이세계인 등록이 완료되었습니다.",
            "  ──────────────────────────────────────────────",
        },
        {
            "",
            "  눈을 떠보니, 낯선 초원.",
            "  수상한 반투명 창문이 눈앞에 펼쳐진다.",
            "",
        },
        {
            "   ┌─────────────────────────────────────────┐",
            "   │                                         │",
            "   │    ★   이 세 계 시 스 템   ★           │",
            "   │                                         │",
            "   │   [ 소환자 정보 ]                        │",
            "   │     이름  :  ???                        │",
            "   │     직업  :  ???                        │",
            "   │     레벨  :  1                          │",
            "   │                                         │",
            "   │   이 세계를 위협하는 던전을              │",
            "   │   모두 정복하는 것이 당신의 사명입니다.  │",
            "   │                                         │",
            "   │   단 한 가지, 경고가 있습니다.           │",
            "   │                                         │",
            "   │   \" 엄청나게 많이 갈아야 합니다. \"      │",
            "   │                                         │",
            "   └─────────────────────────────────────────┘",
        },
        {
            "",
            "  당신의 무한 노가다가 지금 시작됩니다.",
            "",
        },
    };

    // ── 메인 메뉴 ──────────────────────────────────────────────────────────────

    private static final String[] MAIN_MENU = {
        "",
        "        ╔═══════════════════════════════════╗",
        "        ║           [ 메 인 메 뉴 ]          ║",
        "        ╠═══════════════════════════════════╣",
        "        ║                                   ║",
        "        ║    1.  새 게임 시작                ║",
        "        ║    0.  종 료                       ║",
        "        ║                                   ║",
        "        ╚═══════════════════════════════════╝",
        "",
        "        선택 > ",
    };

    // ── public API ─────────────────────────────────────────────────────────────

    public static void showTitle() {
        ConsoleUtils.clear();
        ConsoleUtils.blankLines(1);
        for (String line : PORTAL_ART) {
            System.out.println(line);
            ConsoleUtils.sleep(40);
        }
        ConsoleUtils.blankLines(1);
        for (String line : TITLE_BOX) {
            System.out.println(line);
        }
        ConsoleUtils.blankLines(2);
        System.out.println("                         ▶   PRESS  ENTER  ◀");
        ConsoleUtils.blankLines(1);
        SCANNER.nextLine();
    }

    public static void showOpeningScript() {
        ConsoleUtils.clear();
        for (String[] scene : SCRIPT_SCENES) {
            for (String line : scene) {
                if (line.startsWith("  [ SYSTEM ]") || line.startsWith("  ──")) {
                    ConsoleUtils.typewrite(line, 18);
                } else if (line.isBlank()) {
                    System.out.println();
                    ConsoleUtils.sleep(300);
                } else if (line.contains("트럭")) {
                    ConsoleUtils.sleep(600);
                    ConsoleUtils.typewrite(line, 60);
                    ConsoleUtils.sleep(800);
                } else {
                    ConsoleUtils.typewrite(line, 28);
                }
            }
            ConsoleUtils.sleep(400);
        }
        ConsoleUtils.blankLines(1);
        System.out.print("  [ ENTER 를 눌러 계속 ]");
        SCANNER.nextLine();
    }

    public static void showMainMenu() {
        ConsoleUtils.clear();
        ConsoleUtils.blankLines(3);
        for (String line : PORTAL_ART) {
            System.out.println(line);
        }
        for (int i = 0; i < MAIN_MENU.length - 1; i++) {
            System.out.println(MAIN_MENU[i]);
        }
        System.out.print(MAIN_MENU[MAIN_MENU.length - 1]);
    }
}
