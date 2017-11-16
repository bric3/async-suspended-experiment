package com.github.bric3.asyncsuspendedtest;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.util.concurrent.TimeUnit.SECONDS;

@Component
public class HeavyWeightService {

    @SneakyThrows
    public String waitForIt(Short waitParam) {
        try {
            int wait = Optional.ofNullable(waitParam).map(Short::intValue).orElse(15);
            System.out.println(now() + " --> waitForIt : " + Thread.currentThread().getName());
            System.out.println(now() + " --> waitForIt : " + wait);
            SECONDS.sleep(wait);
            return "...";
        } finally {
            System.out.println(now() + " <-- waitForIt : " + Thread.currentThread().getName());
        }
    }
}
