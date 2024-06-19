package com.github.dsadriel.spectre.enums;

public enum ArmorVisibility {
    BOOTS,
    HIDDEN,
    VISIBLE;

    public static ArmorVisibility fromString(String visibility) {
        for (ArmorVisibility value : ArmorVisibility.values()) {
            if (value.name().equalsIgnoreCase(visibility)) {
                return value;
            }
        }
        return null;
    }
}
