package com.seniorcluckers.dungeons.arena.models;

import com.seniorcluckers.dungeons.Dungeons;
import com.seniorcluckers.dungeons.arena.ArenaState;
import com.seniorcluckers.dungeons.arena.modules.StartCountDown;
import com.seniorcluckers.dungeons.party.Party;
import com.seniorcluckers.dungeons.trigger.Trigger;
import com.seniorcluckers.dungeons.utils.ChatUtil;
import com.seniorcluckers.dungeons.utils.Cuboid;
import com.seniorcluckers.dungeons.utils.MessagesDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;

public class Arena implements ConfigurationSerializable {

    private final Dungeons plugin;
    private final MessagesDataManager messagesDataManager;

    private final int ID;

    private ArenaState state = ArenaState.CLOSED;
    private final String displayName;
    private final String map;
    private final Set<UUID> players = new HashSet<>();
    private Location enterSpawn, exitSpawn;
    private Cuboid exitPortal;

    private int waitingTime = 60;
    private int gameTime = 600;
    private int endTime = 60;

    private final Map<UUID, ArenaPlayer> arenaPlayers = new HashMap<>();

    private boolean partyMode = false;
    private Party party;

    private Set<Trigger> triggers;

    private String mapName;

    public Arena(Dungeons plugin, int ID, String displayName, String map, Location enterSpawn, Location exitSpawn, Cuboid exitPortal, int waitingTime, int gameTime, int endTime) {
        this.plugin = plugin;
        this.messagesDataManager = plugin.getMessageDataManager();

        this.ID = ID;
        this.displayName = displayName;
        this.map = map;
        this.enterSpawn = enterSpawn;
        this.exitSpawn = exitSpawn;
        this.exitPortal = exitPortal;

        this.mapName = map + "_" + ID;

        if (waitingTime > 0) {
            this.waitingTime = waitingTime;
        }
        if (gameTime > 0) {
            this.gameTime = gameTime;
        }
        if (endTime > 0) {
            this.endTime = endTime;
        }

        loadTriggers();
        runPreCommands();
    }

    public void loadTriggers() {
        Bukkit.getLogger().log(Level.SEVERE, "Creating triggers...");
        triggers = plugin.getTriggerDataManager().createTriggers(ID, this);
    }

    public void runPreCommands() {
        String path = "dungeons." + ID + ".pre-commands";
        List<String> commands = plugin.getArenaManager().getArenaDataManager().getConfig().getStringList(path);
            for (String command : commands) {
                String commandTemp = command.replace("%WORLD%", getMap() + "_" + ID);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandTemp);
            }
    }

    private void runEndCommands() {
        String path = "dungeons." + ID + ".end-commands";
        List<String> commands = plugin.getArenaManager().getArenaDataManager().getConfig().getStringList(path);
        for (String command : commands) {
            String commandTemp = command.replace("%WORLD%", getMap() + "_" + ID);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandTemp);
        }
    }

    public boolean hasPlayer(Player player) {
        return players.contains(player.getUniqueId());
    }

    private void init() {
        state = ArenaState.WAITING;
        StartCountDown countDown = new StartCountDown(plugin, this, waitingTime);
        countDown.startCountDown();
    }

    public void initEnd() {
        state = ArenaState.CLOSING;
        broadcastMessage(ChatUtil.addColor(messagesDataManager.getMessage("ending")));
        new BukkitRunnable() {
            int seconds = endTime;
            @Override
            public void run() {

                String path = "dungeons." + ID + ".on-end-timer." + seconds;
                FileConfiguration config = getPlugin().getArenaManager().getArenaDataManager().getConfig();
                if (config.contains(path)) {
                    List<Map<?, ?>> list = config.getMapList(path);
                    for (Map map : list) {
                        if (map.containsKey("command")) {
                            broadcastCommand(map.get("command").toString());
                        } else if (map.containsKey("message")) {
                            broadcastMessage(ChatUtil.addColor(map.get("message").toString()));
                        }
                    }
                }

                if (seconds == 0) {
                    setState(ArenaState.CLOSED);
                    cancel();

                    for (Iterator<UUID> iterator = players.iterator(); iterator.hasNext();) {
                        Player player = Bukkit.getPlayer(iterator.next());
                        if (player != null) {
                            plugin.getArenaManager().removePlayer(player.getUniqueId());
                            iterator.remove();
                            player.sendMessage(ChatUtil.convertMessage(player, messagesDataManager.getMessage("closed")));
                        }
                    }

                    World world = Bukkit.getWorld(getMap() + "_" + ID);
                    if (world != null  && !world.getPlayers().isEmpty()) {
                        for (Player player : world.getPlayers()) {
                            player.teleport(exitSpawn);
                        }
                    }

                    runEndCommands();

                    if (world == null) {
                        Bukkit.getLogger().log(Level.SEVERE, "World is null");
                    }

                    if (plugin.getMapManager() == null) {
                        Bukkit.getLogger().log(Level.SEVERE, "Map Manager is null");
                    }

                    plugin.getMapManager().unloadMap(String.valueOf(ID), world.getName());
                    plugin.getArenaManager().removeArena(Arena.this);
                    return;
                }
                --seconds;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void broadcastMessage(String...msg) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(msg);
            }
        }
    }

    public void broadcastCommand(String command) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                String commandTemp = command.replace("%PLAYER%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandTemp);
            }
        }
    }

    public void addPlayer(Player player) {
        if (state == ArenaState.OPEN) {
            init();
        }
        broadcastMessage(ChatUtil.convertMessage(player, messagesDataManager.getMessage("player-joined")));

        players.add(player.getUniqueId());
        arenaPlayers.put(player.getUniqueId(), new ArenaPlayer(player, this));
        player.teleport(enterSpawn);
        player.sendMessage(ChatUtil.convertMessage(player, messagesDataManager.getMessage("joined")));
    }

    public void removePlayer(Player player) {

        if (state == ArenaState.WAITING) {
            players.remove(player.getUniqueId());
            arenaPlayers.remove(player.getUniqueId());
        } else if (state == ArenaState.IN_PROGRESS) {
            players.remove(player.getUniqueId());
            initEnd();
        }

        players.remove(player.getUniqueId());
        player.teleport(exitSpawn);

    }

    public void setState(ArenaState state) {
        this.state = state;
    }

    public int getID() {
        return ID;
    }

    public ArenaState getState() {
        return state;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMap() {
        return map;
    }

    public Set<UUID> getPlayers() {
        return players;
    }

    public Location getEnterSpawn() {
        return enterSpawn;
    }

    public Location getExitSpawn() {
        return exitSpawn;
    }

    public Cuboid getExitPortal() {
        return exitPortal;
    }

    public boolean isPartyMode() {
        return partyMode;
    }

    public Party getParty() {
        return party;
    }

    public void setEnterSpawn(Location enterSpawn) {
        this.enterSpawn = enterSpawn;
    }

    public void setExitSpawn(Location exitSpawn) {
        this.exitSpawn = exitSpawn;
    }

    public void setExitPortal(Cuboid exitPortal) {
        this.exitPortal = exitPortal;
    }

    public void setParty(Party party) {
        this.party = party;
        partyMode = true;
    }

    public Map<UUID, ArenaPlayer> getArenaPlayers() {
        return arenaPlayers;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getGameTime() {
        return gameTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public MessagesDataManager getMessagesDataManager() {
        return messagesDataManager;
    }

    public Dungeons getPlugin() {
        return plugin;
    }

    public String getMapName() {
        return mapName;
    }

    public Set<Trigger> getTriggers() {
        return triggers;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("ID", ID);
        data.put("displayName", displayName);
        data.put("map", map);

        data.put("waiting-timer", waitingTime);
        data.put("game-timer", gameTime);
        data.put("end-timer", endTime);

        data.put("enterSpawn", enterSpawn != null ? enterSpawn.serialize() : "");
        data.put("exitSpawn", exitSpawn != null ? exitSpawn.serialize() : "");
        data.put("exitPortal.pos1", exitPortal != null ? exitPortal.getPoint1().serialize() : "");
        data.put("exitPortal.pos2", exitPortal != null ? exitPortal.getPoint2().serialize() : "");
        return data;
    }

    @Override
    public String toString() {
        return "Arena{" +
                "ID=" + ID +
                ", state=" + state +
                ", displayName='" + displayName + '\'' +
                ", map='" + map + '\'' +
                ", players=" + players +
                ", enterSpawn=" + enterSpawn +
                ", exitSpawn=" + exitSpawn +
                ", exitPortal=" + exitPortal +
                ", partyMode=" + partyMode +
                ", party=" + party +
                '}';
    }
}
