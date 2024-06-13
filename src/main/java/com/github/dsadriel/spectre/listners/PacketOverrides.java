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

public class PacketOverrides implements PacketListener {

    private final SpectreManager spm = Spectre.spectreManager;

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if(event.getPlayer() == null){
            return;
        }
        Player dest = (Player) event.getPlayer();
        if(!spm.getPlayerOptions(dest).isEnabled()){
            return;
        }  
        
        // Cancel the equipment packet if the player is invisible to the destination player
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
