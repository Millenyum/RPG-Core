package fr.rpg.thepen.custommobs;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class CustomEntityPattern {

	double maxHealth;
	double strength;
	double speed;
	double knockbackResistance;
	double range;
	ItemStack helmet;
	ItemStack chestplate;
	ItemStack leggings;
	ItemStack boots;
	ItemStack hand;
	List<ItemStack> loots;
	
	public CustomEntityPattern(double healthvalue, double strengthvalue, double speedvalue, double knockbackRes, double rangevalue){
		maxHealth = healthvalue;
		strength = strengthvalue;
		speed = speedvalue;
		knockbackResistance = knockbackRes;
		range = rangevalue;
	}
}
