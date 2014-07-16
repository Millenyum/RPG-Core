package fr.rpg.thepen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Tree;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

import com.gmail.molnardad.quester.Quester;

import de.slikey.effectlib.EffectLib;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.WarpEntityEffect;
import de.slikey.effectlib.util.ParticleEffect;
import fr.rpg.thepen.custommobs.CustomEntityZombie;

public class Main extends JavaPlugin implements Listener {
	
	ArrayList<Player> setters = new ArrayList<Player>();
	HashMap<Player, String> setdonjon_name = new HashMap<Player, String>();
	HashMap<Player, Location> setdonjon_location = new HashMap<Player, Location>();
	HashMap<String, Location> setdoor = new HashMap<String, Location>();
	ArrayList<Donjon> donjons = new ArrayList<Donjon>();
	HashMap<Player, Donjon> indonjon = new HashMap<Player, Donjon>();
	HashMap<Player, RoomType> setroom_type = new HashMap<Player, RoomType>();
	HashMap<Player, Donjon> setroom_donjon = new HashMap<Player, Donjon>();
	HashMap<Player, String> setroom_name = new HashMap<Player, String>();
	HashMap<Player, Location> setroom_location = new HashMap<Player, Location>();
	
	public Items items = new Items();
	EffectManager effectmanager;
	ScoreboardManager manager;
	Scoreboard board;
	Quester quester;
	public ArrayList<Player> arbalete = new ArrayList<Player>();
	
	@Override
	public void onEnable() {
		saveConfig();
		getServer().getPluginManager().registerEvents(this, this);
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
	
	public boolean isInDonjon(Player p){
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
	
	
	//Fonctions à part
	
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

	
	//Events

	@EventHandler
	public void onTreeGrow(StructureGrowEvent e){
		if (e.getSpecies() == TreeType.BIG_TREE){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location from = e.getFrom();
		Location to = e.getTo();
		if(isInDonjon(p) == false){
			for(int i = 0; i < donjons.size(); i++){
				int radius = 2;
				if ((from.getBlockX() != to.getBlockX()) || (from.getBlockZ() != to.getBlockZ())) {
					if(getNearbyPlayers(donjons.get(i).getLocation(), radius).length >= 1) {
						if(donjons.get(i).isActive()){
							if(donjons.get(i).isOpen()){
								donjons.get(i).setClose();
								p.sendMessage("Ce tombeau sera votre tombeau!");
								p.getWorld().playSound(donjons.get(i).getLocation(), Sound.WITHER_SPAWN, 1, 1);
								indonjon.put(p, donjons.get(i));
							}
						}
					}
				}
			}
		}
		if(isInDonjon(p)){
			Donjon donj = indonjon.get(p);
			for(int i = 0; i < donj.getRooms().size(); i++){
				int radius = 2;
				Room room = donj.getRooms().get(i);
				if ((from.getBlockX() != to.getBlockX()) || (from.getBlockZ() != to.getBlockZ())) {
					if(getNearbyPlayers(room.getLocation(), radius).length >= 1) {
						if(room.getType() == RoomType.FIGHTROOM){
							room.setClosed();
						}
					}
				}
			}
		}
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

	@EventHandler 
	public void onPlayerInteract(PlayerInteractEvent e){
		Player p = e.getPlayer();
		
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(isInDonjon(p)){
				if(e.getPlayer().getItemInHand().getType().equals(Material.NAME_TAG) || e.getPlayer().getItemInHand().getType().equals(Material.TRIPWIRE_HOOK)){
					Donjon donjon = indonjon.get(p);
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
			else if(setdonjon_name.containsKey(p)){
				if(!setdoor.containsKey(p.getName() + 1)){
					setdoor.put(p.getName() + 1, e.getClickedBlock().getLocation());
					p.sendMessage("Le point 1 à été défini. Selectionne le point 2 à présent.");
				}
				else{
					setdoor.put(p.getName() + 2, e.getClickedBlock().getLocation());
					Door door = new Door(DoorType.DONJON, setdoor.get(p.getName() + 1), setdoor.get(p.getName() + 2), true);
					setdoor.remove(p.getName() + 1);
					setdoor.remove(p.getName() + 2);
					Location loc = setdonjon_location.get(p);
					String nom = setdonjon_name.get(p);
					Donjon donj = new Donjon(loc, nom, door, false);
					donjons.add(donj);
					setdonjon_location.remove(p);
					setdonjon_name.remove(p);
					p.sendMessage("Donjon Created");
					donj.setClose();
				}
			}
			if(setroom_donjon.containsKey(p)){
				Donjon donj = setroom_donjon.get(p);
				String name = setroom_name.get(p);
				RoomType type = setroom_type.get(p);
				Location loc = setroom_location.get(p);
				if(!setdoor.containsKey(p.getName() + 1)){
					setdoor.put(p.getName() + 1, e.getClickedBlock().getLocation());
					p.sendMessage("Le point 1 à été défini. Selectionne le point 2 à présent.");
				}
				else if (setdoor.containsKey(p.getName() + 1) && !setdoor.containsKey(p.getName() + 2)){
					if(type == RoomType.FIGHTROOM || type == RoomType.ROOM || type == RoomType.REWARDROOM){
						setdoor.put(p.getName() + 2, e.getClickedBlock().getLocation());
						if(type == RoomType.FIGHTROOM){
							Door door = new Door(DoorType.FIGHTROOM, setdoor.get(p.getName() + 1), setdoor.get(p.getName() + 2), true);
							ArrayList<Door> doors = new ArrayList<Door>();
							doors.add(door);
							Room room = new Room(name, doors, loc, type);
							donj.addRoom(room);
							setdoor.remove(p.getName() + 1);
							setdoor.remove(p.getName() + 2);
							setroom_donjon.remove(p);
							setroom_location.remove(p);
							setroom_name.remove(p);
							setroom_type.remove(p);
							p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "La salle a été créée et a été ajoutée au donjon.");
						}
						else if(type == RoomType.ROOM){
							Door door = new Door(DoorType.NORMAL, setdoor.get(p.getName() + 1), setdoor.get(p.getName() + 2), true);
							ArrayList<Door> doors = new ArrayList<Door>();
							doors.add(door);
							Room room = new Room(name, doors, loc, type);
							donj.addRoom(room);
							setdoor.remove(p.getName() + 1);
							setdoor.remove(p.getName() + 2);
							setroom_donjon.remove(p);
							setroom_location.remove(p);
							setroom_name.remove(p);
							setroom_type.remove(p);
							p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "La salle a été créée et a été ajoutée au donjon.");
						}
						else if(type == RoomType.REWARDROOM){
							Door door = new Door(DoorType.NORMAL, setdoor.get(p.getName() + 1), setdoor.get(p.getName() + 2), false);
							ArrayList<Door> doors = new ArrayList<Door>();
							doors.add(door);
							Room room = new Room(name, doors, loc, type);
							donj.addRoom(room);
							setdoor.remove(p.getName() + 1);
							setdoor.remove(p.getName() + 2);
							setroom_donjon.remove(p);
							setroom_location.remove(p);
							setroom_name.remove(p);
							setroom_type.remove(p);
							p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "La salle a été créée et a été ajoutée au donjon.");
						}
					}
					else{
						setdoor.put(p.getName() + 2, e.getClickedBlock().getLocation());
						p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "Maintenant, selectionne d'un clic droit le bloc qui fera office de serrure.");
						
					}
				}
				else if(setdoor.containsKey(p.getName() + 2)){
					Location location = e.getClickedBlock().getLocation();
					if(type == RoomType.REWARDROOMKEY){
						Door door = new Door(DoorType.KEY, setdoor.get(p.getName() + 1), setdoor.get(p.getName() + 2), location, false);
						ArrayList<Door> doors = new ArrayList<Door>();
						doors.add(door);
						Room room = new Room(name, doors, loc, type);
						donj.addRoom(room);
						setdoor.remove(p.getName() + 1);
						setdoor.remove(p.getName() + 2);
						setroom_donjon.remove(p);
						setroom_location.remove(p);
						setroom_name.remove(p);
						setroom_type.remove(p);
						p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "La salle a été créée et a été ajoutée au donjon.");
				
					}
					if(type == RoomType.ROOMKEY){
						Door door = new Door(DoorType.KEY, setdoor.get(p.getName() + 1), setdoor.get(p.getName() + 2), location, false);
						ArrayList<Door> doors = new ArrayList<Door>();
						doors.add(door);
						Room room = new Room(name, doors, loc, type);
						donj.addRoom(room);
						setdoor.remove(p.getName() + 1);
						setdoor.remove(p.getName() + 2);
						setroom_donjon.remove(p);
						setroom_location.remove(p);
						setroom_name.remove(p);
						setroom_type.remove(p);
						p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "La salle a été créée et a été ajoutée au donjon.");
					}
					if(type == RoomType.FIGHTROOMKEY){
						Door door = new Door(DoorType.FIGHTROOMKEY, setdoor.get(p.getName() + 1), setdoor.get(p.getName() + 2), location, false);
						ArrayList<Door> doors = new ArrayList<Door>();
						doors.add(door);
						Room room = new Room(name, doors, loc, type);
						donj.addRoom(room);
						setdoor.remove(p.getName() + 1);
						setdoor.remove(p.getName() + 2);
						setroom_donjon.remove(p);
						setroom_location.remove(p);
						setroom_name.remove(p);
						setroom_type.remove(p);
						p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "La salle a été créée et a été ajoutée au donjon.");
					}
					if(type == RoomType.BOSSROOM){
						Door door = new Door(DoorType.BOSS, setdoor.get(p.getName() + 1), setdoor.get(p.getName() + 2), location, false);
						ArrayList<Door> doors = new ArrayList<Door>();
						doors.add(door);
						Room room = new Room(name, doors, loc, type);
						donj.addRoom(room);
						setdoor.remove(p.getName() + 1);
						setdoor.remove(p.getName() + 2);
						setroom_donjon.remove(p);
						setroom_location.remove(p);
						setroom_name.remove(p);
						setroom_type.remove(p);
						p.sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.AQUA + "La salle a été créée et a été ajoutée au donjon.");
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBreakBlock(BlockBreakEvent e){
		Material type = e.getPlayer().getItemInHand().getType();
		if(type == Material.DIAMOND_AXE || type == Material.DIAMOND_HOE || type == Material.DIAMOND_SPADE || type == Material.DIAMOND_PICKAXE || type == Material.DIAMOND_SWORD
			|| type == Material.IRON_AXE || type == Material.IRON_HOE || type == Material.IRON_PICKAXE || type == Material.IRON_SPADE || type == Material.IRON_SWORD
			|| type == Material.WOOD_AXE || type == Material.WOOD_HOE || type == Material.WOOD_PICKAXE || type == Material.WOOD_SPADE || type == Material.WOOD_SWORD 
			|| type == Material.STONE_AXE || type == Material.STONE_HOE || type == Material.STONE_PICKAXE || type == Material.STONE_SPADE || type == Material.STONE_SWORD
			|| type == Material.GOLD_AXE || type == Material.GOLD_HOE || type == Material.GOLD_PICKAXE || type == Material.GOLD_SPADE || type == Material.GOLD_SWORD || type == Material.SHEARS){
		e.getPlayer().getItemInHand().setDurability((short) 0);
		e.getPlayer().updateInventory();
		}
		if(e.getPlayer().isOp()){
			return;
		}
		
		if(e.getBlock().getType() == Material.SAPLING){
			e.setCancelled(true);
		}
		
		else{
			BlockInfo blockinfo = new BlockInfo(e.getBlock().getLocation(), e.getBlock().getType(), e.getBlock().getData());
			final HashMap<Location, BlockInfo> blocks = new HashMap<Location, BlockInfo>();
			final ArrayList<Location> blockslist = new ArrayList<Location>();
			blocks.put(e.getBlock().getLocation(), blockinfo);
			blockslist.add(e.getBlock().getLocation());
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {
				
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
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			
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

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract2(final PlayerInteractEvent e){
		if(e.getPlayer().getItemInHand().getItemMeta().equals(items.m_parchemin_1)){
			WarpEntityEffect effect = new WarpEntityEffect(effectmanager, e.getPlayer());
			effect.start();
			double x = getConfig().getDouble(getRace(e.getPlayer()).toLowerCase() + "s.x");
			double y = getConfig().getDouble(getRace(e.getPlayer()).toLowerCase() + "s.y");
			double z = getConfig().getDouble(getRace(e.getPlayer()).toLowerCase() + "s.z");
			float yaw = getConfig().getInt(getRace(e.getPlayer()).toLowerCase() + "s.yaw");
			float pitch = getConfig().getInt(getRace(e.getPlayer()).toLowerCase() + "s.pitch");
			Location loc = new Location(e.getPlayer().getWorld(), x, y, z, yaw, pitch);
			e.getPlayer().teleport(loc);
			e.setCancelled(true);
			e.getPlayer().updateInventory();
			effect.start();
		}
		if(e.getPlayer().getItemInHand().getItemMeta().equals(items.m_parchemin_2)){
			Fireball fire = e.getPlayer().launchProjectile(Fireball.class);
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 1000));
			e.setCancelled(true);
			e.getPlayer().updateInventory();
			fire.setVelocity(e.getPlayer().getLocation().getDirection().multiply(3));
		}
		if(e.getPlayer().getItemInHand().getItemMeta().equals(items.m_parchemin_3)){
			List<Entity> entitylist = e.getPlayer().getNearbyEntities(8, 8, 8);
			for(int i = 0; i < entitylist.size(); i++){
				if(entitylist.get(i) instanceof LivingEntity){
					LivingEntity en = (LivingEntity) entitylist.get(i);
					en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 250, 0));
					WarpEntityEffect effect = new WarpEntityEffect(effectmanager, e.getPlayer());
					effect.particle = ParticleEffect.ANGRY_VILLAGER;
					effect.start();
				}
			}
			e.setCancelled(true);
			e.getPlayer().updateInventory();
		}
		
		if(e.getPlayer().getItemInHand().getItemMeta().equals(items.m_parchemin_4)){
			e.setCancelled(true);
			e.getPlayer().updateInventory();
			final Location loc = e.getPlayer().getLocation();
			final int id = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
				int radius = 3;
				
				@Override
				public void run() {
					createCyl(loc, radius, Material.FIRE);
					createCyl(loc, radius-1, Material.AIR);
					radius ++;
				}
			}, 0L, 10L).getTaskId();
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {
				
				@Override
				public void run() {
					createCyl(loc, 3 + 5, Material.AIR);
					Bukkit.getScheduler().cancelTask(id);
				}
			}, 50L);
		    }
		if(e.getPlayer().getItemInHand().getItemMeta().equals(items.m_parchemin_5)){
			e.setCancelled(true);;
			e.getPlayer().updateInventory();
			List<Entity> entitylist = e.getPlayer().getNearbyEntities(5, 5, 5);
			for(int i = 0; i < entitylist.size(); i++){
					entitylist.get(i).setVelocity(new Vector(0.0, 1.0, 0.0));
					Location loc = e.getPlayer().getLocation();
			        e.getPlayer().getWorld().playEffect(loc, Effect.STEP_SOUND, Material.DIRT);
			}
		}
		if(e.getPlayer().getItemInHand().getItemMeta().equals(items.m_parchemin_6)){
			e.setCancelled(true);;
			e.getPlayer().updateInventory();
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 2));
			InvisibilityEffect effect = new InvisibilityEffect(effectmanager, e.getPlayer().getLocation());
			effect.visibleRange = 10;
			effect.start();
		}
		if(e.getPlayer().getItemInHand().getItemMeta().equals(items.m_parchemin_7)){
			e.getPlayer().setHealth(20);
			e.getPlayer().setFoodLevel(20);
			ParticleEffect.HEART.display(e.getPlayer().getLocation().add(0.0, 2.0, 0.0), 10);
			ParticleEffect.HEART.display(e.getPlayer().getLocation().add(1.0, 0.0, 1.0), 10);
			ParticleEffect.HEART.display(e.getPlayer().getLocation().add(0.0, 0.0, 1.0), 10);
			ParticleEffect.HEART.display(e.getPlayer().getLocation().add(1.0, 0.0, 0.0), 10);

			ParticleEffect.HEART.display(e.getPlayer().getLocation().subtract(1.0, 0.0, 1.0), 10);
			ParticleEffect.HEART.display(e.getPlayer().getLocation().subtract(0.0, 0.0, 1.0), 10);
			ParticleEffect.HEART.display(e.getPlayer().getLocation().subtract(1.0, 0.0, 0.0), 10);
			
			ParticleEffect.HEART.display(e.getPlayer().getLocation().add(0.0, 2.0, 0.0), 10);
			ParticleEffect.HEART.display(e.getPlayer().getLocation().add(1.0, 1.0, 1.0), 10);
			ParticleEffect.HEART.display(e.getPlayer().getLocation().add(0.0, 1.0, 1.0), 10);
			ParticleEffect.HEART.display(e.getPlayer().getLocation().add(1.0, 1.0, 0.0), 10);

			ParticleEffect.HEART.display(e.getPlayer().getLocation().subtract(1.0, -1.0, 1.0), 10);
			ParticleEffect.HEART.display(e.getPlayer().getLocation().subtract(0.0, -1.0, 1.0), 10);
			ParticleEffect.HEART.display(e.getPlayer().getLocation().subtract(1.0, -1.0, 0.0), 10);
			
			e.setCancelled(true);
			e.getPlayer().updateInventory();
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDamageEntity(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Arrow){
			Arrow a = (Arrow) e.getDamager();
			if(a.getShooter() instanceof Player){
				Player p = (Player) a.getShooter();
				if(p.getItemInHand().equals(items.arbalete)){
					e.setDamage(8);
				}
			}
		}
		if(e.getDamager() instanceof Fireball){
			Fireball a = (Fireball) e.getDamager();
			if(a.getShooter() instanceof Player){
				Player p = (Player) a.getShooter();
				if(p.getItemInHand().equals(items.m_parchemin_2)){
					if(p != e.getEntity()){
						e.setDamage(6);
					}
				}
			}
		}
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
	public void onPlayerDrop(PlayerDropItemEvent e){
		if(e.getPlayer().isOp()){
			return;
		}
		e.setCancelled(true);
		e.getPlayer().updateInventory();
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
			saveConfig();
			p.teleport(location);
			Objective race = board.registerNewObjective("Tu es un " + getRace(p), "dummy");
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
			saveConfig();
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
			saveConfig();
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
			saveConfig();
			Location location3 = new Location(Bukkit.getWorld(w3), x3, y3, z3, yaw3, pitch3);
			p.teleport(location3);
			break;
		default:
			break;
		}
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
	
	//NO Exp ORBS DROP
	
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
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		Objective obj = board.registerNewObjective(p.getName(), "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		Score espace = obj.getScore(Bukkit.getOfflinePlayer("----------------"));
		espace.setScore(7);
		
		Score race = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Race: " + getRace(p)));
		race.setScore(6);
		
		Score level = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.AQUA + "Level:  " + getLevel(p)));
		level.setScore(5);
		
		Score exp = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "Experience:  " + getExp(p)));
		exp.setScore(4);
		
		Score argent = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Argent:  " + getMoney(p)));
		argent.setScore(3);
		
		Score espace1 = obj.getScore(Bukkit.getOfflinePlayer(" "));
		espace1.setScore(2);
		
		Score queste = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Quête: "));
		queste.setScore(1);
		
		if (Quester.qMan.getPlayerQuest(p.getName()).getName().length() > 16){
			String questname = Quester.qMan.getPlayerQuest(p.getName()).getName().substring(0, 16);
			Score quest = obj.getScore(Bukkit.getOfflinePlayer(questname));
			quest.setScore(0);
		}
		else{
		Score quest = obj.getScore(Bukkit.getOfflinePlayer(Quester.qMan.getPlayerQuest(p.getName()).getName()));
		quest.setScore(0);
		}
		p.setScoreboard(board);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		Player p = e.getPlayer();
		board.getObjective(p.getName()).unregister();
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
