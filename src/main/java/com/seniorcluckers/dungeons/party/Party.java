package com.seniorcluckers.dungeons.party;

import com.seniorcluckers.dungeons.Dungeons;
import com.seniorcluckers.dungeons.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Party {

    private final Dungeons plugin;

    private UUID owner;
    private Set<UUID> players;

    public Party(Dungeons plugin, Player player) {
        this.plugin = plugin;
        this.owner = player.getUniqueId();
        this.players = new HashSet<>();

        addPlayer(player);
    }

    public void broadcastMessage(String...msg) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(msg);
            }
        }
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        if (player.getUniqueId() == owner) {
            if (players.size() == 0) {
                plugin.getPartyManager().removeParty(this);
                plugin.getPartyManager().sanitizePartyInvites(this);
            } else if (players.size() > 0) {
                owner = players.iterator().next();
                broadcastMessage(ChatUtil.addColor("&e" + Bukkit.getOfflinePlayer(owner).getName() + " is now the party leader!"));
            }
            return;
        }
    }

    public Set<UUID> getPlayers() {
        return players;
    }

    public boolean hasPlayer(Player player) {
        return players.contains(player.getUniqueId()) ? true : false;
    }

    public UUID getOwner() {
        return owner;
    }
}
