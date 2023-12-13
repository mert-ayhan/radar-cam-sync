package com.github.mertayhan.radarcamsync.kafka;

import java.util.Arrays;

public class MessageProcessor {
    public static double[] parsePositionFromString(String text) {
        return Arrays.stream(text.split(","))
                .mapToDouble(Double::parseDouble)
                .toArray();
    }
}
