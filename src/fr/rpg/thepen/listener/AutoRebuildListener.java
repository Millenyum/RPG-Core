package fr.rpg.thepen.listener;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import fr.rpg.thepen.BlockInfo;
import fr.rpg.thepen.Main;

public class AutoRebuildListener implements Listener{
	public Main main;
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBreakBlock(BlockBreakEvent e){
		Player p = e.getPlayer();
		
		if(e.getBlock().getType() == Material.SAPLING){
			if(!p.isOp()){
				e.setCancelled(true);
			}
		}
		
		else{
			if(p.isOp()){
				BlockInfo blockinfo = new BlockInfo(e.getBlock().getLocation(), e.getBlock().getType(), e.getBlock().getData());
				final HashMap<Location, BlockInfo> blocks = new HashMap<Location, BlockInfo>();
				final ArrayList<Location> blockslist = new ArrayList<Location>();
				blocks.put(e.getBlock().getLocation(), blockinfo);
				blockslist.add(e.getBlock().getLocation());
				Bukkit.getScheduler().runTaskLater(main, new Runnable() {
				
					@Override
					public void run() {
						for(int i = 0; i < blockslist.size(); i++){
							Location loc = blockslist.get(i);
							BlockInfo bi = blocks.get(loc);
							loc.getWorld().getBlockAt(loc).setType(bi.getType());
							loc.getWorld().getBlockAt(loc).setData(bi.getDataValue());
						}
					
					}
				}, 600L);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		final ArrayList<Location> blockslist = new ArrayList<Location>();
		final HashMap<Location, BlockInfo> blocks = new HashMap<Location, BlockInfo>();
		for(int i = 0; i < e.blockList().size(); i++){
			Block bl = e.blockList().get(i);
			BlockInfo bi = new BlockInfo(bl.getLocation(), bl.getType(), bl.getData());
			blocks.put(bl.getLocation(), bi);
			blockslist.add(bl.getLocation());
		}
		Bukkit.getScheduler().runTaskLater(main, new Runnable() {
			
			@Override
			public void run() {
				for(int i = 0; i < blockslist.size(); i++){
					Location loc = blockslist.get(i);
					BlockInfo bi = blocks.get(loc);
					loc.getWorld().getBlockAt(loc).setType(bi.getType());
					loc.getWorld().getBlockAt(loc).setData(bi.getDataValue());
				}
				
			}
		}, 600L);
		  
		}

}
