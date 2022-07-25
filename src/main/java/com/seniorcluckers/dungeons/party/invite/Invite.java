package com.seniorcluckers.dungeons.party.invite;

import com.seniorcluckers.dungeons.Dungeons;
import com.seniorcluckers.dungeons.party.Party;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class Invite {

    private final Dungeons plugin;

    private final Party party;
    private InviteState status;

    private final UUID recipient;

    private BukkitTask task;

    public Invite(Dungeons plugin,  Party party, Player recipient) {
        this.plugin = plugin;
        this.recipient = recipient.getUniqueId();
        this.party = party;
        this.status = InviteState.AVAILABLE;

        startExpiration();
    }

    private void startExpiration() {
        task = new BukkitRunnable() {
            @Override
            public void run() {

                status = InviteState.EXPIRED;
                plugin.getPartyManager().removeInvite(Invite.this);
                cancel();
            }
        }.runTaskLater(plugin, 20L * 30L);
    }

    public void cancelInvite() {
        if (!task.isCancelled()) {
            task.cancel();
        }
    }

    public Party getParty() {
        return party;
    }

    public InviteState getStatus() {
        return status;
    }

    public UUID getRecipient() {
        return recipient;
    }

    @Override
    public String toString() {
        return "Invite{" +
                "party=" + party +
                ", status=" + status +
                ", recipient=" + recipient +
                '}';
    }
}
