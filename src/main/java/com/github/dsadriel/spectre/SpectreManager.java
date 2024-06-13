package com.github.dsadriel.spectre;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.github.dsadriel.spectre.enums.ArmorVisibility;
import com.github.dsadriel.spectre.enums.SpectreMode;

public class SpectreManager {

    private final Map<UUID, PlayerOptions> playerOptionsMap = new HashMap<>();
    private final Map<UUID, Map<Integer, UUID>> nearbyPlayersMap = new HashMap<>();

    public SpectreManager() {
    }

    /**
     * Enables Spectre mode for a player.
     * 
     * @param player the player to enable Spectre mode for
     */
    public void enableSpectre(Player player) {
        PlayerOptions options = getPlayerOptions(player);
        options.setEnabled(true);
        playerOptionsMap.put(player.getUniqueId(), options);
    }

    /**
     * Disables Spectre mode for a player.
     * 
     * @param player the player to disable Spectre mode for
     */
    public void disableSpectre(Player player) {
        PlayerOptions options = getPlayerOptions(player);
        SpectreApply.showAllPlayers(player);
        setNearbyMap(player, List.of());
        options.setEnabled(false);
        playerOptionsMap.put(player.getUniqueId(), options);
    }

    /**
     * Sets the Spectre mode for a player.
     * 
     * @param player the player to set the mode for
     * @param mode   the Spectre mode to set
     */
    public void setMode(Player player, SpectreMode mode) {
        PlayerOptions options = getPlayerOptions(player);
        options.setMode(mode);
    }

    /**
     * Sets the armor visibility for a player.
     * 
     * @param player     the player to set the armor visibility for
     * @param visibility the armor visibility to set
     */
    public void setArmorVisibility(Player player, ArmorVisibility visibility) {
        PlayerOptions options = getPlayerOptions(player);
        options.setArmorVisibility(visibility);
    }

    public void load() {
        // Load player options from persistent storage
    }

    public void save() {
        // Save player options to persistent storage
    }

    /**
     * Gets the player options for a player.
     * 
     * @param player the player to get the options for
     * @return the player options
     */
    public PlayerOptions getPlayerOptions(Player player) {
        return playerOptionsMap.getOrDefault(player.getUniqueId(), new PlayerOptions(false, SpectreMode.INVISIBLE, ArmorVisibility.VISIBLE));
    }

    /**
     * Gets the nearby map of players for a player.
     * 
     * @param player the player to get the nearby map for
     * @return the map of nearby players
     */
    public Map<Integer, UUID> getNearbyMap(Player player) {
        return nearbyPlayersMap.getOrDefault(player.getUniqueId(), new HashMap<>());
    }

    /**
     * Sets the nearby map of players for a player.
     * 
     * @param player the player to set the nearby map for
     * @param nearby the list of nearby players
     */
    public void setNearbyMap(Player player, List<Player> nearby) {
        Map<Integer, UUID> nearbyPlayers = new HashMap<>();
        if (nearby != null) {
            for (Player p : nearby) {
                nearbyPlayers.put(p.getEntityId(), p.getUniqueId());
            }
        }
        nearbyPlayersMap.put(player.getUniqueId(), nearbyPlayers);
    }
}
