package com.github.dsadriel.spectre;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.bukkit.Bukkit;


public class SpectreUpdateCheck {

    private static final String VERSION_URL = "https://api.github.com/repos/dsadriel/spectre/releases";
    private static final String currentVersion = Spectre.getInstance().getDescription().getVersion();

    public static void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(Spectre.getInstance(), () -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(VERSION_URL))
                        .timeout(Duration.ofSeconds(10))
                        .build();
                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() != 200) {
                    Spectre.getInstance().getLogger().warning("Failed to check for updates: " + response.statusCode());
                    return;
                }

                String version = response.body().split("\"name\":\"v")[1].split("\"")[0];

                if (!currentVersion.equals(version)) {
                    Spectre.getInstance().getLogger().warning("A new version of Spectre is available! (Current: "
                            + currentVersion + ", Latest: " + version + ")");
                } else {
                    Spectre.getInstance().getLogger().info("Spectre is up to date! (Current: "
                            + currentVersion + ")");
                }
            } catch (Exception e) {
                Spectre.getInstance().getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
        });
    }

}
