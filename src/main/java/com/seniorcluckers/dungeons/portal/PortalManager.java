package com.seniorcluckers.dungeons.portal;

import com.seniorcluckers.dungeons.Dungeons;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class PortalManager {

    private final Dungeons plugin;

    private final PortalDataManager portalDataManager;

    private List<Portal> portals = new ArrayList<>();
    private int portalSize = 0;

    public PortalManager(Dungeons plugin) {
        this.plugin = plugin;
        this.portalDataManager = new PortalDataManager(plugin);
        loadPortals();
    }

    public void addPortal(Location pos1, Location pos2, String destination) {
        Portal portal = new Portal(++portalSize, pos1, pos2);
        portal.setDestination(destination);
        portals.add(portal);
        portalDataManager.savePortal(portal);
    }

    public void addPortal(int ID, Location pos1, Location pos2, String destination) {
        if (ID >= portalSize) {
            portalSize = ID;
        }
        Portal portal = new Portal(ID, pos1, pos2);
        portal.setDestination(destination);
        portals.add(portal);
    }

    public void removePortal(Portal portal) {
        portals.remove(portal);
        portalDataManager.deletePortal(portal);
    }

    public Portal getPortal(int ID) {
        for (Portal portal : portals) {
            if (portal.getID() == ID) {
                return portal;
            }
        }
        return null;
    }

    private void loadPortals() {
        ConfigurationSection section = portalDataManager.getConfig().getConfigurationSection("portals");
        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            Map<String, Object> temp = section.getConfigurationSection(key + ".pos1").getValues(true);
            Map<String, Object> temp2 = section.getConfigurationSection(key + ".pos2").getValues(true);

//            Bukkit.getLogger().log(Level.SEVERE, temp.toString());

            Location pos1 = Location.deserialize(temp);
            Location pos2 = Location.deserialize(temp2);
            addPortal(Integer.parseInt(key), pos1, pos2, section.getString(key + ".destination"));
        }
    }

    /**
     * Check if the portal destination points to a dungeon that exists.
     * @param portal
     * @return
     */
    public boolean isPortalDestinationValid(Portal portal) {
        if (!plugin.getArenaManager().getArenaDataManager().hasArena(Integer.parseInt(portal.getDestination()))) {
            return false;
        }
        return true;
    }

    public Portal findPortal(Player player) {
        for (Portal portal : portals) {
            if (portal.isPlayerInPortal(player)) {
                return portal;
            }
        }
        return null;
    }

    public List<Portal> getPortals() {
        return portals;
    }
}
