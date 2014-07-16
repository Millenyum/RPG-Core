package fr.rpg.thepen;


import org.bukkit.Location;
import org.bukkit.Material;

public class BlockInfo {

	Location location;
	Material material;
	byte datavalue;
	
	public BlockInfo(Location loc, Material mat, byte data){
		location = loc;
		material = mat;
		datavalue = data;
	}
	
	public Material getType(){
		return material;
	}
	
	public Location getLocation(){
		return location;
	}
	
	public byte getDataValue(){
		return datavalue;
	}
}
