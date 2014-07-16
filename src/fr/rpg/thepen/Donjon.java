package fr.rpg.thepen;

import java.util.ArrayList;
import org.bukkit.Location;

public class Donjon {

	String name;
	Location location;
	Door enter;
	ArrayList<Room> rooms = new ArrayList<Room>();
	boolean open = true;
	boolean active = false;
	
	public Donjon(final Location loc, final String nom, Door door, boolean active){
		enter = door;
		name = nom;
		location = loc;
	}
	
	//Setters
	
	public void addRoom(Room room){
		rooms.add(room);
	}
	
	public void setName(String newname){
		name = newname;
	}
	
	//Getters
	
	public Room getRoomByDoor(Door door){
		for(int i = 0; i < getRooms().size(); i++){
			if(getRooms().get(i).getDoors().contains(door)){
				return getRooms().get(i);
			}
		}
		return null;
	}
	public Door getEnterDoor(){
		return enter;
	}
	
	public Location getLocation(){
		return location;
	}
	
	public String getName(){
		return name;
	}
	
	public ArrayList<Room> getRooms(){
		return rooms;
	}
	
	public Room getRoomByName(String name){
		if(rooms.size() > 0){
			for(int i = 0; i < rooms.size(); i++){
				Room r = rooms.get(i);
				if(r.getName().equals(name)){
					return r;
				}
			}
		return null;
		}
		else {
			System.out.println("Il n y a pas de salles");
		}

		return null;
	}
	
	public ArrayList<Door> getDoors(){
		ArrayList<Door> doors= new ArrayList<Door>();
		for(int i = 0; i < getRooms().size(); i++){
			Room room = getRooms().get(i);
			for(int i1 = 0; i1 < room.getDoors().size(); i1++){
				Door d = room.getDoors().get(i1);
				doors.add(d);
			}
		}
		return doors;
	}
	
	//Actions
	
	public void setOpen(){
		enter.setOpen();
		open = true;
	}
	
	public void setClose(){
			enter.setClose();
			open = false;
	}
	
	public void setActive(){
		active = true;
	}
	//Conditions
	
	public boolean isOpen(){
		return open;
	}
	
	public boolean isActive(){
		return active;
	}
	
}
