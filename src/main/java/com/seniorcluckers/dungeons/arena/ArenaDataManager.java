package com.seniorcluckers.dungeons.arena;

import com.seniorcluckers.dungeons.Dungeons;
import com.seniorcluckers.dungeons.arena.models.Arena;
import com.seniorcluckers.dungeons.utils.ChatUtil;
import com.seniorcluckers.dungeons.utils.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class ArenaDataManager extends DataManager {

    private final Dungeons plugin;

    public ArenaDataManager(Dungeons plugin) {
        super(plugin, "dungeons.yml");
        this.plugin = plugin;
    }

    public void saveArena(Arena arena) {
        getConfig().set("dungeons." + arena.getID(), arena.serialize());
        saveConfig();
        reloadConfig();
    }

    public void deleteArena(Arena arena) {
        getConfig().set("dungeons." + arena.getID(), null);
        saveConfig();
        reloadConfig();
    }

    public void deleteArena(int ID) {
        getConfig().set("dungeons." + ID, null);
        saveConfig();
        reloadConfig();
    }

    public List<String> getArenaList() {
        ConfigurationSection dungeons = getConfig().getConfigurationSection("dungeons");
        if (dungeons == null) {
            return null;
        }

        List<String> arenas = new ArrayList<>();
        for (String key : dungeons.getKeys(false)) {
            arenas.add(key);
        }

        return arenas;
    }

    public boolean hasArena(int ID) {
        ConfigurationSection dungeons = getConfig().getConfigurationSection("dungeons." + ID);
        if (dungeons == null) {
            return false;
        }
       return true;
    }

    public String getArenaMap(int ID) {
        ConfigurationSection dungeons = getConfig().getConfigurationSection("dungeons." + ID);
        if (dungeons == null) {
            return null;
        }
        return dungeons.getString(".map");
    }

    public List<String> getArenaInfo(int ID) {
        ConfigurationSection dungeons = getConfig().getConfigurationSection("dungeons." + ID);
        if (dungeons == null) {
            return null;
        }

        List<String> arenaInfo = new ArrayList<>();
        arenaInfo.add(ChatUtil.addColor("&bID&7:&e " + dungeons.getString("ID")));
        arenaInfo.add(ChatUtil.addColor("&bDisplay Name&7:&e " + dungeons.getString("displayName")));
        arenaInfo.add(ChatUtil.addColor("&bMap&7:&e " + dungeons.getString("map")));

        String enterSpawn = getLocationFormatted(dungeons.getConfigurationSection("enterSpawn"));
        if (enterSpawn != null) {
            arenaInfo.add(ChatUtil.addColor("&bEnter Spawn&7:&e\n" + enterSpawn));
        } else {
            arenaInfo.add(ChatUtil.addColor("&bEnter Spawn&7: &c[not-set]"));
        }

        String exitSpawn = getLocationFormatted(dungeons.getConfigurationSection("exitSpawn"));
        if (exitSpawn != null) {
            arenaInfo.add(ChatUtil.addColor("&bExit Spawn&7:&e\n" + exitSpawn));
        } else {
            arenaInfo.add(ChatUtil.addColor("&bExit Spawn&7: &c[not-set]"));
        }

        String exitPortalPos1 = getLocationFormatted(dungeons.getConfigurationSection("exitPortal.pos1"));
        String exitPortalPos2 = getLocationFormatted(dungeons.getConfigurationSection("exitPortal.pos2"));
        if (exitPortalPos1 != null && exitPortalPos2 != null) {
            arenaInfo.add(ChatUtil.addColor("&bExit Portal&7:&e " + exitPortalPos1 + " -> " + exitPortalPos2));
        } else {
            arenaInfo.add(ChatUtil.addColor("&bExit Portal&7: &c[not-set]"));
        }

        return arenaInfo;
    }

//    //TODO We need to use this to validate a location make sure it has a world, x, y, z, pitch, yaw
//    private boolean isLocationValid(ConfigurationSection section) {
//
//        if (section == null || !section.contains("world") || !section.contains("x") || !section.contains("y") || !section.contains("z")
//                || !section.contains("yaw") || !section.contains("pitch")) {
//            return false;
//        }
//
//        String world = section.getString("world");
//        double x = section.getDouble("x");
//        double y = section.getDouble("y");
//        double z = section.getDouble("z");
//        float yaw = (float) section.getDouble("yaw");
//        float pitch = (float) section.getDouble("pitch");
//
//        Location temp = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
//        if (temp == null) {
//            return false;
//        }
//
//        return true;
//    }

    public boolean isArenaSetUp(int ID) {
        ConfigurationSection dungeon = getConfig().getConfigurationSection("dungeons." + ID);
        if (dungeon == null) {
            return false;
        }

        ConfigurationSection enterSpawn = dungeon.getConfigurationSection("enterSpawn");
        if (enterSpawn == null || !enterSpawn.contains("world") || !enterSpawn.contains("x") || !enterSpawn.contains("y") || !enterSpawn.contains("z")
                || !enterSpawn.contains("yaw") || !enterSpawn.contains("pitch") || !enterSpawn.getString("world").equalsIgnoreCase(dungeon.getString("map"))) {
            return false;
        }

        ConfigurationSection exitSpawn = dungeon.getConfigurationSection("exitSpawn");
        if (exitSpawn == null || !exitSpawn.contains("world") || !exitSpawn.contains("x") || !exitSpawn.contains("y") || !exitSpawn.contains("z")
                || !exitSpawn.contains("yaw") || !exitSpawn.contains("pitch")) {
            return false;
        }

        ConfigurationSection exitPortalPos1 = dungeon.getConfigurationSection("exitPortal.pos1");
        if (exitPortalPos1 == null || !exitPortalPos1.contains("world") || !exitPortalPos1.contains("x") || !exitPortalPos1.contains("y") || !exitPortalPos1.contains("z")
                || !exitPortalPos1.contains("yaw") || !exitPortalPos1.contains("pitch") || !exitPortalPos1.getString("world").equalsIgnoreCase(dungeon.getString("map"))) {
            return false;
        }

        ConfigurationSection exitPortalPos2 = dungeon.getConfigurationSection("exitPortal.pos2");
        if (exitPortalPos2 == null || !exitPortalPos2.contains("world") || !exitPortalPos2.contains("x") || !exitPortalPos2.contains("y") || !exitPortalPos2.contains("z")
                || !exitPortalPos2.contains("yaw") || !exitPortalPos2.contains("pitch") || !exitPortalPos2.getString("world").equalsIgnoreCase(dungeon.getString("map"))) {
            return false;
        }

        return true;
    }

    private String getLocationFormatted(ConfigurationSection section) {

        if (section == null || !section.contains("world") || !section.contains("x") || !section.contains("y") || !section.contains("z")
                || !section.contains("yaw") || !section.contains("pitch")) {
            return null;
        }

        String world = section.getString("world");
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        float yaw = (float) section.getDouble("yaw");
        float pitch = (float) section.getDouble("pitch");

        Location temp = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        if (temp == null) {
            return null;
        }

        return "(" + world + ":" + x + "," + y +
                "," + z + "," + yaw + "," + pitch + ")";
    }

    public boolean saveArena(int ID, String path, Object object) {
        ConfigurationSection dungeons = getConfig().getConfigurationSection("dungeons." + ID);
        if (dungeons == null) {
            return false;
        }
        dungeons.set(path, object);
        saveConfig();
        reloadConfig();
        return true;

    }
}
