package com.rapchen.sanguosha;

import com.rapchen.sanguosha.core.Engine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SanguoshaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SanguoshaApplication.class, args);
        Engine engine = new Engine();
        engine.gameStart();
    }

}
