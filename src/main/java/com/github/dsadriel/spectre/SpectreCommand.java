package com.github.dsadriel.spectre;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.dsadriel.spectre.enums.ArmorVisibility;
import com.github.dsadriel.spectre.enums.SpectreMode;

public class SpectreCommand implements CommandExecutor, TabCompleter {

    private final SpectreManager spectreManager = Spectre.spectreManager;
    private final Plugin plugin = Spectre.getInstance();
    private final FileConfiguration config = plugin.getConfig();

    public SpectreCommand() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, "player_only");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String arg1 = args[0].toLowerCase();

        switch (arg1) {
            case "enable":
                handleEnable(player);
                return true;
            case "disable":
                handleDisable(player);
                return true;
            case "mode":
                if (args.length < 2) {
                    sendMessage(sender, "Usage: /spectre mode <vanish|ghost|invisible>");
                    return true;
                }
                handleMode(player, args[1].toLowerCase());
                return true;
            case "armor":
                if (args.length < 2) {
                    sendMessage(sender, "Usage: /spectre armor <visible|hidden|boots>");
                    return true;
                }
                handleArmor(player, args[1].toLowerCase());
                return true;
            default:
                sendUsage(sender);
                return true;
        }
    }

    private void handleEnable(Player player) {
        spectreManager.enableSpectre(player);
        sendMessage(player, "enabled");
    }

    private void handleDisable(Player player) {
        spectreManager.disableSpectre(player);
        sendMessage(player, "disabled");
    }

    private void handleMode(Player player, String mode) {
        switch (mode) {
            case "vanish":
                spectreManager.setMode(player, SpectreMode.VANISH);
                sendMessage(player, "modes.vanish");
                break;
            case "ghost":
                spectreManager.setMode(player, SpectreMode.GHOST);
                sendMessage(player, "modes.ghost");
                break;
            case "invisible":
                spectreManager.setMode(player, SpectreMode.INVISIBLE);
                sendMessage(player, "modes.invisible");
                break;
            default:
                sendMessage(player, "Usage: /spectre mode <vanish|ghost|invisible>");
                break;
        }
    }

    private void handleArmor(Player player, String armor) {
        ArmorVisibility visibility;
        switch (armor) {
            case "visible":
                visibility = ArmorVisibility.VISIBLE;
                break;
            case "hidden":
                visibility = ArmorVisibility.HIDDEN;
                break;
            case "boots":
                visibility = ArmorVisibility.BOOTS;
                break;
            default:
                sendMessage(player, "Usage: /spectre armor <visible|hidden|boots>");
                return;
        }
        spectreManager.setArmorVisibility(player, visibility);
        sendMessage(player, "armor", armor.toUpperCase());
    }

    private void sendMessage(CommandSender sender, String messageKey, String... args) {
        String message = ChatColor.translateAlternateColorCodes('&',    
                config.getString("messages.prefix") + String.format(config.getString("messages." + messageKey), (Object[]) args));
        sender.sendMessage(message);
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage("Usage: /spectre <enable|disable|mode|armor>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("enable", "disable", "mode", "armor").stream()
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("mode")) {
                return Arrays.asList("vanish", "ghost", "invisible").stream()
                        .filter(mode -> mode.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (args[0].equalsIgnoreCase("armor")) {
                return Arrays.asList("visible", "hidden", "boots").stream()
                        .filter(armor -> armor.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return List.of();
    }
}
