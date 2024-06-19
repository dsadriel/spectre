package com.github.dsadriel.spectre;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpectreUpdateCheck {

    private static final String VERSION_URL = "https://api.github.com/repos/dsadriel/spectre/releases/latest";
    private static final String currentVersion = Spectre.getInstance().getDescription().getVersion();

    /**
     * Checks for updates and logs a warning if a new version is available.
     * This method is called asynchronously to avoid blocking the main thread.
     */
    public static void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(Spectre.getInstance(), () -> {
            String latestVersion = getLatestVersion();
            if (latestVersion.isEmpty()) {
                Spectre.getInstance().getLogger().warning("Failed to check for updates");
                return;
            }

            if (!currentVersion.equals(latestVersion)) {
                Spectre.getInstance().getLogger().warning("A new version of Spectre is available: " + latestVersion);
            }
        });
    }

    /**
     * Checks for updates and sends a message to the player if a new version is available.
     * This method is called asynchronously to avoid blocking the main thread.
     * 
     * @param sendTo
     */
    public static void checkForUpdates(Player sendTo){
        Bukkit.getScheduler().runTaskAsynchronously(Spectre.getInstance(), () -> {
            String latestVersion = getLatestVersion();
            if (latestVersion.isEmpty()) {
                Spectre.sendMessageKey(sendTo, "version-check.error");
                sendTo.playSound(sendTo.getLocation(), "block.anvil.land", 1, 1);
                return;
            }

            if (!currentVersion.equals(latestVersion)) {
                Spectre.sendMessageKey(sendTo, "version-check.outdated", latestVersion);
                sendTo.playSound(sendTo.getLocation(), "block.anvil.land", 1, 1);
            } else {
                Spectre.sendMessageKey(sendTo, "version-check.up-to-date", latestVersion);
                sendTo.playSound(sendTo.getLocation(), "block.conduit.activate", 1, 1);
            }
        });
    }

    /**
     * Gets the latest version of Spectre from the GitHub releases API.
     * 
     * @return the latest version of Spectre
     */
    public static String getLatestVersion() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VERSION_URL))
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                Spectre.getInstance().getLogger().warning("Failed to check for updates: " + response.statusCode());
                return "";
            }

            return response.body().split("\"name\":\"v")[1].split("\"")[0];

        } catch (Exception e) {
            Spectre.getInstance().getLogger().warning("Failed to check for updates: " + e.getMessage());
            return "";
        }
    }

}
