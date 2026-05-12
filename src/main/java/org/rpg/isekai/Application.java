package org.rpg.isekai;

import org.rpg.isekai.controller.IsekaiApplication;
import org.rpg.isekai.ioc.IsekaiBootApplication;

@IsekaiBootApplication
public class Application {
    public static void main(String[] args) {
        IsekaiApplication.run(Application.class);
    }
}
