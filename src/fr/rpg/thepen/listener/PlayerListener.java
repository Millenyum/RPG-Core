package fr.rpg.thepen.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import fr.rpg.thepen.Donjon;
import fr.rpg.thepen.Door;
import fr.rpg.thepen.DoorType;
import fr.rpg.thepen.Items;
import fr.rpg.thepen.Main;
import fr.rpg.thepen.Room;
import fr.rpg.thepen.RoomType;

public class PlayerListener implements Listener{
	
	private Main main;
	
	@SuppressWarnings("unused")
    private Items items = main.items;
	
	public PlayerListener(Main main){
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location from = e.getFrom();
		Location to = e.getTo();
		if(Main.isInDonjon(p) == false){
			for(int i = 0; i < main.donjons.size(); i++){
				int radius = 2;
				if ((from.getBlockX() != to.getBlockX()) || (from.getBlockZ() != to.getBlockZ())) {
					if(Main.getNearbyPlayers(main.donjons.get(i).getLocation(), radius).length >= 1) {
						if(main.donjons.get(i).isActive()){
							if(main.donjons.get(i).isOpen()){
								main.donjons.get(i).setClose();
								p.sendMessage("Ce tombeau sera votre tombeau!");
								p.getWorld().playSound(main.donjons.get(i).getLocation(), Sound.WITHER_SPAWN, 1, 1);
								Main.indonjon.put(p, main.donjons.get(i));
							}
						}
					}
				}
			}
		}
		if(Main.isInDonjon(p)){
			Donjon donj = Main.indonjon.get(p);
			for(int i = 0; i < donj.getRooms().size(); i++){
				int radius = 2;
				Room room = donj.getRooms().get(i);
				if ((from.getBlockX() != to.getBlockX()) || (from.getBlockZ() != to.getBlockZ())) {
					if(Main.getNearbyPlayers(room.getLocation(), radius).length >= 1) {
						if(room.getType() == RoomType.FIGHTROOM){
							room.setClosed();
						}
					}
				}
			}
		}
	}

	@EventHandler 
	public void onPlayerInteract(PlayerInteractEvent e){
		Player p = e.getPlayer();
		
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(Main.isInDonjon(p)){
				if(e.getPlayer().getItemInHand().getType().equals(Material.NAME_TAG) || e.getPlayer().getItemInHand().getType().equals(Material.TRIPWIRE_HOOK)){
					Donjon donjon = Main.indonjon.get(p);
					if(e.getClickedBlock().getType().equals(Material.GOLD_BLOCK) || e.getClickedBlock().getType().equals(Material.IRON_BLOCK)){
						for(int i = 0; i < donjon.getDoors().size(); i++){
							if(donjon.getDoors().get(i).getType().equals(DoorType.KEY)  || donjon.getDoors().get(i).getType().equals(DoorType.BOSS) || donjon.getDoors().get(i).getType().equals(DoorType.FIGHTROOMKEY)){
								Door door = donjon.getDoors().get(i);
								if(door.getLock().equals(e.getClickedBlock().getLocation())){
									List<String> lore = e.getItem().getItemMeta().getLore();
									 if(lore.get(0).equals(donjon.getName())){
										 if(lore.get(1).equals(donjon.getRoomByDoor(door).getName())){
											 donjon.getDoors().get(i).setClose();
											 donjon.getDoors().get(i).setOpen();
										 }
									 }
								}
							}
						}
					}
				}
			}
			else if(main.setdonjon_name.containsKey(p)){
				if(!main.setdoor.containsKey(p.getName() + 1)){
					main.setdoor.put(p.getName() + 1, e.getClickedBlock().getLocation());
					p.sendMessage("Le point 1 à été défini. Selectionne le point 2 à présent.");
				}
				else{
					main.setdoor.put(p.getName() + 2, e.getClickedBlock().getLocation());
					Door door = new Door(DoorType.DONJON, main.setdoor.get(p.getName() + 1), main.setdoor.get(p.getName() + 2), true);
					main.setdoor.remove(p.getName() + 1);
					main.setdoor.remove(p.getName() + 2);
					Location loc = main.setdonjon_location.get(p);
					String nom = main.setdonjon_name.get(p);
					Donjon donj = new Donjon(loc, nom, door, false);
					main.donjons.add(donj);
					main.setdonjon_location.remove(p);
					main.setdonjon_name.remove(p);
					p.sendMessage("Donjon Created");
					donj.setClose();
				}
			}
			if(main.setroom_donjon.containsKey(p)){
				Donjon donj = main.setroom_donjon.get(p);
				String name = main.setroom_name.get(p);
				RoomType type = main.setroom_type.get(p);
				Location loc = main.setroom_location.get(p);
				if(!main.setdoor.containsKey(p.getName() + 1)){
					main.setdoor.put(p.getName() + 1, e.getClickedBlock().getLocation());
					p.sendMessage("Le point 1 à été défini. Selectionne le point 2 à présent.");
				}
				else if (main.setdoor.containsKey(p.getName() + 1) && !main.setdoor.containsKey(p.getName() + 2)){
					if(type == RoomType.FIGHTROOM || type == RoomType.ROOM || type == RoomType.REWARDROOM){
						main.setdoor.put(p.getName() + 2, e.getClickedBlock().getLocation());
						if(type == RoomType.FIGHTROOM){
							Door door = new Door(DoorType.FIGHTROOM, main.setdoor.get(p.getName() + 1), main.setdoor.get(p.getName() + 2), true);
							ArrayList<Door> doors = new ArrayList<Door>();
							doors.add(door);
							Room room = new Room(name, doors, loc, type);
							donj.addRoom(room);
							main.setdoor.remove(p.getName() + 1);
							main.setdoor.remove(p.getName() + 2);
							main.setroom_donjon.remove(p);
							main.setroom_location.remove(p);
							main.setroom_name.remove(p);
							main.setroom_type.remove(p);
							p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "La salle a été créée et a été ajoutée au donjon.");
						}
						else if(type == RoomType.ROOM){
							Door door = new Door(DoorType.NORMAL, main.setdoor.get(p.getName() + 1), main.setdoor.get(p.getName() + 2), true);
							ArrayList<Door> doors = new ArrayList<Door>();
							doors.add(door);
							Room room = new Room(name, doors, loc, type);
							donj.addRoom(room);
							main.setdoor.remove(p.getName() + 1);
							main.setdoor.remove(p.getName() + 2);
							main.setroom_donjon.remove(p);
							main.setroom_location.remove(p);
							main.setroom_name.remove(p);
							main.setroom_type.remove(p);
							p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "La salle a été créée et a été ajoutée au donjon.");
						}
						else if(type == RoomType.REWARDROOM){
							Door door = new Door(DoorType.NORMAL, main.setdoor.get(p.getName() + 1), main.setdoor.get(p.getName() + 2), false);
							ArrayList<Door> doors = new ArrayList<Door>();
							doors.add(door);
							Room room = new Room(name, doors, loc, type);
							donj.addRoom(room);
							main.setdoor.remove(p.getName() + 1);
							main.setdoor.remove(p.getName() + 2);
							main.setroom_donjon.remove(p);
							main.setroom_location.remove(p);
							main.setroom_name.remove(p);
							main.setroom_type.remove(p);
							p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "La salle a été créée et a été ajoutée au donjon.");
						}
					}
					else{
						main.setdoor.put(p.getName() + 2, e.getClickedBlock().getLocation());
						p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "Maintenant, selectionne d'un clic droit le bloc qui fera office de serrure.");
						
					}
				}
				else if(main.setdoor.containsKey(p.getName() + 2)){
					Location location = e.getClickedBlock().getLocation();
					if(type == RoomType.REWARDROOMKEY){
						Door door = new Door(DoorType.KEY, main.setdoor.get(p.getName() + 1), main.setdoor.get(p.getName() + 2), location, false);
						ArrayList<Door> doors = new ArrayList<Door>();
						doors.add(door);
						Room room = new Room(name, doors, loc, type);
						donj.addRoom(room);
						main.setdoor.remove(p.getName() + 1);
						main.setdoor.remove(p.getName() + 2);
						main.setroom_donjon.remove(p);
						main.setroom_location.remove(p);
						main.setroom_name.remove(p);
						main.setroom_type.remove(p);
						p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "La salle a été créée et a été ajoutée au donjon.");
				
					}
					if(type == RoomType.ROOMKEY){
						Door door = new Door(DoorType.KEY, main.setdoor.get(p.getName() + 1), main.setdoor.get(p.getName() + 2), location, false);
						ArrayList<Door> doors = new ArrayList<Door>();
						doors.add(door);
						Room room = new Room(name, doors, loc, type);
						donj.addRoom(room);
						main.setdoor.remove(p.getName() + 1);
						main.setdoor.remove(p.getName() + 2);
						main.setroom_donjon.remove(p);
						main.setroom_location.remove(p);
						main.setroom_name.remove(p);
						main.setroom_type.remove(p);
						p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "La salle a été créée et a été ajoutée au donjon.");
					}
					if(type == RoomType.FIGHTROOMKEY){
						Door door = new Door(DoorType.FIGHTROOMKEY, main.setdoor.get(p.getName() + 1), main.setdoor.get(p.getName() + 2), location, false);
						ArrayList<Door> doors = new ArrayList<Door>();
						doors.add(door);
						Room room = new Room(name, doors, loc, type);
						donj.addRoom(room);
						main.setdoor.remove(p.getName() + 1);
						main.setdoor.remove(p.getName() + 2);
						main.setroom_donjon.remove(p);
						main.setroom_location.remove(p);
						main.setroom_name.remove(p);
						main.setroom_type.remove(p);
						p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "La salle a été créée et a été ajoutée au donjon.");
					}
					if(type == RoomType.BOSSROOM){
						Door door = new Door(DoorType.BOSS, main.setdoor.get(p.getName() + 1), main.setdoor.get(p.getName() + 2), location, false);
						ArrayList<Door> doors = new ArrayList<Door>();
						doors.add(door);
						Room room = new Room(name, doors, loc, type);
						donj.addRoom(room);
						main.setdoor.remove(p.getName() + 1);
						main.setdoor.remove(p.getName() + 2);
						main.setroom_donjon.remove(p);
						main.setroom_location.remove(p);
						main.setroom_name.remove(p);
						main.setroom_type.remove(p);
						p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "La salle a été créée et a été ajoutée au donjon.");
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent e){
		Player p = e.getPlayer();
		if(e.getItemDrop().getItemStack().equals(items.horseinvocator)){
			e.getItemDrop().remove();
			p.getInventory().setItem(8, items.horseinvocator);
		}
		else if(e.getItemDrop().getItemStack().equals(items.parchemin_1)){
			e.getItemDrop().remove();
			p.getInventory().setItem(7, items.parchemin_1);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		p.getInventory().setItem(8, items.horseinvocator);
		p.getInventory().setItem(7, items.parchemin_1);
	}
	
	@EventHandler
	@SuppressWarnings("deprecation")
	public void onInventoryClick(InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		if(e.getInventory().equals(p.getInventory())){
			if(e.getSlot() == 7 || e.getSlot() == 8){
				p.updateInventory();
				e.setCancelled(true);
				p.closeInventory();
			}
			if(e.getSlot() == 0){
				Material type = e.getCursor().getType();
				if(!(type == Material.IRON_SWORD || type == Material.IRON_AXE ||
					type == Material.STONE_SWORD || type == Material.STONE_AXE ||
					type == Material.WOOD_SWORD || type == Material.WOOD_AXE ||
					type == Material.GOLD_SWORD || type == Material.GOLD_SWORD ||
					type == Material.DIAMOND_AXE || type == Material.DIAMOND_SWORD || type == Material.AIR)){
					p.closeInventory();
					p.getWorld().dropItem(p.getLocation(), e.getCursor());
					p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.DARK_RED + "Cette case ne peut contenir qu'une arme de melée");
					p.updateInventory();
					e.setCancelled(true);
				}
			}
			if(e.getSlot() == 1){
				Material type = e.getCursor().getType();
				if(!(type == Material.BOW || e.getCursor() == items.arbalete || type == Material.AIR)){
					p.closeInventory();
					p.getWorld().dropItem(p.getLocation(), e.getCursor());
					p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.DARK_RED + "Cette case ne peut contenir qu'une arme de portée");
					p.updateInventory();
					e.setCancelled(true);
				}
			}
		}	
	}

	
}
