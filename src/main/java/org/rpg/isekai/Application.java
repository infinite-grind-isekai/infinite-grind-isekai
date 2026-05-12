package org.rpg.isekai;

import org.rpg.isekai.controller.IsekaiApplication;
import org.rpg.isekai.ioc.IsekaiBootApplication;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

@IsekaiBootApplication
public class Application {
    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        IsekaiApplication.run(Application.class);
    }
}
