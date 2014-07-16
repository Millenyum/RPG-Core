package fr.rpg.thepen.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import fr.rpg.thepen.Items;
import fr.rpg.thepen.Main;

public class NoExpListener implements Listener{
	private Main main;
	@SuppressWarnings("unused")
    private Items items = main.items;
	public NoExpListener(Main main){
		this.main = main;
	}
	@EventHandler
	public void onBlockExp(BlockExpEvent e)
	  {
	    e.setExpToDrop(0);
	  }
	  
	@EventHandler
	public void onDeath(EntityDeathEvent e)
	  {
	    e.setDroppedExp(0);
	  }
	  
	@EventHandler
	public void onFurnace(FurnaceExtractEvent e)
	  {
	    e.setExpToDrop(0);
	  }
	  
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEntitySpawn(ItemSpawnEvent event)
	  {
	    Entity entity = event.getEntity();
	    if ((entity instanceof ExperienceOrb)) {
	      event.setCancelled(true);
	    }
	  }
	  
	@EventHandler
	public void onPickup(PlayerPickupItemEvent e)
	  {
	    if ((e.getItem() instanceof ExperienceOrb))
	    {
	      e.getItem().remove();
	      e.setCancelled(true);
	    }
	  }
	  
	@EventHandler
	public void onFish(PlayerFishEvent e)
	  {
	    e.setExpToDrop(0);
	  }
	
}
