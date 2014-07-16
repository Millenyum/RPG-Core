package fr.rpg.thepen.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import fr.rpg.thepen.Items;
import fr.rpg.thepen.Main;

public class InventoryListener implements Listener{

	Main main;
	Items items = main.items;
	
	public FileConfiguration getConfig(){
		return main.getConfig();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	
	public void onInventoryClick(InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		if(e.getInventory().equals(p.getInventory())){
			if(e.getSlot() == 7 || e.getSlot() == 8){
				p.updateInventory();
				e.setCancelled(true);
				p.closeInventory();
				p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.DARK_RED + "Ces items ne peuvent pas être déplacés!");
			}
		}
		
		if(ChatColor.stripColor(e.getInventory().getName()).equalsIgnoreCase("Selectionne ta race")){
			e.setCancelled(true);
		if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR || !e.getCurrentItem().hasItemMeta()){
			p.closeInventory();
			return;
		}
			
		switch(e.getCurrentItem().getType()){
		case BOOK: 
			p.sendMessage(ChatColor.GOLD + "[RPG]" + ChatColor.AQUA + " Tu as choisi la race humain! Bienvenue dans le royaume!");
			p.closeInventory();
			double x = (double) getConfig().get("humains.x");
			double y = (double) getConfig().get("humains.y");
			double z = (double) getConfig().get("humains.z");
			String w = (String) getConfig().get("humains.w");
			float yaw = getConfig().getInt("humains.yaw");
			float pitch = getConfig().getInt("humains.pitch");
			Location location = new Location(Bukkit.getWorld(w), x, y, z, yaw, pitch);
			p.getInventory().setItem(7, items.parchemin_1);
			String path = "users." + p.getName();
			getConfig().set(path + ".race", "Humain");
			getConfig().set(path + ".level", 1);
			getConfig().set(path + ".experience", 0);
			getConfig().set(path + ".argent", 100);
			main.saveConfig();
			p.teleport(location);
			Objective race = main.board.registerNewObjective("Tu es un " + main.getRace(p), "dummy");
			Score score = race.getScore(p);
			score.setScore(0);
			
			
			break;
		case IRON_PICKAXE: 
			p.sendMessage(ChatColor.GOLD + "[RPG]" + ChatColor.AQUA + " Tu as choisi la race nain! Bienvenue à la mine!");
			p.closeInventory();
			double x1 = (double) getConfig().get("nains.x");
			double y1 = (double) getConfig().get("nains.y");
			double z1 = (double) getConfig().get("nains.z");			
			float yaw1 = getConfig().getInt("nains.yaw");
			float pitch1 = getConfig().getInt("nains.pitch");
			p.getInventory().setItem(7, items.parchemin_1);
			String path1 = "users." + p.getName();
			getConfig().set(path1 + ".race", "Nain");
			getConfig().set(path1 + ".level", 1);
			getConfig().set(path1 + ".experience", 0);
			getConfig().set(path1 + ".argent", 100);
			main.saveConfig();
			String w1 = getConfig().getString("nains.w");
			Location location1 = new Location(Bukkit.getWorld(w1), x1, y1, z1, yaw1, pitch1);
			p.teleport(location1);
			break;
		case STONE_SWORD: 
			p.sendMessage(ChatColor.GOLD + "[RPG]" + ChatColor.AQUA + " Tu as choisi la race orc! Bienvenue dans le volcan!");
			p.closeInventory();
			double x2 = (double) getConfig().get("orcs.x");
			double y2 = (double) getConfig().get("orcs.y");
			double z2 = (double) getConfig().get("orcs.z");
			float yaw2 = getConfig().getInt("orcs.yaw");
			float pitch2 = getConfig().getInt("orcs.pitch");
			String w2 = (String) getConfig().get("orcs.w");
			p.getInventory().setItem(7, items.parchemin_1);
			Location location2 = new Location(Bukkit.getWorld(w2), x2, y2, z2, yaw2, pitch2);
			String path2 = "users." + p.getName();
			getConfig().set(path2 + ".race", "Orc");
			getConfig().set(path2 + ".level", 1);
			getConfig().set(path2 + ".experience", 0);
			getConfig().set(path2 + ".argent", 100);
			main.saveConfig();
			p.teleport(location2);
			break;
		case BOW: 
			p.sendMessage(ChatColor.GOLD + "[RPG]" + ChatColor.AQUA + " Tu as choisi la race elfe! Bienvenue dans la forêt!");
			p.closeInventory();
			double x3 = (double) getConfig().get("elfes.x");
			double y3 = (double) getConfig().get("elfes.y");
			double z3 = (double) getConfig().get("elfes.z");
			String w3 = (String) getConfig().get("elfes.w");
			p.getInventory().setItem(7, items.parchemin_1);
			float yaw3 = getConfig().getInt("elfes.yaw");
			float pitch3 = getConfig().getInt("elfes.pitch");
			String path3 = "users." + p.getName();
			getConfig().set(path3 + ".race", "Elfe");
			getConfig().set(path3 + ".level", 1);
			getConfig().set(path3 + ".experience", 0);
			getConfig().set(path3 + ".argent", 100);
			main.saveConfig();
			Location location3 = new Location(Bukkit.getWorld(w3), x3, y3, z3, yaw3, pitch3);
			p.teleport(location3);
			break;
		default:
			break;
		}
	}
}
}
