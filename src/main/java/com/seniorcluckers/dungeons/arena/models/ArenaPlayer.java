package com.seniorcluckers.dungeons.arena.models;

import org.bukkit.entity.Player;

import java.util.UUID;

public class ArenaPlayer {

    private UUID uuid;
    private String name;
    private Arena arena;

    private int deaths = 0;

    public ArenaPlayer(Player player, Arena arena) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.arena = arena;
    }

    public void addDeath() {
        deaths++;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Arena getArena() {
        return arena;
    }

    public int getDeaths() {
        return deaths;
    }
}
