package com.engine.dawnstar.utils.math;

public enum Direction {

    NORTH(0),
    SOUTH(1),
    EAST(2),
    WEST(3),
    UP(4),
    DOWN(5);

    public final int value;

    Direction(int value) {
        this.value = value;
    }
}
