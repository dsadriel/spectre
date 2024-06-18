package com.github.dsadriel.spectre;

import com.github.dsadriel.spectre.listners.*;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;

import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;

public class Spectre extends JavaPlugin {
    private SpectreManager spectreManager;

    @Override
    public void onLoad() {
        // Load the PacketEvents
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(true)
                .bStats(false);
        PacketEvents.getAPI().load();

    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        // Initialize the SpectreManager
        spectreManager = new SpectreManager(this);
        
        // Set the default boots
        if(!SpectreApply.setBootsMaterial(getConfig().getString("defaults.boots-material"))){
            getLogger().warning("Invalid default boots material: " + getConfig().getString("defaults.boots-material"));
        }


        // Register the command
        getCommand("spectre").setExecutor(new SpectreCommand(this));

        // Register the packet listener
        getServer().getPluginManager().registerEvents(new PlayerMovement(), this);
        
        // Initialize the PacketEvents
        PacketEvents.getAPI().getSettings()
            .bStats(false)
            .checkForUpdates(true);
        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getEventManager().registerListener(new PacketOverrides(), PacketListenerPriority.HIGH);

        // Initialize the metrics for bStats
        if(getConfig().getBoolean("enable-bStats")){
            new Metrics(this, 22290);
        }

        // Check for updates
        if(getConfig().getBoolean("check-for-updates")){
            SpectreUpdateCheck.checkForUpdates();
        }
        
        // Print the plugin enabled message
        getLogger().info("Spectre v." + getDescription().getVersion() + " has been enabled");
    }

    @Override
    public void onDisable() {
        // Remove the packet listener
        getLogger().info("Spectre has been disabled");
        PacketEvents.getAPI().terminate();
    }

    public static Spectre getInstance() {
        return getPlugin(Spectre.class);
    }

    public SpectreManager getSpectreManager() {
        return spectreManager;
    }

    public static void sendMessage(Player destination, String message, Boolean usePrefix) {
        destination.sendMessage(
                ChatColor.translateAlternateColorCodes('&',
                        (usePrefix ? getInstance().getConfig().getString("messages.prefix") : "")
                                + message));
    }

    public static void sendMessage(Player destination, String message) {
        sendMessage(destination, message, true);
    }

    public static  void sendMessageKey(Player destination, String key, Boolean usePrefix,  String... args) {
        destination.sendMessage(
                ChatColor.translateAlternateColorCodes('&',
                        (usePrefix ? getInstance().getConfig().getString("messages.prefix") : "")
                                + String.format(getInstance().getConfig().getString("messages." + key), (Object[]) args)));
    }

    public static  void sendMessageKeyList(Player destination, String key, Boolean usePrefix,  String... args) {
        String message = String.join("\n", getInstance().getConfig().getStringList("messages." + key));
        destination.sendMessage(
                ChatColor.translateAlternateColorCodes('&',
                        (usePrefix ? getInstance().getConfig().getString("messages.prefix") : "")
                                + String.format(message, (Object[]) args)));
    }


    public static void sendMessageKey(Player destination, String key, String... args) {
        sendMessageKey(destination, key, true, args);
    }
    
}
