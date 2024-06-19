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
            sender.sendMessage(config.getString("messages.player-only"));
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            Spectre.sendMessageKey(player, "usage", true, "<help|enable|disable|mode|armor>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                Spectre.sendMessageKeyList(player, "help", false);
                if (player.hasPermission("spectre.admin")) {
                    Spectre.sendMessageKeyList(player, "help-admin", false);
                }
                return true;

            case "enable":
            case "disable":
                if (!player.hasPermission("spectre.toggle")) {
                    Spectre.sendMessageKey(player, "no-permission");
                    return true;
                }
                handleToggle(player, args);
                return true;

            case "set":
                handleSet(player, args);
                return true;

            case "version":
                if (!player.hasPermission("spectre.admin")) {
                    Spectre.sendMessageKey(player, "no-permission");
                    return true;
                }
                SpectreUpdateCheck.checkForUpdates(player);
                return true;

            case "info":
                Player target = player;
                if (args.length == 2) {
                    target = plugin.getServer().getPlayer(args[1]);
                    if (target == null) {
                        Spectre.sendMessageKey(player, "other.not-found", true, args[1]);
                        return true;
                    }
                }
                Spectre.sendMessage(player, "&7Spectre Info for &c" + target.getName() + "&7:"
                        + spectreManager.getPlayerOptions(target).toString());
                return true;

            default:
                Spectre.sendMessageKey(player, "usage", true, "<help|enable|disable|set>");
                return true;
        }
    }

    /**
     * Handles the toggle command.
     * 
     * @param player
     * @param args
     */
    private void handleToggle(Player player, String[] args) {
        if (!player.hasPermission("spectre.toggle")
                || (args.length == 2 && !player.hasPermission("spectre.admin"))) {
            Spectre.sendMessageKey(player, "no-permission");
            return;
        }

        Player target = args.length == 2 ? plugin.getServer().getPlayer(args[1]) : player;

        if (target == null) {
            Spectre.sendMessageKey(player, "other.not-found", true, args[1]);
            return;
        }

        if (args[0].equalsIgnoreCase("enable")) {
            spectreManager.enableSpectre(target);
            if (target.equals(player)) {
                Spectre.sendMessageKey(player, "self.enabled", true);
            } else {
                Spectre.sendMessageKey(player, "other.enabled", true, target.getName());
            }
        } else {
            spectreManager.disableSpectre(target);
            if (target.equals(player)) {
                Spectre.sendMessageKey(player, "self.disabled", true);
            } else {
                Spectre.sendMessageKey(player, "other.disabled", true, target.getName());
            }
        }
    }

    /**
     * Handles the set command.
     * 
     * @param player
     * @param args
     */
    private void handleSet(Player player, String[] args) {
        if (args.length < 3) {
            if (player.hasPermission("spectre.admin")) {
                Spectre.sendMessageKey(player, "usage", "set <mode|armor|radius> <value> [player]");
            } else {
                Spectre.sendMessageKey(player, "usage", "set <mode|armor|radius> <value>");
            }
            return;
        }
        Player target = args.length == 4 ? plugin.getServer().getPlayer(args[3]) : player;

        if (target == null) {
            Spectre.sendMessageKey(player, "other.not-found", true, args[3]);
            return;
        }
        if (!player.equals(target) && !player.hasPermission("spectre.admin")) {
            Spectre.sendMessageKey(player, "no-permission");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "mode":
                handleSetMode(player, args[2], target);
                break;
            case "armor":
                handleSetArmor(player, args[2], target);
                break;
            case "radius":
                handleSetRadius(player, args[2], target);
                break;
            default:
                Spectre.sendMessageKey(player, "usage", true, "set <mode|armor|radius> <value>");
                break;
        }
    }

    /**
     * Handles the set mode command.
     * 
     * @param player
     * @param mode
     * @param target
     */
    private void handleSetMode(Player player, String mode, Player target) {
        if (!player.hasPermission("spectre.mode")) {
            Spectre.sendMessageKey(player, "no-permission");
            return;
        }
        SpectreMode spectreMode = SpectreMode.fromString(mode);
        if (spectreMode == null) {
            Spectre.sendMessageKey(player, "usage", true, "set mode <vanish|ghost|invisible>");
            return;
        }
        spectreManager.setMode(target, spectreMode);
        if (target.equals(player)) {
            Spectre.sendMessageKey(player, "self.mode", true, mode.toUpperCase());
        } else {
            Spectre.sendMessageKey(player, "other.mode", true, target.getName(), mode.toUpperCase());
        }
    }

    /**
     * Handles the set armor command.
     * 
     * @param player
     * @param visibility
     * @param target
     */
    private void handleSetArmor(Player player, String visibility, Player target) {
        if (!player.hasPermission("spectre.armor")) {
            Spectre.sendMessageKey(player, "no-permission");
            return;
        }
        ArmorVisibility armorVisibility = ArmorVisibility.fromString(visibility);
        if (armorVisibility == null) {
            Spectre.sendMessageKey(player, "usage", true, "set armor <visible|hidden|boots>");
            return;
        }
        spectreManager.setArmorVisibility(target, armorVisibility);
        if (target.equals(player)) {
            Spectre.sendMessageKey(player, "self.armor", true, visibility.toUpperCase());
        } else {
            Spectre.sendMessageKey(player, "other.armor", true, target.getName(), visibility.toUpperCase());
        }
    }

    /**
     * Handles the set radius command.
     * 
     * @param player
     * @param radius
     * @param target
     */
    private void handleSetRadius(Player player, String radius, Player target) {
        if (!player.hasPermission("spectre.radius")) {
            Spectre.sendMessageKey(player, "no-permission");
            return;
        }
        try {
            double _radius = Double.parseDouble(radius);
            if (!spectreManager.getPlayerOptions(target).setRadius(_radius)) {
                Spectre.sendMessageKey(player, "self.radius.invalid", true,
                        String.valueOf(config.getDouble("max-hiding-radius")));
                return;
            }
            if (target.equals(player)) {
                Spectre.sendMessageKey(player, "self.radius.success", true, String.valueOf(radius));
            } else {
                Spectre.sendMessageKey(player, "other.radius.success", true, target.getName(), String.valueOf(radius));
            }
        } catch (NumberFormatException e) {
            Spectre.sendMessageKey(player, "self.radius.invalid", true,
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
            completions = Arrays.asList("help", "enable", "disable", "set");
            if (sender.hasPermission("spectre.admin")) {
                completions = Arrays.asList("help", "enable", "disable", "set", "version", "info");
            }
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable")) {
            if (sender.hasPermission("spectre.admin"))
                completions = null; // Player names
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length == 2) {
                completions = Arrays.asList("mode", "armor", "radius");
            }
            if (args.length == 3) {
                if (args[1].equalsIgnoreCase("mode")) {
                    completions = Arrays.asList("vanish", "ghost", "invisible");
                }
                if (args[1].equalsIgnoreCase("armor")) {
                    completions = Arrays.asList("visible", "hidden", "boots");
                }
            }
            if (args.length == 4) {
                if (sender.hasPermission("spectre.admin"))
                    completions = null; // Player names
            }
        }
        if (completions != null) {
            return completions.stream()
                    .filter(s -> s.startsWith(args[args.length - 1]))
                    .collect(Collectors.toList());
        } else
            return null;
    }
}
