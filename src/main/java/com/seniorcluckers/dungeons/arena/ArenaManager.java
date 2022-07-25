package com.seniorcluckers.dungeons.arena;

import com.seniorcluckers.dungeons.Dungeons;
import com.seniorcluckers.dungeons.arena.models.Arena;
import com.seniorcluckers.dungeons.arena.models.ArenaPlayer;
import com.seniorcluckers.dungeons.utils.ChatUtil;
import com.seniorcluckers.dungeons.utils.Cuboid;
import com.seniorcluckers.dungeons.utils.MessagesDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class ArenaManager {

    private final Dungeons plugin;

    private final ArenaDataManager arenaDataManager;
    private final MessagesDataManager messagesDataManager;

    private final int MAX_PLAYERS;

    private Set<Arena> arenas = new LinkedHashSet<>();
    private Map<UUID, Arena> players = new HashMap<>();

    private int arenaSize = 0;

    public ArenaManager(Dungeons plugin) {
        this.plugin = plugin;
        this.arenaDataManager = new ArenaDataManager(plugin);
        this.messagesDataManager = plugin.getMessageDataManager();

        this.MAX_PLAYERS = plugin.getConfig().getInt("max-players");
        loadArenaIDs();
    }

    public int saveArena(String displayName, String map) {
        Arena arena = new Arena(plugin, ++arenaSize, displayName, map, null, null, null, 0, 0, 0);
        arenaDataManager.saveArena(arena);
        return arena.getID();
    }

    public void addArena(Arena arena) {
//        Bukkit.getLogger().log(Level.SEVERE, arena.toString());
        arena.setState(ArenaState.OPEN);
        arenas.add(arena);
    }

    public boolean hasArena(int ID) {
        for (Arena arena : arenas) {
            if (arena.getID() == ID) {
                return true;
            }
        }
        return false;
    }

    public Set<Arena> getArenas() {
        return arenas;
    }

    public Arena getArena(int ID) {
        for (Arena arena : arenas) {
            if (arena.getID() == ID) {
                return arena;
            }
        }
        return null;
    }

    public List<String> getLiveArenaInfo(int ID) {

        Arena arena = getArena(ID);

        List<String> arenaInfo = new ArrayList<>();
        arenaInfo.add(ChatUtil.addColor("&bID&7:&e " + arena.getID()));
        arenaInfo.add(ChatUtil.addColor("&bStatus&7:&e " + arena.getState()));
        arenaInfo.add(ChatUtil.addColor("&bDisplay Name&7:&e " + arena.getDisplayName()));
        arenaInfo.add(ChatUtil.addColor("&bMap&7:&e " + arena.getMap()));

        StringBuilder players = new StringBuilder();

        for (ArenaPlayer player : arena.getArenaPlayers().values()) {
            if (arena.getArenaPlayers().values().size() > 1) {
                players.append(player.getName() + ", ");
            } else {
                players.append(player.getName() + ".");
            }
        }

        arenaInfo.add(ChatUtil.addColor("&bPlayers&7:&e " + players));

        return arenaInfo;
    }

//    public void addArena(int ID, Arena arena) {
//        if (ID >= arenaSize) {
//            arenaSize = ID;
//        }
//        arenas.add(arena);
//    }

    public void removeArena(Arena arena) {
        arenas.remove(arena);
    }

    public void deleteArena(Arena arena) {
        arenaDataManager.deleteArena(arena);
    }

    private void loadArenaIDs() {
        ConfigurationSection section = arenaDataManager.getConfig().getConfigurationSection("dungeons");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            ConfigurationSection arenaData = section.getConfigurationSection(key);
//            Bukkit.getLogger().log(Level.SEVERE, arenaData.toString());

            int ID = arenaData.getInt("ID");
            if (ID >= arenaSize) {
                arenaSize = ID;
            }
        }
    }

    public void loadArena(int ID) {
        ConfigurationSection arenaData = arenaDataManager.getConfig().getConfigurationSection("dungeons." + ID);
        if (arenaData == null) {
            return;
        }

        String displayName = arenaData.getString("displayName");
        String map = arenaData.getString("map");

        int waitingTime = arenaData.getInt("waiting-timer");
        int gameTime = arenaData.getInt("game-timer");
        int endTime = arenaData.getInt("end-timer");

        try {
            Location enter = createLocation(ID, "enterSpawn");
            Location exit = Location.deserialize(arenaData.getConfigurationSection("exitSpawn").getValues(true));
            Location exitPortalPos1 = createLocation(ID, "exitPortal.pos1");
            Location exitPortalPos2 = createLocation(ID, "exitPortal.pos2");

//            Bukkit.getLogger().log(Level.SEVERE, "Dungeon #" + ID + " has been added to arenas!");
            addArena(new Arena(plugin, ID, displayName, map, enter, exit, new Cuboid(exitPortalPos1, exitPortalPos2), waitingTime, gameTime, endTime));
        } catch (NullPointerException ex) {
            ex.printStackTrace();
//            Bukkit.getLogger().log(Level.SEVERE, "Dungeon #" + ID + " could not be loaded!");
            return;
        }
    }

    private Location createLocation(int ID, String path) {
        ConfigurationSection section = arenaDataManager.getConfig().getConfigurationSection("dungeons." + ID + "." + path);

//        Bukkit.getLogger().log(Level.SEVERE, section.toString());

        String world = section.getString("world") + "_" + ID;
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        float yaw = (float) section.getDouble("yaw");
        float pitch = (float) section.getDouble("pitch");

//        Bukkit.getLogger().log(Level.SEVERE, world);

        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public ArenaDataManager getArenaDataManager() {
        return arenaDataManager;
    }

    public boolean hasPlayer(Player target) {
//        for (UUID player : players.keySet()) {
//            if (player == target.getUniqueId()) {
//                return true;
//            }
//        }
//        return false;
        if (players.containsKey(target.getUniqueId())) {
            return true;
        }
        return false;
    }

    public Arena getPlayer(Player player) {
        if (players.containsKey(player.getUniqueId())) {
            return players.get(player.getUniqueId());
        } else {
            return null;
        }
    }

    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    public void removePlayer(Player player) {
        if (players.containsKey(player.getUniqueId())) {
            Arena arena = players.get(player.getUniqueId());
            players.remove(player.getUniqueId());
            arena.removePlayer(player);
        }
    }

    public boolean addPlayer(Player player, Arena arena) {

        if (player.hasPermission("dungeons.dungeon." + arena.getID())) {
            if (arena.getState() == ArenaState.OPEN) {
                if (plugin.getPartyManager().isPlayerInParty(player)) {
                    arena.setParty(plugin.getPartyManager().getParty(player));
                }
                arena.addPlayer(player);
                players.put(player.getUniqueId(), arena);
                return true;
            } else if (arena.getState() == ArenaState.WAITING) {

                if (arena.getPlayers().size() < MAX_PLAYERS) {
                    if (arena.isPartyMode()) {
                        if (arena.getParty().hasPlayer(player)) {
                            arena.addPlayer(player);
                            players.put(player.getUniqueId(), arena);
                        } else {
                            player.sendMessage(ChatUtil.convertMessage(player, messagesDataManager.getMessage("reserved")));
                        }
                    } else {
                        arena.addPlayer(player);
                        players.put(player.getUniqueId(), arena);
                    }
                } else {
                    player.sendMessage(ChatUtil.convertMessage(player, messagesDataManager.getMessage("max-players")));
                }
            } else {
                player.sendMessage(ChatUtil.convertMessage(player, messagesDataManager.getMessage("portal-closed")));
            }
        } else {
            player.sendMessage(ChatUtil.convertMessage(player, messagesDataManager.getMessage("no-permission")));
        }
        return false;
    }

    public Map<UUID, Arena> getPlayers() {
        return players;
    }
}
