package fr.rpg.thepen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.gmail.molnardad.quester.Quester;

import de.slikey.effectlib.EffectLib;
import de.slikey.effectlib.EffectManager;
import fr.rpg.thepen.listener.AutoRebuildListener;
import fr.rpg.thepen.listener.DamageListener;
import fr.rpg.thepen.listener.InventoryListener;
import fr.rpg.thepen.listener.NoBigTreesListener;
import fr.rpg.thepen.listener.NoDurabilityListener;
import fr.rpg.thepen.listener.NoExpListener;
import fr.rpg.thepen.listener.PlayerListener;
import fr.rpg.thepen.listener.ScrollListener;

public class Main extends JavaPlugin implements Listener {
	
	ArrayList<Player> setters = new ArrayList<Player>();
	public HashMap<Player, String> setdonjon_name = new HashMap<Player, String>();
	public HashMap<Player, Location> setdonjon_location = new HashMap<Player, Location>();
	public HashMap<String, Location> setdoor = new HashMap<String, Location>();
	public ArrayList<Donjon> donjons = new ArrayList<Donjon>();
	public static HashMap<Player, Donjon> indonjon = new HashMap<Player, Donjon>();
	public HashMap<Player, RoomType> setroom_type = new HashMap<Player, RoomType>();
	public HashMap<Player, Donjon> setroom_donjon = new HashMap<Player, Donjon>();
	public HashMap<Player, String> setroom_name = new HashMap<Player, String>();
	public HashMap<Player, Location> setroom_location = new HashMap<Player, Location>();
	public Items items = new Items();
	public EffectManager effectmanager;
	ScoreboardManager manager;
	public Scoreboard board;
	Quester quester;
	public ArrayList<Player> arbalete = new ArrayList<Player>();
	
	@Override
	public void onEnable() {
		saveConfig();
		getServer().getPluginManager().registerEvents(new InventoryListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new AutoRebuildListener(), this);
		getServer().getPluginManager().registerEvents(new DamageListener(), this);
		getServer().getPluginManager().registerEvents(new NoDurabilityListener(), this);
		getServer().getPluginManager().registerEvents(new NoExpListener(), this);
		getServer().getPluginManager().registerEvents(new ScrollListener(), this);
		getServer().getPluginManager().registerEvents(new NoBigTreesListener(), this);
		loadDonjons();
		
		System.out.println("[RPG] Enable");
		Bukkit.getServer().getPluginManager().registerEvents(this, this);	
		setupItems();
		 EffectLib lib = EffectLib.instance();
		 effectmanager = new EffectManager(lib);
		 manager = Bukkit.getScoreboardManager();
		 board = manager.getNewScoreboard();
		 setupQuester();
		 
		 Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			
			@Override
			public void run() {
				for(int i = 0; i < Bukkit.getServer().getOnlinePlayers().length; i++){
					Player p = Bukkit.getServer().getOnlinePlayers()[i];
					if(p.getLevel() < 100){
						p.setLevel(p.getLevel() + 1);
					}
				}
				
			}
		}, 0, 20);
	}

	@Override
	public void onDisable() {
		if(!donjons.isEmpty()){
		saveDonjons();
		}
		saveConfig();
	}

	//Getters
	
	public String getRace(Player p){
		return (String) getConfig().get("users." + p.getName() + ".race");
	}
	
	public int getLevel(Player p){
		return getConfig().getInt("users." + p.getName() + ".level");
	}
	
	public int getExp(Player p){
		return getConfig().getInt("users." + p.getName() + ".experience");
	}
	
	public int getMoney(Player p){
		return getConfig().getInt("users." + p.getName() + ".argent");
	}
	
	public int getMana(Player p){
		return p.getLevel();
	}
	
	public Donjon getDonjonByName(String name){
		for(int i = 0; i < donjons.size(); i++){
			Donjon donj1 = donjons.get(i);
			if(donj1.getName().equals(name)){
				return donj1;
			}
		}
		return null;
	}

	public ArrayList<String> getDonjonsList(){
		ArrayList<String> names = new ArrayList<String>();
		for(int i = 0; i < donjons.size(); i++){
			Donjon donj = donjons.get(i);
			names.add(donj.getName());
		}
		return names;
	}
	
	public static Player[] getNearbyPlayers(Location l, int radius){
		int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16))/16;
		HashSet<Player> radiusPlayers = new HashSet<Player>();
		for (int chX = 0 -chunkRadius; chX <= chunkRadius; chX ++){
			for (int chZ = 0 -chunkRadius; chZ <= chunkRadius; chZ++){
				int x=(int) l.getX(),y=(int) l.getY(),z=(int) l.getZ();
				for (Entity e : new Location(l.getWorld(),x+(chX*16),y,z+(chZ*16)).getChunk().getEntities()){
					if (e instanceof Player) {
						Player p = (Player) e;
						if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock()) {
							radiusPlayers.add(p);
						}
					}
				}
			}
		}
		return radiusPlayers.toArray(new Player[radiusPlayers.size()]);
	}
	
	//Setters
	
	public void setMana(Player p, int level){
		p.setLevel(level);
	}
	
	//Setups
	
	public void setupItems(){
		
		items.m_arbalete.setDisplayName(ChatColor.DARK_AQUA + "Arbalète");
		items.arbalete.setItemMeta(items.m_arbalete);
		
		items.m_carreau.setDisplayName(ChatColor.DARK_RED + "Carreau d'arbalète");
		items.carreau.setItemMeta(items.m_carreau);
		
		items.m_parchemin_1.setDisplayName(ChatColor.GOLD + "Parchemin Magique");
		ArrayList<String> desc = new ArrayList<String>();
		desc.add(ChatColor.DARK_BLUE + " Parchemin du retour.");
		desc.add(ChatColor.AQUA + " Coût de mana: 15");
		desc.add(ChatColor.GREEN + " Permet de revenir");
		desc.add(ChatColor.GREEN + " à sa base.");
		items.m_parchemin_1.setLore(desc);
		items.parchemin_1.setItemMeta(items.m_parchemin_1);
		
		items.m_parchemin_2.setDisplayName(ChatColor.GOLD + "Parchemin Magique");
		ArrayList<String> desc1 = new ArrayList<String>();
		desc1.add(ChatColor.DARK_BLUE + " Boule de Feu.");
		desc1.add(ChatColor.AQUA + " Coût de mana: 25");
		desc1.add(ChatColor.GREEN + " Lance une boule de feu");
		items.m_parchemin_2.setLore(desc1);
		items.parchemin_2.setItemMeta(items.m_parchemin_2);
		
		items.m_parchemin_3.setDisplayName(ChatColor.GOLD + "Parchemin Magique");
		ArrayList<String> desc2 = new ArrayList<String>();
		desc2.add(ChatColor.DARK_BLUE + " Empoisonnement.");
		desc2.add(ChatColor.AQUA + " Coût de mana: 25");
		desc2.add(ChatColor.GREEN + " Empoisonne les joueurs");
		desc2.add(ChatColor.GREEN + " dans un rayon de 6 blocs.");
		items.m_parchemin_3.setLore(desc2);
		items.parchemin_3.setItemMeta(items.m_parchemin_3);
		
		items.m_parchemin_4.setDisplayName(ChatColor.GOLD + "Parchemin Magique");
		ArrayList<String> desc3 = new ArrayList<String>();
		desc3.add(ChatColor.DARK_BLUE + " Aura de feu");
		desc3.add(ChatColor.AQUA + " Coût de mana: 30");
		desc3.add(ChatColor.GREEN + " Créé un cercle de feu");
		desc3.add(ChatColor.GREEN + " autour de vous.");
		items.m_parchemin_4.setLore(desc3);
		items.parchemin_4.setItemMeta(items.m_parchemin_4);
		
		items.m_parchemin_5.setDisplayName(ChatColor.GOLD + "Parchemin Magique");
		ArrayList<String> desc4 = new ArrayList<String>();
		desc4.add(ChatColor.DARK_BLUE + " Choc Terrestre");
		desc4.add(ChatColor.AQUA + " Coût de mana: 30");
		desc4.add(ChatColor.GREEN + " Créé un choc sur le sol ");
		desc4.add(ChatColor.GREEN + " faisant voler les joueurs autour");
		desc4.add(ChatColor.GREEN + " de vous dans un rayon de 5 blocs.");
		items.m_parchemin_5.setLore(desc4);
		items.parchemin_5.setItemMeta(items.m_parchemin_5);
		
		items.m_parchemin_6.setDisplayName(ChatColor.GOLD + "Parchemin Magique");
		ArrayList<String> desc5 = new ArrayList<String>();
		desc5.add(ChatColor.DARK_BLUE + " Invisibilité");
		desc5.add(ChatColor.AQUA + " Coût de mana: 30");
		desc5.add(ChatColor.GREEN + " Vous confère ");
		desc5.add(ChatColor.GREEN + " 30 secondes ");
		desc5.add(ChatColor.GREEN + " d'invisibilité.");
		items.m_parchemin_6.setLore(desc5);
		items.parchemin_6.setItemMeta(items.m_parchemin_6);
		
		items.m_parchemin_7.setDisplayName(ChatColor.GOLD + "Parchemin Magique");
		ArrayList<String> desc6 = new ArrayList<String>();
		desc6.add(ChatColor.DARK_BLUE + " Heal");
		desc6.add(ChatColor.AQUA + " Coût de mana: 30");
		desc6.add(ChatColor.GREEN + " Vous redonne de la vie ");
		items.m_parchemin_7.setLore(desc6);
		items.parchemin_7.setItemMeta(items.m_parchemin_7);
	}
	
	private boolean setupQuester()
	  {
	    Plugin plugin = getServer().getPluginManager().getPlugin("Quester");
	    if ((plugin != null) && ((plugin instanceof Quester))) {
	      quester = (Quester)plugin;
	    }
	    return quester != null;
	  }
	
	
	//Conditions
	
	public static boolean isInDonjon(Player p){
		if(indonjon.containsKey(p)){
			return true;
		}
		else{
			return false;
		}
	}
	
	
	//Saves
	
	public void saveDonjons(){

		List<String> dnames = new ArrayList<String>();
		for(int i = 0; i < donjons.size(); i++){
			Donjon donj = donjons.get(i);
			dnames.add(donj.getName());
			getConfig().set("donjons." + donj.getName() + ".name", donj.getName());
			getConfig().set("donjons." + donj.getName() + ".location", new SLocation(donj.getLocation()));
			getConfig().set("donjons." + donj.getName() + ".door.corner1", new SLocation(donj.getEnterDoor().getCorner1()));
			getConfig().set("donjons." + donj.getName() + ".door.corner2", new SLocation(donj.getEnterDoor().getCorner2()));
			getConfig().set("donjons." + donj.getName() + ".active", donj.isActive());
			getConfig().set("donjons." + donj.getName() + ".open", donj.isOpen());
			
			List<String> rnames = new ArrayList<String>();
			
			for(int i1 = 0; i1 < donj.getRooms().size(); i1++){
				Room room = donj.getRooms().get(i1);
				rnames.add(room.getName());

				getConfig().set("donjons." + donj.getName() + ".rooms." + room.getName() + ".name" , room.getName());
				getConfig().set("donjons." + donj.getName() + ".rooms." + room.getName() + ".location" , new SLocation(room.getLocation()));
				getConfig().set("donjons." + donj.getName() + ".rooms." + room.getName() + ".type" , room.getType().toString());
				
				int doors = 0;
				for(int i11 = 0; i11 < room.getDoors().size(); i11++){
					doors ++;
					Door door = room.getDoors().get(i11);
					getConfig().set("donjons." + donj.getName() + ".rooms." + room.getName() + ".doors." + String.valueOf(doors) + ".corner1" , new SLocation(door.getCorner1()));
					getConfig().set("donjons." + donj.getName() + ".rooms." + room.getName() + ".doors." + String.valueOf(doors) + ".corner2" , new SLocation(door.getCorner2()));
					getConfig().set("donjons." + donj.getName() + ".rooms." + room.getName() + ".doors." + String.valueOf(doors) + ".type" , door.getType().toString());
					getConfig().set("donjons." + donj.getName() + ".rooms." + room.getName() + ".doors." + String.valueOf(doors) + ".open" , door.isOpenDefault());
					if(door.getType() == DoorType.BOSS || door.getType() == DoorType.FIGHTROOMKEY || door.getType() == DoorType.KEY){
						getConfig().set("donjons." + donj.getName() + ".rooms." + room.getName() + ".doors." + String.valueOf(doors) + ".key" , new SLocation(door.getLock()));
					}

				}

				getConfig().set("donjons." + donj.getName() + ".rooms." + room.getName() + ".doors.count" , room.getDoors().size());
			}
			getConfig().set("donjons." + donj.getName() + ".rooms.list", rnames);
		}
		saveConfig();
		getConfig().set("donjons.list", dnames);
	}
	
	public void loadDonjons(){
		
		List<String> dnames = getConfig().getStringList("donjons.list");
		for(int i = 0; i < dnames.size(); i++){
			String name = dnames.get(i);
			Location loc = (SLocation) getConfig().get("donjons." + name + ".location");
			Location corner1 = (SLocation) getConfig().get("donjons." + name + ".door.corner1");
			Location corner2 = (SLocation) getConfig().get("donjons." + name + ".door.corner2");
			boolean active = getConfig().getBoolean("donjons." + name + ".active");
			boolean open = getConfig().getBoolean("donjons." + name + ".open");
			Donjon donj = new Donjon(loc, name, new Door(DoorType.NORMAL, corner1, corner2, true), active);
			if(open){
				donj.setOpen();
			}
			else{
				donj.setClose();
			}
			List<String> rnames = getConfig().getStringList("donjons." + donj.getName() + ".rooms.list");
			
			for(int j = 0; j < rnames.size(); j++){
				String rname = rnames.get(j);
				Location rloc = (SLocation) getConfig().get("donjons." + name + ".rooms." + rname + ".location");
				String type = getConfig().getString("donjons." + name + ".rooms." + rname + ".type");
				int doorcount = getConfig().getInt("donjons." + name + ".rooms." + rname + ".doors.count" );
				ArrayList<Door> doorslist = new ArrayList<Door>();
				int doors = 0;
					for(int x = 1; x <= doorcount; x++){
						doors++;
						Location dcorner1 = (SLocation) getConfig().get("donjons." + name + ".rooms." + rname + ".doors." + doors + ".corner1");
						Location dcorner2 = (SLocation) getConfig().get("donjons." + name + ".rooms." + rname + ".doors." + doors + ".corner2");
						String dtype = getConfig().getString("donjons." + name + ".rooms." + rname + ".doors." + doors + ".type");
						boolean opendefault = getConfig().getBoolean("donjons." + name + ".rooms." + rname + ".doors." + doors + ".open");
							
						if(dtype.equals(DoorType.BOSS.toString()) || dtype.equals(DoorType.FIGHTROOMKEY.toString()) || dtype.equals(DoorType.KEY.toString())){
							if(getConfig().get("donjons." + name + ".rooms." + rname + ".doors." + doors + ".key") != null){
							Location dloc = (SLocation) getConfig().get("donjons." + name + ".rooms." + rname + ".doors." + doors + ".key");
							
							Door door = new Door(DoorType.valueOf(dtype), dcorner1, dcorner2, dloc, opendefault);
							door.setLock(dloc);
							doorslist.add(door);
							System.out.println("key door");
							}
							else{
								System.out.println("Location key = null");
							}
						}
						else{
							System.out.println("default door");
							Door door = new Door(DoorType.valueOf(dtype), dcorner1, dcorner2, opendefault);
							doorslist.add(door);
						}
					}
				Room room = new Room(rname, doorslist, rloc, RoomType.valueOf(type));	
				donj.addRoom(room);
			}
			donjons.add(donj);
		}
		saveConfig();
	}

	//Fonctions
	
	public void createCyl(Location loc, int r, Material mat) {
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        World w = loc.getWorld();
        int rSquared = r * r;
        for (int x = cx - r; x <= cx + r; x++) {
            for (int z = cz - r; z <= cz + r; z++) {
                if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared) {
                    Location cylBlock = new Location(w, x, cy, z);
                    if(w.getBlockAt(cylBlock).getType().equals(Material.AIR) || w.getBlockAt(cylBlock).getType().equals(Material.FIRE))
                     w.getBlockAt(cylBlock).setType(mat);
                }
            }
        }
    }
	
	private void openGUI(Player player){
		Inventory inv = Bukkit.createInventory(null, 9, ChatColor.AQUA + "Selectionne ta race");
		ItemStack humain = new ItemStack(Material.BOOK);
		ItemMeta m_humain = humain.getItemMeta();
		ItemStack nain = new ItemStack(Material.IRON_PICKAXE);
		ItemMeta m_nain = nain.getItemMeta();
		ItemStack orc = new ItemStack(Material.STONE_SWORD);
		ItemMeta m_orc = orc.getItemMeta();
		ItemStack elfe = new ItemStack(Material.BOW);
		ItemMeta m_elfe = elfe.getItemMeta();
		
		m_humain.setDisplayName(ChatColor.GOLD + "Humain");
		m_nain.setDisplayName(ChatColor.DARK_GRAY + "Nain");
		m_orc.setDisplayName(ChatColor.DARK_GREEN + "Orc");
		m_elfe.setDisplayName(ChatColor.WHITE + "Elfe");
		
		humain.setItemMeta(m_humain);
		nain.setItemMeta(m_nain);
		orc.setItemMeta(m_orc);
		elfe.setItemMeta(m_elfe);
		
		inv.setItem(0, humain);
		inv.setItem(1, nain);
		inv.setItem(2, orc);
		inv.setItem(3, elfe);
		
		player.openInventory(inv);
		
	}



	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String CommandLabel, String[] args){
		Player p = (Player) sender;
		if(CommandLabel.equals("rpg")){
			if(args.length == 0){
				p.sendMessage(ChatColor.GOLD + "-------------------" + ChatColor.AQUA + "[" + ChatColor.GOLD + "HellFire RPG" + ChatColor.AQUA + "]" + ChatColor.GOLD + "-------------------");
				p.sendMessage(ChatColor.AQUA + "/rpg adddonjon <name> :  " + ChatColor.GOLD + "Utilisé pour créer un donjon");
				p.sendMessage(ChatColor.AQUA + "/rpg addroom <donjon> <name> <type>:  " + ChatColor.GOLD + "Utilisé pour ajouter une salle à un donjon");
				p.sendMessage(ChatColor.AQUA + "/rpg activedonjon <donjon> :  " + ChatColor.GOLD + "Utilisé pour activer un donjon");
				p.sendMessage(ChatColor.AQUA + "/rpg adddonjon <name> :  " + ChatColor.GOLD + "Utilisé pour créer un donjon");
			}
			else if(args[0].equals("adddonjon")){
				if(args.length == 2){
					p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "Le donjon à été créé avec succès!");
					p.sendMessage(ChatColor.GOLD + "[RPG]" + ChatColor.AQUA + " Veuillez maintenant definir les point 1 et 2 de la porte du donjon");
					setdonjon_name.put(p, args[1]);
					setdonjon_location.put(p, p.getLocation());
				}
				else{
					p.sendMessage(ChatColor.GOLD + "[RPG]" + ChatColor.DARK_RED + " Vous devez ajouter un nom à votre donjon.");
				}
			}
			else if(args[0].equals("addroom")){
				if(args.length == 4){
					if(getDonjonByName(args[1]) != null){
						Donjon donj = getDonjonByName(args[1]);
							for(int i = 0; i < donj.getRooms().size(); i++){
								Room room = donj.getRooms().get(i);
								if(room.getName().equals(args[2])){
									p.sendMessage(ChatColor.GOLD + "[RPG]" + ChatColor.DARK_RED + " La salle " + args[2] + " existe déjà.");
									return false;
								}
							}
						switch(args[3]){
							case "fightroom":
								setroom_donjon.put(p, donj);
								setroom_name.put(p, args[2]);
								setroom_type.put(p, RoomType.FIGHTROOM);
								setroom_location.put(p, p.getLocation());
								p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "Maintenant, ajoute des portes à ta salle.");
								p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "Selectionne via un clic droit le point 1 et le point 2.");
								p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "Quand tu as ajouté ta porte, si tu veux en ajouter une nouvelle, reselectionne ta porte. sinon, fait /rpg finish.");
								break;
							case "bossroom":
								setroom_donjon.put(p, donj);
								setroom_name.put(p, args[2]);
								setroom_type.put(p, RoomType.BOSSROOM);
								setroom_location.put(p, p.getLocation());
								p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "Maintenant, ajoute des portes à ta salle.");
								p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "Selectionne via un clic droit le point 1 et le point 2.");
								break;
							case "rewardroom":
								setroom_donjon.put(p, donj);
								setroom_name.put(p, args[2]);
								setroom_type.put(p, RoomType.REWARDROOM);
								setroom_location.put(p, p.getLocation());
								p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "Maintenant, ajoute des portes à ta salle.");
								p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "Selectionne via un clic droit le point 1 et le point 2.");
								break;
							case "room":
								setroom_donjon.put(p, donj);
								setroom_name.put(p, args[2]);
								setroom_type.put(p, RoomType.ROOM);
								setroom_location.put(p, p.getLocation());
								p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "Maintenant, ajoute des portes à ta salle.");
								p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "Selectionne via un clic droit le point 1 et le point 2.");
							case "rewardroomkey":
								setroom_donjon.put(p, donj);
								setroom_name.put(p, args[2]);
								setroom_type.put(p, RoomType.REWARDROOMKEY);
								setroom_location.put(p, p.getLocation());
								p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "Maintenant, ajoute des portes à ta salle.");
								p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "Selectionne via un clic droit le point 1 et le point 2.");
								break;
							case "roomkey":
								setroom_donjon.put(p, donj);
								setroom_name.put(p, args[2]);
								setroom_type.put(p, RoomType.ROOMKEY);
								setroom_location.put(p, p.getLocation());
								p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "Maintenant, ajoute des portes à ta salle.");
								p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "Selectionne via un clic droit le point 1 et le point 2.");
								break;
							default:
								p.sendMessage(ChatColor.GOLD + "[RPG]" + ChatColor.DARK_RED + " Le type de salle est incorrect... Les types sont: room, roomkey, bossroom, fightroom, rewardroom, rewardroomkey.");
								
						}
					}
					else{
						p.sendMessage(ChatColor.GOLD + "[RPG]" + ChatColor.DARK_RED + " Le donjon " + args[1] + " n'existe pas... Veuillez vérifier l'ortographe.");
					}
				}
			}
			else if(args[0].equals("getkey")){
				if(args.length == 3){
					if(getDonjonByName(args[2]) != null){
						Donjon donj = getDonjonByName(args[2]);
						if(donj.getRoomByName(args[1]) != null){
							Room room = donj.getRoomByName(args[1]);
							switch (room.getType()){
							case BOSSROOM:
								ItemStack item = new ItemStack(Material.NAME_TAG);
								ItemMeta meta = item.getItemMeta();
								meta.setDisplayName(ChatColor.GOLD + "BossKey");
								ArrayList<String> lore = new ArrayList<String>();
								lore.add(donj.getName());
								lore.add(room.getName());
								meta.setLore(lore);
								item.setItemMeta(meta);
								p.getInventory().addItem(item);
								p.updateInventory();
								break;
							case REWARDROOMKEY:
								ItemStack item2 = new ItemStack(Material.TRIPWIRE_HOOK);
								ItemMeta meta2 = item2.getItemMeta();
								meta2.setDisplayName(ChatColor.GOLD + "Key");
								ArrayList<String> lore2 = new ArrayList<String>();
								lore2.add(donj.getName());
								lore2.add(room.getName());
								meta2.setLore(lore2);
								item2.setItemMeta(meta2);
								p.getInventory().addItem(item2);
								p.updateInventory();
								break;
							case ROOMKEY:
								ItemStack item21 = new ItemStack(Material.TRIPWIRE_HOOK);
								ItemMeta meta21 = item21.getItemMeta();
								meta21.setDisplayName(ChatColor.GOLD + "Key");
								ArrayList<String> lore21 = new ArrayList<String>();
								lore21.add(donj.getName());
								lore21.add(room.getName());
								meta21.setLore(lore21);
								item21.setItemMeta(meta21);
								p.getInventory().addItem(item21);
								p.updateInventory();
								break;
							case FIGHTROOMKEY:
								ItemStack item211 = new ItemStack(Material.TRIPWIRE_HOOK);
								ItemMeta meta211 = item211.getItemMeta();
								meta211.setDisplayName(ChatColor.GOLD + "Key");
								ArrayList<String> lore211 = new ArrayList<String>();
								lore211.add(donj.getName());
								lore211.add(room.getName());
								meta211.setLore(lore211);
								item211.setItemMeta(meta211);
								p.getInventory().addItem(item211);
								p.updateInventory();
								break;
							default:
								break;
							
							}
						}
						
					}
				}
			}
			else if(args[0].equals("activedonjon")){
				if(args.length == 2){
					if(getDonjonByName(args[1]) != null){
						Donjon donj = getDonjonByName(args[1]);
						donj.setActive();
						donj.setOpen();
						p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "Le donjon est maintenant actif");
						for(int i = 0; i < donj.getDoors().size(); i++){
							Door door = donj.getDoors().get(i);
							if(door.isOpenDefault()){
								door.setOpen();
							}
							else{
								door.setClose();
							}
						}
					}
				}
			}
			else if(args[0].equals("getdonjonslist")){
				for(int i = 0; i < getDonjonsList().size(); i++){
					p.sendMessage(getDonjonsList().get(i));
				}
			}
			else if(args[0].equals("getroomtype")){
				if(args.length == 3){
					if(getDonjonByName(args[1]) != null){
						Donjon donj = getDonjonByName(args[1]);
						if(donj.getRoomByName(args[2]) != null){
							Room room = donj.getRoomByName(args[2]);
							p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "La salle " + args[2] + " est une salle de type " + room.getType().toString());
						}
					}
				}
			}
			else if(args[0].equals("getdoorscount")){
				if(args.length == 3){
					if(getDonjonByName(args[1]) != null){
						Donjon donj = getDonjonByName(args[1]);
						if(donj.getRoomByName(args[2]) != null){
							Room room = donj.getRoomByName(args[2]);
							p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "La salle " + args[2] + " contient " + room.getDoors().size() + " portes.");
						}
					}
				}
			}
			else if(args[0].equals("getrooms")){
				if(args.length == 2){
					if(getDonjonByName(args[1]) != null){
						Donjon donj = getDonjonByName(args[1]);
						p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "Le donjon " + donj.getName() + " contient les salles suivantes:");
						for(int i = 0; i < donj.getRooms().size(); i++){
							Room room = donj.getRooms().get(i);
							p.sendMessage(ChatColor.BLUE + room.getName());
						}
					}
				}
			}
			else if(args[0].equals("getdoorstype")){
				if(args.length == 3){
					if(getDonjonByName(args[1]) != null){
						Donjon donj = getDonjonByName(args[1]);
						if(donj.getRoomByName(args[2]) != null){
							Room room = donj.getRoomByName(args[2]);
							for(int i = 0; i < room.getDoors().size(); i++){
								p.sendMessage(room.getDoors().get(i).getType().toString());
							}
						}
					}
				}
			}
			else if(args[0].equals("closeroom")){
				if(args.length == 3){
					if(getDonjonByName(args[1]) != null){
						Donjon donj = getDonjonByName(args[1]);
						if(donj.getRoomByName(args[2]) != null){
							Room room = donj.getRoomByName(args[2]);
							for(int i = 0; i < room.getDoors().size(); i++){
								room.getDoors().get(i).setClose();
								p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "La salle à été fermée !");
							}
						}
					}
				}
			}
			else if(args[0].equals("getlock")){
				if(args.length == 3){
					if(getDonjonByName(args[1]) != null){
						Donjon donj = getDonjonByName(args[1]);
						if(donj.getRoomByName(args[2]) != null){
							Room room = donj.getRoomByName(args[2]);
							for(int i = 0; i < room.getDoors().size(); i++){
								p.teleport(room.getDoors().get(i).getLock());
							}
						}
					}
				}
			}
			else if(args[0].endsWith("spawncustom")){
				
			}
		}
		if(args[0].equals("GUI")){
			openGUI(p);
		}
		if(args.length == 2){
			
			if(args[0].equals("givescroll")){
				switch(args[1]){
				case "retour" : 
					p.getInventory().addItem(items.parchemin_1);
					break;
				case "fireball" : 
					p.getInventory().addItem(items.parchemin_2);
					break;
				case "poison" : 
					p.getInventory().addItem(items.parchemin_3);
					break;
				case "fireaura" : 
					p.getInventory().addItem(items.parchemin_4);
					break;
				case "choc" : 
					p.getInventory().addItem(items.parchemin_5);
					break;
				case "invisibility" : 
					p.getInventory().addItem(items.parchemin_6);
					break;
				case "heal" : 
					p.getInventory().addItem(items.parchemin_7);
					break;
				}
					
			}
			if(args[0].equals("getslot")){
				int slot = Integer.parseInt(args[1]);
				ItemStack item = p.getInventory().getItem(slot);
				p.sendMessage("Le slot " + args[1] + " contient " + item.getType());
			}
			
			if(args[0].equals("getrace")){
				
				if(getConfig().get("users." + args[1] + ".race") != null){
					p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "Le joueur " + args[1] + " est un " + getConfig().get("users." + args[1] + ".race"));
				}
				else{
					p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.DARK_RED + "Le joueur n' a pas de race!");
				}
			}
			if(args[0].equals("setrace")){
				if(args[1].equals("humains")){
					getConfig().set("humains.x", p.getLocation().getX());
					getConfig().set("humains.y", p.getLocation().getY());
					getConfig().set("humains.z", p.getLocation().getZ());
					getConfig().set("humains.yaw",  (float)p.getLocation().getYaw());
					getConfig().set("humains.pitch",  (float)p.getLocation().getPitch());
					getConfig().set("humains.w", p.getWorld().getName());
					p.sendMessage(ChatColor.GOLD + "[RPG]" +  ChatColor.AQUA + " La position d'apparition des humains à bien été sauvegardée!");
					saveConfig();
				}
				if(args[1].equals("elfes")){
					getConfig().set("elfes.x", p.getLocation().getX());
					getConfig().set("elfes.y", p.getLocation().getY());
					getConfig().set("elfes.z", p.getLocation().getZ());
					getConfig().set("elfes.yaw",  (float)p.getLocation().getYaw());
					getConfig().set("elfes.pitch",  (float)p.getLocation().getPitch());
					getConfig().set("elfes.w", p.getLocation().getWorld().getName());
					p.sendMessage(ChatColor.GOLD + "[RPG]" +  ChatColor.AQUA + " La position d'apparition des elfes à bien été sauvegardée!");
					saveConfig();
				}
				if(args[1].equals("orcs")){
					getConfig().set("orcs.x", p.getLocation().getX());
					getConfig().set("orcs.y", p.getLocation().getY());
					getConfig().set("orcs.z", p.getLocation().getZ());
					getConfig().set("orcs.w", p.getWorld().getName());
					getConfig().set("orcs.yaw", (float) p.getLocation().getYaw());
					getConfig().set("orcs.pitch", (float) p.getLocation().getPitch());
					p.sendMessage(ChatColor.GOLD + "[RPG]" +  ChatColor.AQUA + " La position d'apparition des orcs à bien été sauvegardée!");
					saveConfig();
				}
				if(args[1].equals("nains")){
					getConfig().set("nains.x", p.getLocation().getX());
					getConfig().set("nains.y", p.getLocation().getY());
					getConfig().set("nains.z", p.getLocation().getZ());
					getConfig().set("nains.yaw",  (float)p.getLocation().getYaw());
					getConfig().set("nains.pitch",  (float)p.getLocation().getPitch());
					getConfig().set("nains.w", p.getWorld().getName());
					p.sendMessage(ChatColor.GOLD + "[RPG]" +  ChatColor.AQUA + " La position d'apparition des nains à bien été sauvegardée!");
					saveConfig();
				}
				if(args[1].equals("demons")){
					getConfig().set("demons.x", p.getLocation().getX());
					getConfig().set("demons.y", p.getLocation().getY());
					getConfig().set("demons.z", p.getLocation().getZ());
					getConfig().set("demons.w", p.getWorld().getName());
					getConfig().set("demons.yaw",  (float)p.getLocation().getYaw());
					getConfig().set("demons.pitch", (float) p.getLocation().getPitch());
				}
				if(args[1].equals("anges")){
					getConfig().set("anges.x", p.getLocation().getX());
					getConfig().set("anges.y", p.getLocation().getY());
					getConfig().set("anges.z", p.getLocation().getZ());
					getConfig().set("anges.w", p.getWorld().getName());
					getConfig().set("anges.yaw", (float) p.getLocation().getYaw());
					getConfig().set("anges.pitch", (float) p.getLocation().getPitch());
				}
			}
		}
		return false;
	}


	  
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public class CooldownArbalete implements Runnable{

		ArrayList<Player> list;
		Player p;
		public void setList(ArrayList<Player> liste, Player player){
			list = liste;
			p = player;
		}
		
		@Override
		public void run() {
			try {
				Thread.sleep(5000);
				list.remove(p);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
