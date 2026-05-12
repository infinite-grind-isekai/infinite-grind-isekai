package org.rpg.isekai.view;

import org.rpg.isekai.domain.job.*;
import org.rpg.isekai.domain.system.UsernameValidator;

public class CharacterSetupView {

    private static final Job[] JOBS = {
        new Warrior(),
        new Mage(),
        new Archer(),
        new Gunslinger()
    };

    // ── 이름 입력 ───────────────────────────────────────────────────────────────

    public static String showUsernameInput() {
        while (true) {
            ConsoleUtils.clear();
            System.out.println();
            System.out.println("     ┌─────────────────────────────────────────────────────┐");
            System.out.println("     │                                                     │");
            System.out.println("     │              ★   소 환 자 등 록   ★                │");
            System.out.println("     │                                                     │");
            System.out.println("     │   이 세계에서 사용할 이름을 입력하세요.              │");
            System.out.println("     │                                                     │");
            System.out.println("     │   [ 규칙 ]  영문·한글  3 ~ 8 글자                  │");
            System.out.println("     │            중복된 이름은 사용할 수 없습니다.         │");
            System.out.println("     │                                                     │");
            System.out.println("     └─────────────────────────────────────────────────────┘");
            System.out.println();
            System.out.print("     이름 > ");

            String input = ConsoleUtils.SCANNER.nextLine().trim();

            if (UsernameValidator.isValid(input)) {
                return input;
            }

            System.out.println();
            System.out.println("     [ ! ] 사용할 수 없는 이름입니다. 다시 입력하세요.");
            ConsoleUtils.sleep(1200);
        }
    }

    // ── 직업 선택 ───────────────────────────────────────────────────────────────

    public static Job showJobSelection() {
        while (true) {
            ConsoleUtils.clear();
            printJobTable();
            System.out.println();
            System.out.print("     직업 선택 > ");

            String input = ConsoleUtils.SCANNER.nextLine().trim();

            switch (input) {
                case "1" -> { return JOBS[0]; }
                case "2" -> { return JOBS[1]; }
                case "3" -> { return JOBS[2]; }
                case "4" -> { return JOBS[3]; }
                default  -> {
                    System.out.println("     [ ! ] 1 ~ 4 중에서 선택하세요.");
                    ConsoleUtils.sleep(900);
                }
            }
        }
    }

    // ── 내부 렌더링 ─────────────────────────────────────────────────────────────

    private static void printJobTable() {
        System.out.println();
        System.out.println("     ┌─────────────────────────────────────────────────────────────────┐");
        System.out.println("     │                   ★   직 업 을 선 택 하 세 요   ★              │");
        System.out.println("     ├─────────────────────────────────────────────────────────────────┤");
        System.out.println("     │                                                                 │");

        for (int i = 0; i < JOBS.length; i++) {
            Job job = JOBS[i];
            System.out.printf(
                "     │   [ %d ] %-8s   ATK %3d   DEF %3d   HP %3d   MP %3d        │%n",
                i + 1,
                job.getName(),
                job.getStat().getPower(),
                job.getStat().getDefense(),
                job.getStat().getHp(),
                job.getStat().getMp()
            );
        }

        System.out.println("     │                                                                 │");
        System.out.println("     ├─────────────────────────────────────────────────────────────────┤");
        System.out.println("     │   스킬                                                          │");
        System.out.println("     │   전사      : 파워슬래시, 아이언스트라이크                      │");
        System.out.println("     │   마법사    : 파이어볼, 아이스스피어                            │");
        System.out.println("     │   궁수      : 더블샷, 피어싱애로우                              │");
        System.out.println("     │   건슬링어  : 래피드파이어, 헤드샷                              │");
        System.out.println("     │                                                                 │");
        System.out.println("     └─────────────────────────────────────────────────────────────────┘");
    }
}
