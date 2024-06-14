package com.github.dsadriel.spectre.listners;

import org.bukkit.entity.Player;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;

import com.github.dsadriel.spectre.Spectre;
import com.github.dsadriel.spectre.SpectreManager;
import com.github.dsadriel.spectre.enums.ArmorVisibility;

/**
 * This class implements the PacketListener interface and provides overrides for packet events.
 * It is responsible for handling packet send events and performing specific actions based on the packet type.
 */
public class PacketOverrides implements PacketListener {

    private final SpectreManager spm = Spectre.spectreManager;

    /**
     * This method is called when a packet is being sent to a player.
     * It checks if the player is enabled and if their armor visibility is not set to visible.
     * If the packet type is ENTITY_EQUIPMENT and the player's armor visibility is not visible,
     * it cancels the event if the entity ID is present in the nearby map.
     * If the packet type is ENTITY_METADATA and the entity ID is present in the nearby map,
     * it sets the invisible flag in the metadata packet for the player.
     *
     * @param event The PacketSendEvent containing information about the packet being sent.
     */
    @Override
    public void onPacketSend(PacketSendEvent event) {
        if(event.getPlayer() == null){
            return;
        }
        Player dest = (Player) event.getPlayer();
        if(!spm.getPlayerOptions(dest).isEnabled()){
            return;
        }  
        
        if(event.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT
            && spm.getPlayerOptions(dest).getArmorVisibility() != ArmorVisibility.VISIBLE){
            WrapperPlayServerEntityEquipment wrapped = new WrapperPlayServerEntityEquipment(event);
            if(spm.getNearbyMap(dest).containsKey(wrapped.getEntityId())){
                event.setCancelled(true);
                return;
            }
        }

        // Set the invisible flag in the metadata packet if the player is invisible to the destination player
        if(event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA){
            WrapperPlayServerEntityMetadata wrapped = new WrapperPlayServerEntityMetadata(event);
            if(spm.getNearbyMap(dest).containsKey(wrapped.getEntityId())){
                wrapped.getEntityMetadata().forEach(data -> {
                    if(data.getIndex() == 0 && data.getType() == EntityDataTypes.BYTE){
                        byte b = (byte) data.getValue();
                        b |= (byte) 0x20; // Set the invisible flag
                        data.setValue(b);
                        return;
                    }
                });
            }
        }
    }

}
