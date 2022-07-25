package com.seniorcluckers.dungeons.map;

import com.seniorcluckers.dungeons.Dungeons;
import com.seniorcluckers.dungeons.utils.ChatUtil;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class MapManager {

    private final Dungeons plugin;

    private final String path = new File(".").getAbsolutePath() + "/dungeons_maps";
    private File mapDirectory;

    private List<String> maps = new ArrayList<>();

    private Map<String, String> dungeonMaps = new HashMap<>();

    public MapManager(Dungeons plugin) {
        this.plugin = plugin;

        saveDefaultFolder();
        findMaps();
    }

    public List<String> getMaps() {
        return maps;
    }

    public void findMaps() {
        maps.clear();
        for (File folder : mapDirectory.listFiles()) {
            if (folder.isDirectory()) {
                for (File levelFile : folder.listFiles()) {
                    if (levelFile.getName().equalsIgnoreCase("level.dat")) {
                        maps.add(folder.getName());
                    }
                }
            }
        }
    }

    public void copyMap(String map, Player player) {
        if (!hasMap(map)) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    FileUtils.copyDirectory(new File(path + "/" + map),new File(Bukkit.getWorldContainer() + "/" + map));

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.getLogger().log(Level.SEVERE, "Map has been successfully copied!");
                            if (loadMap(map)) {
                                if (player.isOnline()) {
                                    player.sendMessage(ChatUtil.addColor("&eMap has been successfully loaded."));
                                }
                            }
                        }
                    }.runTask(plugin);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void copyMap(int ID) {
        if (!hasMap(plugin.getArenaManager().getArenaDataManager().getArenaMap(ID))) {
//            Bukkit.getLogger().log(Level.SEVERE, "Map trying to load:" + plugin.getArenaManager().getArenaDataManager().getArenaMap(ID));

            Bukkit.getLogger().log(Level.SEVERE, "Original Map exists!");
            return;
        } else if (dungeonMaps.containsKey(String.valueOf(ID))) {
            Bukkit.getLogger().log(Level.SEVERE, "Dungeon map exists!");
            return;
        }

        Bukkit.getLogger().log(Level.SEVERE, "Map is about to be copied...");

        String map = plugin.getArenaManager().getArenaDataManager().getArenaMap(ID) + "_" + ID;
        dungeonMaps.put(String.valueOf(ID), map);

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    FileUtils.copyDirectory(new File(path + "/" + plugin.getArenaManager().getArenaDataManager().getArenaMap(ID)), new File(Bukkit.getWorldContainer() + "/" + map));

                    new BukkitRunnable() {
                        @Override
                        public void run() {
//                            Bukkit.getLogger().log(Level.SEVERE, "Map has been successfully copied!");
                            if (loadMap(map)) {
                                plugin.getArenaManager().loadArena(ID);
                            }
                        }
                    }.runTask(plugin);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public boolean loadMap(String map) {
        World world = Bukkit.createWorld(new WorldCreator(map));
        world.setKeepSpawnInMemory(false);
        world.setAutoSave(false);
//        Bukkit.getLogger().log(Level.SEVERE, "Map has been successfully loaded!");
        return true;
    }

    public boolean unloadMap(String map) {
        if (!hasMap(map)) {
            return false;
        }
        World world = Bukkit.getWorld(map);
        if (world != null) {
            for (Chunk chunk : world.getLoadedChunks()) {
                chunk.unload();
            }
            if (Bukkit.unloadWorld(world, false)) {
                deleteMap(world.getWorldFolder());
            }
            return true;
        }
        return false;
    }

    public boolean unloadMap(String ID, String map) {
        if (!dungeonMaps.containsValue(map)) {
            return false;
        }

        World world = Bukkit.getWorld(map);
        if (world != null) {
            for (Chunk chunk : world.getLoadedChunks()) {
                chunk.unload();
            }
            if (Bukkit.unloadWorld(world, false)) {
                deleteMap(world.getWorldFolder());
                dungeonMaps.remove(ID);
            }
            return true;
        }
        return false;
    }

    private void deleteMap(File file) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    FileUtils.deleteDirectory(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    private void saveDefaultFolder() {
        mapDirectory = new File(path);
        mapDirectory.mkdir();
    }

    public boolean hasMap(String map) {
        if (maps.contains(map)) {
            return true;
        }
        return false;
    }

    public boolean hasDungeonMap(int ID) {
        return dungeonMaps.containsKey(ID);
    }
}
