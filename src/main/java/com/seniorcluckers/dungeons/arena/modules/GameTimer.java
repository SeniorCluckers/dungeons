package com.seniorcluckers.dungeons.arena.modules;

import com.seniorcluckers.dungeons.arena.models.Arena;
import com.seniorcluckers.dungeons.utils.ChatUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;

public class GameTimer extends BukkitRunnable {

    private final Arena arena;
    private int seconds;

    public GameTimer(Arena arena, int seconds) {
        this.arena = arena;
        this.seconds = seconds;
    }

    @Override
    public void run() {
        String path = "dungeons." + arena.getID() + ".on-game-timer." + seconds;
        FileConfiguration config = arena.getPlugin().getArenaManager().getArenaDataManager().getConfig();
        if (config.contains(path)) {
            List<Map<?, ?>> list = config.getMapList(path);
            for (Map map : list) {
                if (map.containsKey("command")) {
                    arena.broadcastCommand(map.get("command").toString());
                } else if (map.containsKey("message")) {
                    arena.broadcastMessage(ChatUtil.addColor(map.get("message").toString()));
                }
            }
        }

        --seconds;
        if (seconds == 0) {
            arena.initEnd();
            cancel();
            return;
        }
    }

    public String convert(int secs) {
        int h = secs / 3600, i = secs - h * 3600, m = i / 60, s = i - m * 60;
        String timeF = "";

        if (h < 10) {
            timeF = timeF + "0";
        }
        timeF = timeF + h + ":";
        if (m < 10) {
            timeF = timeF + "0";
        }
        timeF = timeF + m + ":";
        if (s < 10) {
            timeF = timeF + "0";
        }
        timeF = timeF + s;

        return timeF;
    }


}
