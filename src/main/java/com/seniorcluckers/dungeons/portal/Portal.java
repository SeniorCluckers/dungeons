package com.seniorcluckers.dungeons.portal;

import com.seniorcluckers.dungeons.utils.Cuboid;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Portal implements ConfigurationSerializable {

    private final int ID;
    private Cuboid cuboid;
    private String destination;

    public Portal(int ID, Location pos1, Location pos2) {
        this.ID = ID;
        this.cuboid = new Cuboid(pos1, pos2);
    }

    public int getID() {
        return ID;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isPlayerInPortal(Player player) {
        return cuboid.isIn(player);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("ID", ID);
        data.put("destination", destination);
        data.put("pos1", cuboid.getPoint1().serialize());
        data.put("pos2", cuboid.getPoint2().serialize());

        return data;
    }
}
