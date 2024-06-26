package com.github.dsadriel.spectre.listners;

import com.github.dsadriel.spectre.Spectre;
import com.github.dsadriel.spectre.SpectreApply;
import com.github.dsadriel.spectre.SpectreManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerMovement implements Listener {

    private final SpectreManager spectreManager = Spectre.getInstance().getSpectreManager();

    /**
     * This method is called when a player moves.
     * It checks if the player is enabled and if they are near other players.
     * If the player is enabled and near other players, it shows the players that are no longer nearby
     * and hides the players that are now nearby.
     *
     * @param event The PlayerMoveEvent containing information about the player movement.
     */
    @EventHandler
    public void onPlayerMovement(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!spectreManager.getPlayerOptions(player).isEnabled()) {
            return;
        }

        // Get the nearby players
        double radius = spectreManager.getPlayerOptions(player).getRadius();
        List<Player> nearbyPlayers = player.getNearbyEntities(radius, radius, radius).stream()
                .filter(entity -> entity instanceof Player && !entity.hasPermission("spectre.bypass"))
                .map(entity -> (Player) entity)
                .collect(Collectors.toList());

        // Get the last nearby players
        List<Player> lastNearbyPlayers = spectreManager.getNearbyMap(player).values().stream()
                .map(Bukkit::getPlayer)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        // Update the player map
        spectreManager.setNearbyMap(player, nearbyPlayers);

        // Check if the nearby players have changed
        if (lastNearbyPlayers.containsAll(nearbyPlayers) && nearbyPlayers.containsAll(lastNearbyPlayers)) {
            return;
        }

        // Get the players that are no longer nearby
        List<Player> playersToShow = lastNearbyPlayers.stream()
                .filter(p -> !nearbyPlayers.contains(p))
                .collect(Collectors.toList());

        // Show the players that are no longer nearby
        SpectreApply.showPlayers(player, playersToShow);

        // Hide the players that are now nearby
        SpectreApply.hidePlayers(player, nearbyPlayers);
    }
}
