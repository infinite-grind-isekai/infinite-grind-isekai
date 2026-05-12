package org.rpg.isekai.view;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ConsoleUtils {

    public static final Scanner SCANNER = new Scanner(System.in, StandardCharsets.UTF_8);

    private ConsoleUtils() {}

    public static void clear() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("\n".repeat(40));
        }
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void typewrite(String text, long delayMs) {
        for (char c : text.toCharArray()) {
            System.out.print(c);
            sleep(delayMs);
        }
        System.out.println();
    }

    public static void typewrite(String text) {
        typewrite(text, 28);
    }

    public static void blankLines(int count) {
        for (int i = 0; i < count; i++) System.out.println();
    }
}
