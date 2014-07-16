package fr.rpg.thepen.custommobs;

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