package com.seniorcluckers.dungeons.arena.modules;

import com.seniorcluckers.dungeons.Dungeons;
import com.seniorcluckers.dungeons.arena.models.Arena;
import com.seniorcluckers.dungeons.arena.ArenaState;
import com.seniorcluckers.dungeons.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import javax.security.auth.login.Configuration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class StartCountDown extends CountDown {

    public StartCountDown(Dungeons plugin, Arena arena, int seconds) {
        super(plugin, arena, seconds);
    }

    @Override
    public void startCountDown() {
        new BukkitRunnable() {
            @Override
            public void run() {

                if (getArena().getState() == ArenaState.CLOSING) {
                    cancel();
                    return;
                }

//                Bukkit.getLogger().log(Level.SEVERE, "dungeons." + getArena().getID() + ".on-waiting-timer." + getSeconds());
//                Bukkit.getLogger().log(Level.SEVERE, getPlugin().getArenaManager().getArenaDataManager().getConfig().getList("dungeons." + getArena().getID() + ".on-waiting-timer." + getSeconds()).toString());
                String path = "dungeons." + getArena().getID() + ".on-waiting-timer." + getSeconds();
                FileConfiguration config = getPlugin().getArenaManager().getArenaDataManager().getConfig();
                if (config.contains(path)) {
                    List<Map<?, ?>> list = config.getMapList(path);
                    for (Map map : list) {
                        if (map.containsKey("command")) {
                            getArena().broadcastCommand(map.get("command").toString());
                        } else if (map.containsKey("message")) {
                            getArena().broadcastMessage(ChatUtil.addColor(map.get("message").toString()));
                        }
                    }
                }

                if (getSeconds() == 0) {
                    getArena().broadcastMessage(ChatUtil.addColor(getArena().getMessagesDataManager().getMessage("door-closed")));
                    getArena().setState(ArenaState.IN_PROGRESS);
                    GameTimer gameTimer = new GameTimer(getArena(), getArena().getGameTime());
                    gameTimer.runTaskTimer(getPlugin(), 0L, 20L);

                    cancel();
                    return;
                }
                setSeconds(getSeconds() - 1);
            }
        }.runTaskTimer(getPlugin(), 0L, 20L);
    }

}
