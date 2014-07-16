package fr.rpg.thepen.listener;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

import fr.rpg.thepen.Items;
import fr.rpg.thepen.Main;

public class NoDurabilityListener implements Listener{

	Main main;
	Items items = main.items;

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDamageEntity(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Player){
			Material type = ((Player) e.getDamager()).getItemInHand().getType();
			Player p = (Player) e.getDamager();
			if(type == Material.DIAMOND_AXE || type == Material.DIAMOND_HOE || type == Material.DIAMOND_SPADE || type == Material.DIAMOND_PICKAXE || type == Material.DIAMOND_SWORD
					|| type == Material.IRON_AXE || type == Material.IRON_HOE || type == Material.IRON_PICKAXE || type == Material.IRON_SPADE || type == Material.IRON_SWORD
					|| type == Material.WOOD_AXE || type == Material.WOOD_HOE || type == Material.WOOD_PICKAXE || type == Material.WOOD_SPADE || type == Material.WOOD_SWORD 
					|| type == Material.STONE_AXE || type == Material.STONE_HOE || type == Material.STONE_PICKAXE || type == Material.STONE_SPADE || type == Material.STONE_SWORD
					|| type == Material.GOLD_AXE || type == Material.GOLD_HOE || type == Material.GOLD_PICKAXE || type == Material.GOLD_SPADE || type == Material.GOLD_SWORD || type == Material.SHEARS){
			p.getItemInHand().setDurability((short) 0);;
			}
			p.updateInventory();
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler 
	public void onPlayerShot(EntityShootBowEvent e){
		if(!(e.getEntity() instanceof Player)){
			return;
		}
		Player p = (Player) e.getEntity();
		p.getItemInHand().setDurability((short) 0);
		p.updateInventory();
	}

	@EventHandler
	public void onBreackBlock(BlockBreakEvent e){
		Material type = e.getPlayer().getItemInHand().getType();
		if(type == Material.DIAMOND_AXE || type == Material.DIAMOND_HOE || type == Material.DIAMOND_SPADE || type == Material.DIAMOND_PICKAXE || type == Material.DIAMOND_SWORD
			|| type == Material.IRON_AXE || type == Material.IRON_HOE || type == Material.IRON_PICKAXE || type == Material.IRON_SPADE || type == Material.IRON_SWORD
			|| type == Material.WOOD_AXE || type == Material.WOOD_HOE || type == Material.WOOD_PICKAXE || type == Material.WOOD_SPADE || type == Material.WOOD_SWORD 
			|| type == Material.STONE_AXE || type == Material.STONE_HOE || type == Material.STONE_PICKAXE || type == Material.STONE_SPADE || type == Material.STONE_SWORD
			|| type == Material.GOLD_AXE || type == Material.GOLD_HOE || type == Material.GOLD_PICKAXE || type == Material.GOLD_SPADE || type == Material.GOLD_SWORD || type == Material.SHEARS){
		e.getPlayer().getItemInHand().setDurability((short) 0);
		e.getPlayer().updateInventory();
		}
	}
}
