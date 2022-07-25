package com.seniorcluckers.dungeons.party;

import com.seniorcluckers.dungeons.Dungeons;
import com.seniorcluckers.dungeons.party.invite.Invite;
import com.seniorcluckers.dungeons.party.invite.InviteState;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PartyManager {

    private final Dungeons plugin;

    private final int MAX_PLAYERS;

    private Set<Party> parties = new HashSet<>();
    private Set<Invite> invites = new HashSet<>();

    public PartyManager(Dungeons plugin) {
        this.plugin = plugin;
        this.MAX_PLAYERS = plugin.getConfig().getInt("max-players");
    }

    public void createParty(Party party) {
        parties.add(party);
    }

    public void createInvite(Party party, Player recipient) {
        invites.add(new Invite(plugin, party, recipient));
    }

    public void removeParty(Party party) {
        parties.remove(party);
    }

    public void removeInvite(Invite invite) {
        invites.remove(invite);
    }

    public boolean isPlayerInvitedToParty(Player inviter, Player recipient) {
        for (Invite invite : invites) {
            if (inviter.getUniqueId() == invite.getParty().getOwner()) {
                if (invite.getRecipient() == recipient.getUniqueId()) {
                    return true;
                }
            }
        }
        return false;
    }

    public Invite getInvite(Player inviter, Player recipient) {
        for (Invite invite : invites) {
            if (invite.getStatus() != InviteState.EXPIRED) {
                if (inviter.getUniqueId() == invite.getParty().getOwner()) {
                    if (invite.getRecipient() == recipient.getUniqueId()) {
                        return invite;
                    }
                }
            }
        }
        return null;
    }

    public void sanitizePartyInvites(Party party) {
        for (Iterator<Invite> inviteIterator = invites.iterator(); inviteIterator.hasNext();) {
            Invite invite = inviteIterator.next();
            if (invite.getParty() == party) {
                invite.cancelInvite();
                inviteIterator.remove();
            }
        }
    }

    public int getMAX_PLAYERS() {
        return MAX_PLAYERS;
    }

    public Party getParty(Player player) {
        for (Party party : parties) {
            if (party.hasPlayer(player) || party.getOwner() == player.getUniqueId()) {
                return party;
            }
        }
        return null;
    }

    public boolean isPlayerInParty(Player player) {
        for (Party party : parties) {
            if (party.hasPlayer(player) || party.getOwner() == player.getUniqueId()) {
                return true;
            }
        }
        return false;
    }

    public boolean isPlayerPartyOwner(Player player) {
        for (Party party : parties) {
            if (party.getOwner() == player.getUniqueId()) {
                return true;
            }
        }
        return false;
    }

}
