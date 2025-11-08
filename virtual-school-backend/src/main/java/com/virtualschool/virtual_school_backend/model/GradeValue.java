package com.virtualschool.virtual_school_backend.model;

public enum GradeValue {
    INSUFFICIENT(1),
    PASSABLE(2),
    SUFFICIENT(3),
    GOOD(4),
    VERY_GOOD(5),
    EXCELLENT(6);

    private final int value;

    GradeValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static GradeValue fromValue(int value) {
        for (GradeValue gradeValue : values()) {
            if (gradeValue.value == value) {
                return gradeValue;
            }
        }
        throw new IllegalArgumentException("Invalid grade value: " + value);
    }
}
