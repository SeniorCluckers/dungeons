package com.seniorcluckers.dungeons.trigger;

import com.seniorcluckers.dungeons.arena.models.Arena;
import com.seniorcluckers.dungeons.utils.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.*;

public class Trigger implements ConfigurationSerializable {

    private final Arena arena;

    private final TriggerType triggerType;
    private int uses = 0;
    private final Cuboid bounds;
    private final List<String> commands;

    private List<UUID> players = new ArrayList<>();

    public Trigger(TriggerType triggerType, int uses, Cuboid bounds, List<String> commands, Arena arena) {
        this.triggerType = triggerType;
        this.uses = uses;
        this.bounds = bounds;
        this.commands = commands;
        this.arena = arena;
    }

    public Trigger(Cuboid bounds) {
        this.triggerType = TriggerType.ALL;
        this.bounds = bounds;
        arena = null;
        commands = null;
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }

    public void runCommands(Player player) {
        if (uses == 0) {
            return;
        }

        if (bounds.isIn(player)) {
            if (players.contains(player.getUniqueId())) {
                return;
            } else {
                addPlayer(player);
            }
        }

        if (triggerType == TriggerType.INDIVIDUAL) {
            for (String commandTemp : commands) {
                String command = commandTemp.replace("%WORLD%", arena.getMapName()).replace("%PLAYER%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        } else if (triggerType == TriggerType.ALL) {
            for (UUID uuid : arena.getPlayers()) {
                Player arenaPlayer = Bukkit.getPlayer(uuid);
                if (arenaPlayer != null) {
                    for (String commandTemp : commands) {
                        String command = commandTemp.replace("%WORLD%", arena.getMapName()).replace("%PLAYER%", arenaPlayer.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                }
            }
        } else if (triggerType == TriggerType.SINGLE) {
            for (String commandTemp : commands) {
                String command = commandTemp.replace("%WORLD%", arena.getMapName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
        --uses;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public int getUses() {
        return uses;
    }

    public Cuboid getBounds() {
        return bounds;
    }

    public List<String> getCommands() {
        return commands;
    }

    @Override
    public Map<String, Object> serialize() {

        Map<String, Object> data = new LinkedHashMap<>();

        data.put("type", triggerType.name());
        data.put("uses", uses);
        data.put("bounds.pos1", bounds.getPoint1() != null ? bounds.getPoint1().serialize() : "");
        data.put("bounds.pos2", bounds.getPoint2() != null ? bounds.getPoint2().serialize() : "");

        List<String> commands = new ArrayList<>();
        commands.add("say This is a command");
        data.put("commands", commands);

        return data;
    }
}
