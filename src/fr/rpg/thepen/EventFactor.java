package fr.rpg.thepen;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class EventFactor implements Listener {
	@EventHandler
	public void PlayerInteractEntity(PlayerInteractEntityEvent e){
		if(e.getRightClicked().getType() == EntityType.ZOMBIE){
			
		}
	}
}
