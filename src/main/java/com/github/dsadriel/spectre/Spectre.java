package com.github.dsadriel.spectre;

import com.github.dsadriel.spectre.listners.*;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;

import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;

public class Spectre extends JavaPlugin {
    public static SpectreManager spectreManager = new SpectreManager();

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

        // Register the command
        getCommand("spectre").setExecutor(new SpectreCommand());


        // Initialize the PacketEvents
        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getEventManager().registerListener(new PacketOverrides(), PacketListenerPriority.HIGH);

        // Register the packet listener
        getServer().getPluginManager().registerEvents(new PlayerMovement(), this);

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

    public static void sendMessage(Player destination, String message, Boolean usePrefix) {
        destination.sendMessage(
                ChatColor.translateAlternateColorCodes('&',
                        (usePrefix ? getInstance().getConfig().getString("messages.prefix") : "")
                                + message));
    }

    public static  void sendMessageKey(Player destination, String key, Boolean usePrefix,  String... args) {
        destination.sendMessage(
                ChatColor.translateAlternateColorCodes('&',
                        (usePrefix ? getInstance().getConfig().getString("messages.prefix") : "")
                                + String.format(getInstance().getConfig().getString("messages." + key), (Object[]) args)));
    }

}
