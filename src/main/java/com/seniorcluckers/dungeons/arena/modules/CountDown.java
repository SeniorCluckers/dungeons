package com.seniorcluckers.dungeons.arena.modules;

import com.seniorcluckers.dungeons.Dungeons;
import com.seniorcluckers.dungeons.arena.models.Arena;

public abstract class CountDown {

    private final Dungeons plugin;
    private final Arena arena;

    private int seconds;

    public CountDown(Dungeons plugin, Arena arena, int seconds) {
        this.plugin = plugin;
        this.arena = arena;
        this.seconds = seconds;
    }

    public Dungeons getPlugin() {
        return plugin;
    }

    public Arena getArena() {
        return arena;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    abstract void startCountDown();
}
