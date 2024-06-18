package com.github.dsadriel.spectre;

import org.jetbrains.annotations.Nullable;

import com.github.dsadriel.spectre.enums.*;


public class PlayerOptions {
    private SpectreMode mode;
    private ArmorVisibility armorVisibility;
    private Boolean enabled = false;
    private Double radius;

    public PlayerOptions(Boolean enabled, @Nullable SpectreMode mode, @Nullable ArmorVisibility armorVisibility, Double radius) {
        this.enabled = enabled;
        this.mode = mode == null ? SpectreMode.GHOST : mode;
        this.armorVisibility = armorVisibility == null ? ArmorVisibility.BOOTS : armorVisibility;
        this.radius = radius;
    }

    public SpectreMode getMode() {
        return mode;
    }

    public void setMode(SpectreMode mode) {
        this.mode = mode;
    }

    public ArmorVisibility getArmorVisibility() {
        return armorVisibility;
    }

    public void setArmorVisibility(ArmorVisibility armorVisibility) {
        this.armorVisibility = armorVisibility;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Double getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return "PlayerOptions{" +
                "mode=" + mode +
                ", armorVisibility=" + armorVisibility +
                ", enabled=" + enabled +
                ", radius=" + radius +
                '}';
    }

}
