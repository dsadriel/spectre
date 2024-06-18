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

import com.github.dsadriel.spectre.enums.ArmorVisibility;
import com.github.dsadriel.spectre.enums.SpectreMode;

public class SpectreCommand implements CommandExecutor, TabCompleter {

    private Spectre plugin = Spectre.getInstance();
    private SpectreManager spectreManager;
    private FileConfiguration config;

    public SpectreCommand(Spectre plugin) {
        this.plugin = plugin;
        this.spectreManager = plugin.getSpectreManager();
        this.config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(config.getString("messages.player_only"));
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            Spectre.sendMessageKey(player, "usage", true, "<help|enable|disable|mode|armor>");
            return true;
        }

        String arg1 = args[0].toLowerCase();

        switch (arg1) {
            case "enable":
                if (!player.hasPermission("spectre.toggle")) {
                    Spectre.sendMessageKey(player, "no_permission");
                    return true;
                }
                spectreManager.enableSpectre(player);
                Spectre.sendMessageKey(player, "enabled");
                return true;
            case "disable":
                if (!player.hasPermission("spectre.toggle")) {
                    Spectre.sendMessageKey(player, "no_permission");
                    return true;
                }
                spectreManager.disableSpectre(player);
                Spectre.sendMessageKey(player, "disabled");
                return true;
            case "mode":
                if (args.length < 2) {
                    Spectre.sendMessageKey(player, "usage", "mode <vanish|ghost|invisible>");
                    return true;
                }
                handleMode(player, args[1].toLowerCase());
                return true;
            case "armor":
                if (args.length < 2) {
                    Spectre.sendMessageKey(player, "usage", "armor <visible|hidden|boots>");
                    return true;
                }
                handleArmor(player, args[1].toLowerCase());
                return true;
            case "version":
                if (!player.hasPermission("spectre.version")) {
                    Spectre.sendMessageKey(player, "no_permission");
                    return true;
                }
                Spectre.sendMessage(player, "Spectre v." + plugin.getDescription().getVersion() + " by "
                        + plugin.getDescription().getAuthors().get(0));
                return true;
            case "check-updates":
                if (!player.hasPermission("spectre.check-updates")) {
                    Spectre.sendMessageKey(player, "no_permission");
                    return true;
                }
                SpectreUpdateCheck.checkForUpdates(player);
                return true;
            case "help":
                Spectre.sendMessageKeyList(player, "help", false);
                if (player.hasPermission("spectre.admin")) {
                    Spectre.sendMessageKeyList(player, "help-admin", false);
                }
                return true;
            case "set-radius":
                handleRadius(player, args);
                return true;
            case "info":
                Spectre.sendMessage(player, spectreManager.getPlayerOptions(player).toString());
                return true;
            default:
                Spectre.sendMessageKey(player, "usage", true, "<help|enable|disable|mode|armor>");
                return true;
        }
    }

    /**
     * Handles the mode command.
     * 
     * @param player
     * @param mode
     */
    private void handleMode(Player player, String mode) {
        switch (mode) {
            case "vanish":
                if (!player.hasPermission("spectre.mode.vanish")) {
                    Spectre.sendMessageKey(player, "no_permission");
                    return;
                }
                spectreManager.setMode(player, SpectreMode.VANISH);
                Spectre.sendMessageKey(player, "modes.vanish");
                break;
            case "ghost":
                if (!player.hasPermission("spectre.mode.ghost")) {
                    Spectre.sendMessageKey(player, "no_permission");
                    return;
                }
                spectreManager.setMode(player, SpectreMode.GHOST);
                Spectre.sendMessageKey(player, "modes.ghost");
                break;
            case "invisible":
                if (!player.hasPermission("spectre.mode.invisible")) {
                    Spectre.sendMessageKey(player, "no_permission");
                    return;
                }
                spectreManager.setMode(player, SpectreMode.INVISIBLE);
                Spectre.sendMessageKey(player, "modes.invisible");
                break;
            default:
                Spectre.sendMessageKey(player, "usage", "mode <vanish|ghost|invisible>");
                break;
        }
    }

    /**
     * Handles the armor command.
     * 
     * @param player
     * @param armor
     */
    private void handleArmor(Player player, String armor) {
        ArmorVisibility visibility;
        switch (armor) {
            case "visible":
                if (!player.hasPermission("spectre.armor.visible")) {
                    Spectre.sendMessageKey(player, "no_permission");
                    return;
                }
                visibility = ArmorVisibility.VISIBLE;
                break;
            case "hidden":
                if (!player.hasPermission("spectre.armor.hidden")) {
                    Spectre.sendMessageKey(player, "no_permission");
                    return;
                }
                visibility = ArmorVisibility.HIDDEN;
                break;
            case "boots":
                if (!player.hasPermission("spectre.armor.boots")) {
                    Spectre.sendMessageKey(player, "no_permission");
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

    /**
     * Handles the set-radius command.
     * 
     * @param player
     * @param args
     */
    private void handleRadius(Player player, String[] args) {
        if (!player.hasPermission("spectre.set-radius")) {
            Spectre.sendMessageKey(player, "no_permission");
            return;
        }
        if (args.length < 2) {
            Spectre.sendMessageKey(player, "usage", "set-radius <radius>");
            return;
        }
        try {
            double radius = Double.parseDouble(args[1]);
            if (!spectreManager.getPlayerOptions(player).setRadius(radius)) {
                Spectre.sendMessageKey(player, "set-radius.invalid", true,
                        String.valueOf(config.getDouble("max-hiding-radius")));
                return;
            }
            Spectre.sendMessageKey(player, "set-radius.success", true, String.valueOf(radius));
        } catch (NumberFormatException e) {
            Spectre.sendMessageKey(player, "set-radius.invalid", true,
                    String.valueOf(config.getDouble("max-hiding-radius")));
        }
    }

    /**
     * Tab completion for the Spectre command.
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = Arrays.asList();
        if (args.length == 1) {
            completions = Arrays.asList("help", "enable", "disable", "mode", "armor", "version", "set-radius");
            if (sender.hasPermission("spectre.admin")) {
                completions = Arrays.asList("help", "enable", "disable", "mode", "armor", "version", "set-radius", "check-updates");
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("mode")) {
                completions = Arrays.asList("vanish", "ghost", "invisible");
            }
            if (args[0].equalsIgnoreCase("armor")) {
                completions = Arrays.asList("visible", "hidden", "boots");
            }
        }
        return completions.stream()
                .filter(s -> s.startsWith(args[args.length - 1]))
                .collect(Collectors.toList());
    }
}
