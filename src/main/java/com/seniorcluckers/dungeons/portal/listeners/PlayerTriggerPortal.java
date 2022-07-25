package com.seniorcluckers.dungeons.portal.listeners;

import com.seniorcluckers.dungeons.Dungeons;
import com.seniorcluckers.dungeons.arena.models.Arena;
import com.seniorcluckers.dungeons.portal.Portal;
import com.seniorcluckers.dungeons.utils.ChatUtil;
import com.seniorcluckers.dungeons.utils.MessagesDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerTriggerPortal implements Listener {

    private final Dungeons plugin;
    private final MessagesDataManager messagesDataManager;

    private List<UUID> players = new ArrayList<>();

    public PlayerTriggerPortal(Dungeons plugin) {
        this.plugin = plugin;
        this.messagesDataManager = plugin.getMessageDataManager();
    }

    // What is the purpose of priority? Is there an example?
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        Portal portal = plugin.getPortalManager().findPortal(player);
        if (portal != null) {
            if (players.contains(player.getUniqueId())) {
                return;
            } else {
                players.add(player.getUniqueId());
            }

            //TODO we need a check to make sure everything is in the config...
            if (!plugin.getArenaManager().hasArena(Integer.parseInt(portal.getDestination()))) {
                if (!plugin.getPortalManager().isPortalDestinationValid(portal)) {
                    player.sendMessage(ChatUtil.convertMessage(player, messagesDataManager.getMessage("destination-error")));
                    return;
                } else {

                    if (player.hasPermission("dungeons.dungeon." + portal.getDestination())) {
                        if (!plugin.getMapManager().hasDungeonMap(Integer.parseInt(portal.getDestination()))) {
                            if (plugin.getArenaManager().getArenaDataManager().isArenaSetUp(Integer.parseInt(portal.getDestination()))) {
                                plugin.getMapManager().copyMap(Integer.parseInt(portal.getDestination()));
                            } else {
                                player.sendMessage(ChatUtil.convertMessage(player, messagesDataManager.getMessage("error")));
                                return;
                            }
                        }
                        player.sendMessage(ChatUtil.convertMessage(player, messagesDataManager.getMessage("opening")));
                    } else {
                        player.sendMessage(ChatUtil.convertMessage(player, messagesDataManager.getMessage("no-permission")));
                    }
                }
            } else {
                Arena arena = plugin.getArenaManager().getArena(Integer.parseInt(portal.getDestination()));
                plugin.getArenaManager().addPlayer(player, arena);
            }
        } else {
            if (players.contains(player.getUniqueId())) {
                players.remove(player.getUniqueId());
                return;
            }
        }
    }

}
