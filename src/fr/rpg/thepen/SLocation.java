package fr.rpg.thepen;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("Location")
public class SLocation extends Location implements ConfigurationSerializable {
    static {
        ConfigurationSerialization.registerClass(SLocation.class, "Location");
    }
 
    public SLocation(final Location loc) {
        super(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }
 
    public SLocation(final Map<String, Object> map) {
        super(Bukkit.getWorld((String) map.get("world")), (double) map.get("x"), (double) map.get("y"), (double) map.get("z"),
                (float) (double) map.get("yaw"), (float) (double) map.get("pitch"));
    }
 
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("world", getWorld().getName());
        map.put("x", getX());
        map.put("y", getY());
        map.put("z", getZ());
        map.put("yaw", getYaw());
        map.put("pitch", getPitch());
        return map;
    }
} 