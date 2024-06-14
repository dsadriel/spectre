package com.github.dsadriel.spectre;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
            sender.sendMessage(config.getString("messages.player_only"));
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            Spectre.sendMessageKey(player, "usage", true, "<enable|disable|mode|armor>");
            return true;
        }

        String arg1 = args[0].toLowerCase();

        switch (arg1) {
            case "enable":
                if (!player.hasPermission("spectre.toggle")) {
                    Spectre.sendMessageKey(player, "no_permission", true);
                    return true;
                }
                spectreManager.enableSpectre(player);
                Spectre.sendMessageKey(player, "enabled", true);
                return true;
            case "disable":
                if (!player.hasPermission("spectre.toggle")) {
                    Spectre.sendMessageKey(player, "no_permission", true);
                    return true;
                }
                spectreManager.disableSpectre(player);
                Spectre.sendMessageKey(player, "disabled", true);
                return true;
            case "mode":
                if (args.length < 2) {
                    Spectre.sendMessageKey(player, "usage", true, "mode <vanish|ghost|invisible>");
                    return true;
                }
                handleMode(player, args[1].toLowerCase());
                return true;
            case "armor":
                if (args.length < 2) {
                    Spectre.sendMessageKey(player, "usage", true, "armor <visible|hidden|boots>");
                    return true;
                }
                handleArmor(player, args[1].toLowerCase());
                return true;
            default:
                Spectre.sendMessageKey(player, "usage", true, "<enable|disable|mode|armor>");
                return true;
        }
    }

    private void handleMode(Player player, String mode) {
        switch (mode) {
            case "vanish":
                if(!player.hasPermission("spectre.mode.vanish")) {
                    Spectre.sendMessageKey(player, "no_permission", true);
                    return;
                }
                spectreManager.setMode(player, SpectreMode.VANISH);
                Spectre.sendMessageKey(player, "modes.vanish", true);
                break;
            case "ghost":
                if(!player.hasPermission("spectre.mode.ghost")) {
                    Spectre.sendMessageKey(player, "no_permission", true);
                    return;
                }
                spectreManager.setMode(player, SpectreMode.GHOST);
                Spectre.sendMessageKey(player, "modes.ghost", true);
                break;
            case "invisible":
                if(!player.hasPermission("spectre.mode.invisible")) {
                    Spectre.sendMessageKey(player, "no_permission", true);
                    return;
                }
                spectreManager.setMode(player, SpectreMode.INVISIBLE);
                Spectre.sendMessageKey(player, "modes.invisible", true);
                break;
            default:
                Spectre.sendMessageKey(player, "usage", true, "mode <vanish|ghost|invisible>");
                break;
        }
    }

    private void handleArmor(Player player, String armor) {
        ArmorVisibility visibility;
        switch (armor) {
            case "visible":
                if (!player.hasPermission("spectre.armor.visible")) {
                    Spectre.sendMessageKey(player, "no_permission", true);
                    return;
                }
                visibility = ArmorVisibility.VISIBLE;
                break;
            case "hidden":
                if (!player.hasPermission("spectre.armor.hidden")) {
                    Spectre.sendMessageKey(player, "no_permission", true);
                    return;
                }
                visibility = ArmorVisibility.HIDDEN;
                break;
            case "boots":
                if (!player.hasPermission("spectre.armor.boots")) {
                    Spectre.sendMessageKey(player, "no_permission", true);
                    return;
                }
                visibility = ArmorVisibility.BOOTS;
                break;
            default:
                Spectre.sendMessageKey(player, "usage", true, "armor <visible|hidden|boots>");
                return;
        }
        spectreManager.setArmorVisibility(player, visibility);
        Spectre.sendMessageKey(player, "armor", true, armor.toUpperCase());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("enable", "disable", "mode", "armor").stream()
                    .filter(arg -> arg.startsWith(args[0].toLowerCase()))
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
