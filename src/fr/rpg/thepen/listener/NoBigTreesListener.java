package fr.rpg.thepen.listener;

import org.bukkit.TreeType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

public class NoBigTreesListener implements Listener{

	@EventHandler
	public void onTreeGrow(StructureGrowEvent e){
		if (e.getSpecies() == TreeType.BIG_TREE){
			e.setCancelled(true);
		}
	}
}
