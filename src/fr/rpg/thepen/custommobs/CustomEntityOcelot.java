package fr.rpg.thepen.custommobs;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import net.minecraft.server.v1_7_R1.EntityCow;
import net.minecraft.server.v1_7_R1.GenericAttributes;
import net.minecraft.server.v1_7_R1.World;

public class CustomEntityOcelot extends EntityCow{
	
	private double speed = 0.7, maxHealth = 20D, knockbackRes = 0D, followRange = 32D, strength = 2D;
	public CustomEntityOcelot(World w, String s){
		this(w);
		this.setCustomName(s);
		this.setCustomNameVisible(false);//<-- Rendre le nom visible
	}
	@Override
	protected void aD() {
	    super.aD();
	    this.getAttributeInstance(GenericAttributes.a).setValue(this.maxHealth);
	    this.getAttributeInstance(GenericAttributes.b).setValue(followRange);
	    this.getAttributeInstance(GenericAttributes.c).setValue(knockbackRes);
	    this.getAttributeInstance(GenericAttributes.d).setValue(speed);
	    this.getAttributeInstance(GenericAttributes.e).setValue(strength);
	}
    public CustomEntityOcelot(World world){
        super(world);
    }
    
    public static CustomEntityOcelot spawn(Location pos){
    	return CustomEntityOcelot.spawn(pos, "");
    }

    public static CustomEntityOcelot spawn(Location pos, String name){
    	return CustomEntityOcelot.spawn(((CraftWorld)pos.getWorld()).getHandle(), pos.getX(), pos.getY(), pos.getZ(), pos.getYaw(), pos.getPitch(), name);
    }
    
    public static CustomEntityOcelot spawn(World nmsWorld, double x, double y, double z, float yaw, float pitch, String name){
    	CustomEntityOcelot cez = new CustomEntityOcelot(nmsWorld, name);
    	cez.setLocation(x, y, z, yaw, pitch);
    	nmsWorld.addEntity(cez, SpawnReason.CUSTOM);
    	return cez;
    }
    
    public void setMaxHealth(double health){
    	this.maxHealth = health;
    }
    
    public void setHealth(double health){
    	this.setHealth(health);
    }
   
    public void heal(){
    	this.setHealth(this.getMaxHealth());
    }
    
    public void setSpeed(double speed){
    	this.speed = speed;
    }
    
    public void setKnockbackResistance(double res){
    	this.knockbackRes = res;
    }
    
    public void setFollowRange(double range){
    	this.followRange = range;
    }
   
    public void setDamage(double damage){
    	this.strength = damage;
    }
    
    public void updateValues(){
    	this.aD();
    }
}
