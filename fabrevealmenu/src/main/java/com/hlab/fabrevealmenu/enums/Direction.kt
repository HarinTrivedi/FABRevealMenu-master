package com.hlab.fabrevealmenu.enums

enum class Direction(private val id: Int) {
    LEFT(0), UP(1), RIGHT(2), DOWN(3);

    companion object {
        fun fromId(id: Int): Direction {
            for (f in values()) {
                if (f.id == id) return f
            }
            throw IllegalArgumentException()
        }
    }
}
