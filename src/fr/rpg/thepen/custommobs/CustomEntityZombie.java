package fr.rpg.thepen.custommobs;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import net.minecraft.server.v1_7_R3.EntityZombie;
import net.minecraft.server.v1_7_R3.GenericAttributes;
import net.minecraft.server.v1_7_R3.World;

public class CustomEntityZombie extends EntityZombie {
	private double speed = 0.699999988079071D, maxHealth = 20D, knockbackRes = 0D, followRange = 32D, damage = 2D;
	public CustomEntityZombie(World w, String s){
		this(w);
		this.setCustomName(s);
		this.setCustomNameVisible(false);//<-- Rendre le nom visible
	}
	@Override
	protected void aC() {
	    super.aC();
	    this.getAttributeInstance(GenericAttributes.a).setValue(this.maxHealth);
	    this.getAttributeInstance(GenericAttributes.b).setValue(followRange);
	    this.getAttributeInstance(GenericAttributes.c).setValue(knockbackRes);
	    this.getAttributeInstance(GenericAttributes.d).setValue(speed);
	    this.getAttributeInstance(GenericAttributes.e).setValue(damage);
	}
    public CustomEntityZombie(World world){
        super(world);
    }
    /**
     * Faire spawner un zombie sans nom sur la position
     * @param pos La position
     * @return Le zombie
     */
    public static CustomEntityZombie spawnZombie(Location pos){
    	return CustomEntityZombie.spawnZombie(pos, "");
    }
    /**
     * Faire spawner un zombie sur une position avec un nom défini
     * @param pos La position
     * @param name Le nom du zombie
     * @return Le zombie
     */
    public static CustomEntityZombie spawnZombie(Location pos, String name){
    	return CustomEntityZombie.spawnZombie(((CraftWorld)pos.getWorld()).getHandle(), pos.getX(), pos.getY(), pos.getZ(), pos.getYaw(), pos.getPitch(), name);
    }
    /**
     * Faire spawner un zombie sur une position absolue avec un nom défini
     * @param nmsWorld Le monde, sous forme NMS
     * @param x Position X
     * @param y Y
     * @param z Z
     * @param yaw Le yaw en degres
     * @param pitch Le pitch
     * @param name Le nom
     * @return Le zombie
     */
    public static CustomEntityZombie spawnZombie(World nmsWorld, double x, double y, double z, float yaw, float pitch, String name){
    	CustomEntityZombie cez = new CustomEntityZombie(nmsWorld, name);
    	cez.setLocation(x, y, z, yaw, pitch);
    	nmsWorld.addEntity(cez, SpawnReason.CUSTOM);
    	return cez;
    }
    /**
     * Definir la vie maximale du zombie
     * @param health La nouvelle vie(double)
     */
    public void setMaxHealth(double health){
    	this.maxHealth = health;
    }
    /**
     * Definir la vie du zombie
     * @param health Sa vie
     */
    public void setHealth(double health){
    	this.setHealth(health);
    }
    /**
     * Soigner le zombie
     */
    public void heal(){
    	this.setHealth(this.getMaxHealth());
    }
    /**
     * Definir la vitesse du zombie
     * @param speed La nouvelle vitesse
     */
    public void setSpeed(double speed){
    	this.speed = speed;
    }
    /**
     * Definir la resistance au knockback du zombie
     * @param res Sa resistance, + elevee = + resistant
     */
    public void setKnockbackResistance(double res){
    	this.knockbackRes = res;
    }
    /**
     * Definir la distance en blocs sur laquelle le zombie suivra ses cibles
     * @param range Le distance en blocs
     */
    public void setFollowRange(double range){
    	this.followRange = range;
    }
    /**
     * Definir les degats en demi-coeurs qu'inflige le zombie
     * @param damage Les degats
     */
    public void setDamage(double damage){
    	this.damage = damage;
    }
    /**
     * Mettre les valeurs(vitesse, vie max, etc.) a jour
     */
    public void updateValues(){
    	this.aC();
    }
}