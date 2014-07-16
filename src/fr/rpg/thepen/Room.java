package fr.rpg.thepen;

import java.util.ArrayList;
import org.bukkit.Location;

public class Room {

	Location center;
	RoomType roomtype;
	ArrayList<Door> doors;
	String roomname;
	public Room(final String name, final ArrayList<Door> doorslist, final Location centre, final RoomType type){
		doors = doorslist;
		center = centre;
		roomtype = type;
		roomname = name;
	}
	
	//Getters
	
	public Location getLocation(){
		return center;
	}
	
	public ArrayList<Door> getDoors(){
		return doors;
	}
	
	public String getName(){
		return roomname;
	}
	
	public RoomType getType(){
		return roomtype;
	}
	
	public void setClosed(){
		for(int i = 0; i < doors.size(); i++){
			Door door = doors.get(i);
			door.setClose();
		}
	}
}
