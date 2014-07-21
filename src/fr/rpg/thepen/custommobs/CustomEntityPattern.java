package fr.rpg.thepen.custommobs;

import java.util.List;

import net.minecraft.server.v1_7_R1.Item;
import net.minecraft.server.v1_7_R1.World;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;

public class CustomEntityPattern {

	public static String name = null;
	double maxHealth = 20;
	double strength = 2;
	double speed = 0.7D;
	double knockbackResistance = 0.0;
	double range = 32;
	ItemStack helmet;
	ItemStack chestplate;
	ItemStack leggings;
	ItemStack boots;
	ItemStack hand;
	List<ItemStack> loots;
	CustomMobType type;
	ZombieType zombietype;
	HorseType horsetype;
	Color horsecolor;
	Style horsestyle;
	int creeperpower = 3;
	int creeperfuseticks = 30;
	boolean creeperpowered = false;
	int slimesize = 1;
	
	public CustomEntityPattern(String nom, CustomMobType mobtype){
		name = nom;
		type = mobtype;
	}
	
	public void spawn(Location loc){
		switch(type){
		case ZOMBIE:
			CustomEntityZombie en = new CustomEntityZombie((((CraftWorld)loc.getWorld()).getHandle()));
			en.setMaxHealth(maxHealth);
			en.setCustomName(name);
			en.setSpeed(speed);
			en.setDamage(strength);
			en.setKnockbackResistance(knockbackResistance);
			en.setFollowRange(range);
			if(hand != null){
				en.setEquipment(0, CraftItemStack.asNMSCopy(hand));
			}
			if(helmet != null){
				en.setEquipment(1, CraftItemStack.asNMSCopy(helmet));
			}
			if(chestplate != null){
				en.setEquipment(2, CraftItemStack.asNMSCopy(chestplate));
			}
			if(leggings != null){
				en.setEquipment(3, CraftItemStack.asNMSCopy(leggings));
			}
			if(boots != null){
				en.setEquipment(4, CraftItemStack.asNMSCopy(boots));
			}
			if(zombietype != null){
				if(zombietype == ZombieType.BABY){
					en.setBaby(true);
				}
				if(zombietype == ZombieType.BABYVILLAGER){
					en.setVillager(true);
					en.setBaby(true);
				}
				if(zombietype == ZombieType.VILLAGER){
					en.setVillager(true);
				}
			}
			en.spawn(loc);
			
			break;
		case BAT:
			break;
		case BLAZE:
			break;
		case CAVESPIDER:
			break;
		case COW:
			break;
		case CREEPER:
			break;
		case HORSE:
			break;
		case MAGMACUBE:
			break;
		case OCELOT:
			break;
		case PIGMAN:
			break;
		case SILVERFISH:
			break;
		case SKELETON:
			break;
		case SKELETONHORSE:
			break;
		case SLIME:
			break;
		case SPIDER:
			break;
		case SQUID:
			break;
		case WITCH:
			break;
		case WOLF:
			break;
		case ZOMBIEHORSE:
			break;
		default:
			break;
			
		}
	}
	
	//Getters
	
	public String getName(){
		return name;
	}
	
	public CustomMobType getType(){
		return type;
	}
	//Setters
	
	public boolean setZombieType(ZombieType ztype){
		if(type.equals(CustomMobType.ZOMBIE)){
			zombietype  = ztype;
			return true;
		}
		return false;
	}
	
	public boolean setHorseType(HorseType htype){
		if(type.equals(CustomMobType.SKELETONHORSE) || type.equals(CustomMobType.ZOMBIEHORSE)){
			horsetype = htype;
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean setHorseColor(Color color){
		if(type.equals(CustomMobType.HORSE)){
			horsecolor = color;
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean setHorseStyle(Style style){
		if(type.equals(CustomMobType.HORSE)){
			horsestyle = style;
			return true;
		}
		else{
			return false;
		}
	}
	
	public void setHealth(int health){
		maxHealth = health;
	}
	
	public void setStrength(int mobstrength){
		strength = mobstrength;
	}
	public void setSpeed(int mobspeed){
		speed = mobspeed;
	}
	public void setRange(int visionrange){
		range = visionrange;
	}
	public void setHelmet(ItemStack item){
		helmet = item;
	}
	public void setChestplate(ItemStack item){
		chestplate = item;
	}
	public void setLeggings(ItemStack item){
		leggings = item;
	}
	public void setBoots(ItemStack item){
		boots = item;
	}
	public void setItemInHand(ItemStack item){
		hand = item;
	}
}
