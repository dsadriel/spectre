package com.github.dsadriel.spectre.enums;

public enum SpectreMode {
    GHOST,
    INVISIBLE,
    VANISH;

    public static SpectreMode fromString(String mode) {
        for (SpectreMode value : SpectreMode.values()) {
            if (value.name().equalsIgnoreCase(mode)) {
                return value;
            }
        }
        return null;
    }
}
