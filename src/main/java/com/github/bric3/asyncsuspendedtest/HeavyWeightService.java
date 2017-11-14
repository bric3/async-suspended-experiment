package com.github.bric3.asyncsuspendedtest;

import org.springframework.stereotype.Component;

@Component
public class HeavyWeightService {

    public String waitForIt() {
        return "...";
    }
}
