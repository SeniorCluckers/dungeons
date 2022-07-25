package com.seniorcluckers.dungeons.arena.listeners;

import com.seniorcluckers.dungeons.Dungeons;
import com.seniorcluckers.dungeons.arena.ArenaState;
import com.seniorcluckers.dungeons.arena.models.Arena;
import com.seniorcluckers.dungeons.trigger.Trigger;
import com.seniorcluckers.dungeons.utils.ChatUtil;
import com.seniorcluckers.dungeons.utils.MessagesDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ArenaListener implements Listener {

    private final Dungeons plugin;
    private final MessagesDataManager messagesDataManager;

    public ArenaListener(Dungeons plugin) {
        this.plugin = plugin;
        this.messagesDataManager = plugin.getMessageDataManager();
    }

    @EventHandler
    public void onPlayerMoveTrigger(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Arena arena = plugin.getArenaManager().getPlayer(player);
        if (arena != null) {
            if (arena.getTriggers() != null) {
                for (Trigger trigger : arena.getTriggers()) {
                    if (trigger.getBounds().isIn(event.getPlayer())) {
                        trigger.runCommands(player);
                    } else {
                        trigger.removePlayer(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Arena arena = plugin.getArenaManager().getPlayer(player);
        if (arena != null) {
            if (arena.getExitPortal().isIn(player)) {
                if (arena.getState() == ArenaState.IN_PROGRESS || arena.getState() == ArenaState.CLOSING) {
                    player.sendMessage(ChatUtil.convertMessage(player, messagesDataManager.getMessage("completed")));
                    plugin.getArenaManager().removePlayer(player);
                    arena.broadcastMessage(ChatUtil.convertMessage(player, messagesDataManager.getMessage("completed-dungeon")));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (plugin.getArenaManager().hasPlayer(event.getPlayer())) {
            Arena arena = plugin.getArenaManager().getPlayer(event.getPlayer());
            event.setRespawnLocation(arena.getEnterSpawn());
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (plugin.getArenaManager().hasPlayer(event.getPlayer())) {
            Arena arena = plugin.getArenaManager().getPlayer(event.getPlayer());
            if (event.getTo().getWorld() != event.getFrom().getWorld()) {
                if (arena.getState() != ArenaState.CLOSING) {
                    if (arena.hasPlayer(event.getPlayer())) {
                        event.getPlayer().sendMessage(ChatUtil.convertMessage(event.getPlayer(), messagesDataManager.getMessage("teleportation-error")));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

}
