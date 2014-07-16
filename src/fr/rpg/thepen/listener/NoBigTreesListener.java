package fr.rpg.thepen.listener;

import org.bukkit.TreeType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import fr.rpg.thepen.Items;
import fr.rpg.thepen.Main;

public class NoBigTreesListener implements Listener{
	private Main main;
	@SuppressWarnings("unused")
    private Items items = main.items;
	public NoBigTreesListener(Main main){
		this.main = main;
	}
	@EventHandler
	public void onTreeGrow(StructureGrowEvent e){
		if (e.getSpecies() == TreeType.BIG_TREE){
			e.setCancelled(true);
		}
	}
}
