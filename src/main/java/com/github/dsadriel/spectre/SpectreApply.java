package com.github.dsadriel.spectre;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.ScoreBoardTeamInfo;

import com.github.dsadriel.spectre.enums.ArmorVisibility;
import com.github.dsadriel.spectre.enums.SpectreMode;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import net.kyori.adventure.text.Component;

/**
 * The SpectreApply class provides methods for hiding and revealing players using the Spectre plugin.
 * It contains methods for hiding players, vanishing players, ghosting players, hiding armor, and showing players.
 * The class also includes utility methods for retrieving player metadata and boots.
 */
public class SpectreApply {

    private static final SpectreManager spectreManager = Spectre.spectreManager;
    private static final ItemStack DEFAULT_BOOTS = ItemStack.builder().type(ItemTypes.CHAINMAIL_BOOTS).amount(1).build();
    private static final ScoreBoardTeamInfo TEAM_INFO = new ScoreBoardTeamInfo(
            Component.empty(),
            null,
            null,
            WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
            WrapperPlayServerTeams.CollisionRule.NEVER,
            null,
            WrapperPlayServerTeams.OptionData.FRIENDLY_CAN_SEE_INVISIBLE);

    /**
     * This method hides the specified players from the player.
     * The method checks the player's Spectre mode and armor visibility settings and hides the players accordingly.
     * 
     * @param player the player to hide the players from
     * @param playersToHide the list of players to hide
     */
    public static void hidePlayers(Player player, List<Player> playersToHide) {
        playersToHide.remove(player);
        if (playersToHide.isEmpty()) {
            return;
        }

        SpectreMode mode = spectreManager.getPlayerOptions(player).getMode();
        if (mode == SpectreMode.VANISH) {
            vanishPlayers(player, playersToHide);
        } else if (mode == SpectreMode.GHOST) {
            ghostPlayers(player, playersToHide);
        }

        ArmorVisibility armorVisibility = spectreManager.getPlayerOptions(player).getArmorVisibility();
        hideArmor(player, playersToHide, armorVisibility);
    }

    /**
     * This method vanishes the specified players from the player.
     * The method hides the players from the player using the hidePlayer method.
     * 
     * @param player the player to vanish the players from
     * @param playersToHide the list of players to vanish
     */
    private static void vanishPlayers(Player player, List<Player> playersToHide) {
        for (Player other : playersToHide) {
            player.hidePlayer(Spectre.getInstance(), other);
        }
    }

    /**
     * This method ghosts the specified players from the player.
     * The method creates a ghost team for the player and the players to hide.
     * 
     * @param player the player to ghost the players from
     * @param playersToHide the list of players to ghost
     */
    private static void ghostPlayers(Player player, List<Player> playersToHide) {
        Collection<String> playersNames = playersToHide.stream().map(Player::getName).collect(Collectors.toList());
        playersNames.add(player.getName());

        WrapperPlayServerTeams team = new WrapperPlayServerTeams(
                "Spectre." + player.getName(),
                WrapperPlayServerTeams.TeamMode.CREATE,
                TEAM_INFO,
                playersNames);

        PacketEvents.getAPI().getPlayerManager().sendPacketSilently(player, team);
    }


    /**
     * This method hides the armor of the specified players from the player.
     * The method checks the armor visibility setting and hides the armor of the players accordingly.
     * 
     * @param player the player to hide the armor from
     * @param playersToHide the list of players to hide the armor from
     * @param armorVisibility the armor visibility setting
     */
    private static void hideArmor(Player player, List<Player> playersToHide, ArmorVisibility armorVisibility) {
        for (Player other : playersToHide) {
            List<EntityData> entityData = List.of(
                    new EntityData(0, EntityDataTypes.BYTE, (byte) 0x20));
            WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(other.getEntityId(),
                    entityData);
            PacketEvents.getAPI().getPlayerManager().sendPacketSilently(player, metadata);

            if (armorVisibility != ArmorVisibility.VISIBLE) {
                ItemStack boots = getBoots(other, armorVisibility);
                List<Equipment> equipmentList = List.of(
                        new Equipment(EquipmentSlot.MAIN_HAND, null),
                        new Equipment(EquipmentSlot.OFF_HAND, null),
                        new Equipment(EquipmentSlot.HELMET, null),
                        new Equipment(EquipmentSlot.CHEST_PLATE, null),
                        new Equipment(EquipmentSlot.LEGGINGS, null),
                        new Equipment(EquipmentSlot.BOOTS, boots));

                WrapperPlayServerEntityEquipment equipment = new WrapperPlayServerEntityEquipment(other.getEntityId(),
                        equipmentList);
                PacketEvents.getAPI().getPlayerManager().sendPacketSilently(player, equipment);
            }
        }
    }

    /**
     * This method reveals the specified players to the player.
     * The method checks the player's Spectre mode and armor visibility settings and reveals the players accordingly.
     * 
     * @param player the player to reveal the players to
     * @param playersToShow the list of players to reveal
     */
    private static ItemStack getBoots(Player player, ArmorVisibility armorVisibility) {
        if (armorVisibility == ArmorVisibility.HIDDEN) {
            return null;
        } else {
            ItemStack boots = SpigotReflectionUtil.decodeBukkitItemStack(player.getEquipment().getBoots());
            if (boots == null || boots.getType() == ItemTypes.AIR) {
                return DEFAULT_BOOTS;
            }
            return boots;
        }
    }

    /**
     * This method reveals the specified players to the player.
     * The method checks the player's Spectre mode and armor visibility settings and reveals the players accordingly.
     * 
     * @param player the player to reveal the players to
     * @param playersToShow the list of players to reveal
     */
    public static void showAllPlayers(Player player) {
        List<Player> nearbyPlayers = Bukkit.getOnlinePlayers().stream()
                .filter(p -> !p.equals(player))
                .collect(Collectors.toList());

        showPlayers(player, nearbyPlayers);
    }

    /**
     * This method reveals the specified players to the player.
     * The method checks the player's Spectre mode and armor visibility settings and reveals the players accordingly.
     * 
     * @param player the player to reveal the players to
     * @param playersToShow the list of players to reveal
     */
    public static void showPlayers(Player player, @Nullable List<Player> playersToShow) {
        if(playersToShow == null){
            playersToShow = Spectre.spectreManager.getNearbyMap(player).values().stream()
                    .map(Bukkit::getPlayer)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
        }
        playersToShow.remove(player);
        
        
        SpectreMode mode = spectreManager.getPlayerOptions(player).getMode();
        // If mode is VANISH, unvanish the players
        if (mode == SpectreMode.VANISH) {
            unvanishPlayers(player, playersToShow);
            return;
        }

        // Remove the ghost team
        Collection<String> playersNames = playersToShow.stream().map(Player::getName).collect(Collectors.toList());
        WrapperPlayServerTeams team = new WrapperPlayServerTeams(
                "Spectre." + player.getName(),
                WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
                TEAM_INFO,
                playersNames);

        PacketEvents.getAPI().getPlayerManager().sendPacketSilently(player, team);
        team.setTeamMode(WrapperPlayServerTeams.TeamMode.REMOVE_ENTITIES);
        PacketEvents.getAPI().getPlayerManager().sendPacketSilently(player, team);

        revealPlayers(player, playersToShow);
        
    }

    /**
     * This method reveals the specified players to the player.
     * The method checks the player's Spectre mode and armor visibility settings and reveals the players accordingly.
     * 
     * @param player the player to reveal the players to
     * @param playersToShow the list of players to reveal
     */
    private static void unvanishPlayers(Player player, List<Player> playersToShow) {
        for (Player other : playersToShow) {
            player.showPlayer(Spectre.getInstance(), other);
        }
    }

    /**
     * This method reveals the specified players to the player.
     * The method checks the player's Spectre mode and armor visibility settings and reveals the players accordingly.
     * 
     * @param player the player to reveal the players to
     * @param playersToShow the list of players to reveal
     */
    private static void revealPlayers(Player player, List<Player> playersToShow) {
        for (Player other : playersToShow) {
            // Send metadata to show player to remove the invisible flag
            List<EntityData> entityData = List.of(
                    new EntityData(0, EntityDataTypes.BYTE, getPlayerMetaData(player)));
            WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(other.getEntityId(),
                    entityData);
            PacketEvents.getAPI().getPlayerManager().sendPacketSilently(player, metadata);

            // Send equipment to show player
            EntityEquipment otherEquip = other.getEquipment();
            List<Equipment> equipmentList = List.of(
                    new Equipment(EquipmentSlot.MAIN_HAND,
                            SpigotReflectionUtil.decodeBukkitItemStack(otherEquip.getItemInMainHand())),
                    new Equipment(EquipmentSlot.OFF_HAND,
                            SpigotReflectionUtil.decodeBukkitItemStack(otherEquip.getItemInOffHand())),
                    new Equipment(EquipmentSlot.HELMET,
                            SpigotReflectionUtil.decodeBukkitItemStack(otherEquip.getHelmet())),
                    new Equipment(EquipmentSlot.CHEST_PLATE,
                            SpigotReflectionUtil.decodeBukkitItemStack(otherEquip.getChestplate())),
                    new Equipment(EquipmentSlot.LEGGINGS,
                            SpigotReflectionUtil.decodeBukkitItemStack(otherEquip.getLeggings())),
                    new Equipment(EquipmentSlot.BOOTS,
                            SpigotReflectionUtil.decodeBukkitItemStack(otherEquip.getBoots())));

            WrapperPlayServerEntityEquipment equipment = new WrapperPlayServerEntityEquipment(other.getEntityId(),
                    equipmentList);
            PacketEvents.getAPI().getPlayerManager().sendPacketSilently(player, equipment);
        }
    }

    /**
     * This method retrieves the metadata for a player.
     * The metadata includes information about the player's status, such as fire ticks, sneaking, sprinting, swimming, glowing, invisibility, and gliding.
     * 
     * @param player the player to retrieve the metadata for
     * @return the metadata byte for the player
     */
    private static byte getPlayerMetaData(Player player) {
        byte metadata = 0x00;
        if (player.getFireTicks() > 0) {
            metadata |= 0x01;
        }
        if (player.isSneaking()) {
            metadata |= 0x02;
        }
        if (player.isSprinting()) {
            metadata |= 0x08;
        }
        if (player.isSwimming()) {
            metadata |= 0x10;
        }
        if (player.isGlowing()) {
            metadata |= 0x40;
        }
        if (player.getActivePotionEffects().stream().anyMatch(e -> e.getType() == PotionEffectType.INVISIBILITY)) {
            metadata |= 0x20;
        }
        if (player.getActivePotionEffects().stream().anyMatch(e -> e.getType() == PotionEffectType.GLOWING)) {
            metadata |= 0x40;
        }
        if (player.isGliding()) {
            metadata |= 0x80;
        }
        return metadata;
    }
}
