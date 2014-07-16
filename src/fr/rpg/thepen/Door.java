package fr.rpg.thepen;

import java.awt.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class Door {

	DoorType doortype;
	Location corner1;
	Location corner2;
	Location serrure;
	boolean open;
	Material material;
	byte data;
	
	public Door(final DoorType type, final Location loc1, final Location loc2, boolean ouvert){
		doortype = type;
		corner1 = loc1;
		corner2 = loc2;
		open = ouvert;
	}
	
	public Door(final DoorType type, final Location loc1, final Location loc2, Location lock, boolean ouvert){
		doortype = type;
		corner1 = loc1;
		corner2 = loc2;
		serrure = lock;
		open = ouvert;
	}
	
	public Door(final DoorType type, final Location loc1, final Location loc2, Location lock, boolean ouvert, Material mat, byte datavalue){
		doortype = type;
		corner1 = loc1;
		corner2 = loc2;
		serrure = lock;
		open = ouvert;
		data = datavalue;
		material = mat;
	}
	
	//Actions
	public void setClose(){
		for(int x = (int) Math.min(corner1.getX(), corner2.getX()); x <= (int)Math.max(corner1.getX(), corner2.getX()); x++){
			for(int y = (int) Math.min(corner1.getY(), corner2.getY()); y <= (int)Math.max(corner1.getY(), corner2.getY()); y++){
				for(int z = (int) Math.min(corner1.getZ(), corner2.getZ()); z <= (int)Math.max(corner1.getZ(), corner2.getZ()); z++){
					Location loc = new Location(corner1.getWorld(), x, y, z);
					if(getType() == DoorType.KEY || getType() == DoorType.FIGHTROOMKEY){
						if(loc.equals(getLock())){
							System.out.println("Serrure !" + loc.toString());
							corner1.getWorld().getBlockAt(loc).setType(Material.IRON_BLOCK);
						}
						else{
							System.out.println("Porte " + loc.toString());
							
							corner1.getWorld().getBlockAt(loc).setType(Material.PISTON_BASE);
							corner1.getWorld().getBlockAt(loc).setData((byte) 14);
						}
					}
					else if(getType() == DoorType.BOSS){
						if(loc.equals(getLock())){
							corner1.getWorld().getBlockAt(loc).setType(Material.GOLD_BLOCK);
						}
						else{
							corner1.getWorld().getBlockAt(loc).setType(Material.PISTON_BASE);
							corner1.getWorld().getBlockAt(loc).setData((byte) 14);
						}
					}
					else{
					corner1.getWorld().getBlockAt(loc).setType(Material.PISTON_BASE);
					corner1.getWorld().getBlockAt(loc).setData((byte) 14);
					}
				}
			}
		}
	}
	
	public void setOpen(){
		for(double x = Math.min(corner1.getX(), corner2.getX()); x <= Math.max(corner1.getX(), corner2.getX()); x++){
			for(double y = Math.min(corner1.getY(), corner2.getY()); y <= Math.max(corner1.getY(), corner2.getY()); y++){
				for(double z = Math.min(corner1.getZ(), corner2.getZ()); z <= Math.max(corner1.getZ(), corner2.getZ()); z++){
					Location loc = new Location(corner1.getWorld(), x, y, z);
					corner1.getWorld().getBlockAt(loc).setType(Material.AIR);
				}
			}
		}
	}
	
	//Getters
	
	public DoorType getType(){
		return doortype;
	}
	
	public Location getLock(){
		int x = (int) serrure.getX();
		int y = (int) serrure.getY();
		int z = (int) serrure.getZ();
		
		Location loc = new Location(serrure.getWorld(), x, y, z);
		return loc;
	}
	
	public Location getCorner1(){
		return corner1;
	}
	
	public Location getCorner2(){
		return corner2;
	}
	//Conditions
	
	public boolean isOpenDefault(){
		return open;
	}
	
	//Setters
	
	public void setLock(Location lock){
		serrure = lock;
	}
	
	
}

