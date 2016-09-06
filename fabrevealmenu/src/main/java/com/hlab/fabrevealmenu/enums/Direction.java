package com.hlab.fabrevealmenu.enums;

public enum Direction {
    LEFT(0), UP(1), RIGHT(2), DOWN(3);
    private int id;

    Direction(int id) {
        this.id = id;
    }

    public static Direction fromId(int id) {
        for (Direction f : values()) {
            if (f.id == id) return f;
        }
        throw new IllegalArgumentException();
    }
}
