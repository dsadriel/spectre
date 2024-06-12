package com.github.dsadriel.spectre;

import org.jetbrains.annotations.Nullable;

import com.github.dsadriel.spectre.enums.*;


public class PlayerOptions {
    SpectreMode mode;
    ArmorVisibility armorVisibility;
    Boolean enabled = false;

    public PlayerOptions(Boolean enabled, @Nullable SpectreMode mode, @Nullable ArmorVisibility armorVisibility) {
        this.enabled = enabled;
        this.mode = mode == null ? SpectreMode.GHOST : mode;
        this.armorVisibility = armorVisibility == null ? ArmorVisibility.BOOTS : armorVisibility;
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


}
