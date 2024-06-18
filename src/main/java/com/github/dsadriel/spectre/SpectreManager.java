package com.github.dsadriel.spectre;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import com.github.dsadriel.spectre.enums.ArmorVisibility;
import com.github.dsadriel.spectre.enums.SpectreMode;

/**
 * The `SpectreManager` class manages the Spectre mode functionality for players.
 * It provides methods to enable/disable Spectre mode, set Spectre mode and armor visibility,
 * and retrieve player options and nearby player maps.
 */
public class SpectreManager {

    private final Map<UUID, PlayerOptions> playerOptionsMap = new HashMap<>();
    private final Map<UUID, Map<Integer, UUID>> nearbyPlayersMap = new HashMap<>();
    private ArmorVisibility defaultArmorVisibility = ArmorVisibility.BOOTS;
    private SpectreMode defaultMode = SpectreMode.GHOST;
    private Double defaultRadius = 5.0;

    /**
     * Constructs a new `SpectreManager` instance with the default armor visibility, mode, and range.
     * @param plugin
     */
    public SpectreManager(Plugin plugin) {
        switch (plugin.getConfig().getString("defaults.armor").toUpperCase()) {
            case "BOOTS":
                defaultArmorVisibility = ArmorVisibility.BOOTS;
                break;
            case "HIDDEN":
                defaultArmorVisibility = ArmorVisibility.HIDDEN;
                break;
            case "VISIBLE":
                defaultArmorVisibility = ArmorVisibility.VISIBLE;
                break;
            default:
                plugin.getLogger().warning("Invalid default armor visibility: " + plugin.getConfig().getString("defaults.armor"));
                break;
        }
        
        switch (plugin.getConfig().getString("defaults.mode").toUpperCase()) {
            case "GHOST":
                defaultMode = SpectreMode.GHOST;
                break;
            case "VANISH":
                defaultMode = SpectreMode.VANISH;
                break;
            default:
                plugin.getLogger().warning("Invalid default mode: " + plugin.getConfig().getString("defaults.mode"));
                break;
        }

        defaultRadius = plugin.getConfig().getDouble("defaults.vanish-radius") > 0 ? plugin.getConfig().getDouble("defaults.vanish-radius") : 5.0;

    }


    
    /**
     * Enables Spectre mode for a player.
     * 
     * @param player the player to enable Spectre mode for
     */
    public void enableSpectre(Player player) {
        PlayerOptions options = getPlayerOptions(player);
        if(options.isEnabled())
            return;
        options.setEnabled(true);
        playerOptionsMap.put(player.getUniqueId(), options);
        simulatePlayerMovement(player, true, false);
    }

    /**
     * Disables Spectre mode for a player.
     * 
     * @param player the player to disable Spectre mode for
     */
    public void disableSpectre(Player player) {
        PlayerOptions options = getPlayerOptions(player);
        if(!options.isEnabled())
            return;
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
        if(options.getMode() == mode)
            return;
        // Show all players if the player is switching from vanish mode to prevent ghosting
        if(options.getMode() == SpectreMode.VANISH && mode != SpectreMode.VANISH) {
            SpectreApply.showAllPlayers(player);
        }
        options.setMode(mode);
        simulatePlayerMovement(player, true, true);
    }

    /**
     * Sets the armor visibility for a player.
     * 
     * @param player     the player to set the armor visibility for
     * @param visibility the armor visibility to set
     */
    public void setArmorVisibility(Player player, ArmorVisibility visibility) {
        PlayerOptions options = getPlayerOptions(player);
        if(options.getArmorVisibility() == visibility)
            return;
        options.setArmorVisibility(visibility);
        simulatePlayerMovement(player, true, true);
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
        return playerOptionsMap.getOrDefault(player.getUniqueId(),
        new PlayerOptions(false, defaultMode, defaultArmorVisibility, defaultRadius));
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

    public void simulatePlayerMovement(Player player, Boolean updateNearbyPlayers, Boolean showNearbyFirst) {
        if(showNearbyFirst) {
            SpectreApply.showPlayers(player, null);
        }
        if(updateNearbyPlayers) {
            setNearbyMap(player, List.of());
        }
        PlayerMoveEvent e = new PlayerMoveEvent(player, player.getLocation(), player.getLocation());
        Spectre.getInstance().getServer().getPluginManager().callEvent(e);
    }

}
