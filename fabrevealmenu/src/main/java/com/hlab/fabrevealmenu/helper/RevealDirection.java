package com.hlab.fabrevealmenu.helper;

public enum RevealDirection {
    LEFT(0), UP(1), RIGHT(2), DOWN(3);
    private int id;

    RevealDirection(int id) {
        this.id = id;
    }

    public static RevealDirection fromId(int id) {
        for (RevealDirection f : values()) {
            if (f.id == id) return f;
        }
        throw new IllegalArgumentException();
    }
}
