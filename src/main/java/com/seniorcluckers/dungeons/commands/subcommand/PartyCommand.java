package com.seniorcluckers.dungeons.commands.subcommand;

import com.seniorcluckers.dungeons.Dungeons;
import com.seniorcluckers.dungeons.commands.SubCommand;
import com.seniorcluckers.dungeons.party.Party;
import com.seniorcluckers.dungeons.party.invite.Invite;
import com.seniorcluckers.dungeons.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PartyCommand extends SubCommand {

    private final Dungeons plugin;

    public PartyCommand(Dungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "party";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public void perform(Player player, String[] args) {

        if (!player.hasPermission("dungeons.party")) {
            player.sendMessage(ChatUtil.addColor("&cError. You do not have permission."));
            return;
        }

        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("create")) {
                createParty(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("invite")) {
                inviteParty(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("accept")) {
                acceptParty(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("kick")) {
                kickParty(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("leave")) {
                leaveParty(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("info")) {
                infoParty(player, args);
                return;
            }

            player.sendMessage(ChatUtil.addColor("&cError. Command not found."));
        } else {
            printHelp(player);
        }
    }
    //TODO Fix sub commands not working!
    private void createParty(Player player, String[] args) {
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("create")) {
                if (!plugin.getPartyManager().isPlayerInParty(player)) {
                    plugin.getPartyManager().createParty(new Party(plugin, player));
                    player.sendMessage(ChatUtil.addColor("&eParty has been created!"));
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. You're already in a party!"));
                }
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments!"));
        }
    }

    private void inviteParty(Player player, String[] args) {
        if (args.length >= 3) {
            if (args[1].equalsIgnoreCase("invite")) {
                if (plugin.getPartyManager().isPlayerInParty(player)) {
                    if (!plugin.getPartyManager().isPlayerPartyOwner(player)) {
                        player.sendMessage(ChatUtil.addColor("&cError. Only the party leader can invite others!"));
                        return;
                    }
                    Player recipient = Bukkit.getPlayer(args[2]);
                    if (recipient != null) {
                        if (recipient.getUniqueId() == player.getUniqueId()) {
                            player.sendMessage(ChatUtil.addColor("&cYou can't invite yourself!"));
                            return;
                        }

                        if (plugin.getPartyManager().getParty(player).getPlayers().size() < plugin.getPartyManager().getMAX_PLAYERS()) {
                            if (plugin.getPartyManager().isPlayerInvitedToParty(player, recipient)) {
                                player.sendMessage(ChatUtil.addColor("&cError. You already sent an invite to this player!"));
                                return;
                            }
                            player.sendMessage(ChatUtil.addColor("&eYou have sent an invite to " + recipient.getName() + "!"));
                            recipient.sendMessage(ChatUtil.addColor("&eYou have received an invite from " + player.getName() + "!"));
                            plugin.getPartyManager().createInvite(plugin.getPartyManager().getParty(player), recipient);
                        } else {
                            player.sendMessage(ChatUtil.addColor("&cError. The party is full."));
                        }
                    } else {
                        player.sendMessage(ChatUtil.addColor("&cError. Player not found."));
                    }
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. You're not in a party!"));
                }
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments!"));
        }
    }

    private void acceptParty(Player player, String[] args) {
        if (args.length >= 3) {
            if (args[1].equalsIgnoreCase("accept")) {
                if (!plugin.getPartyManager().isPlayerInParty(player)) {
                    Player inviter = Bukkit.getPlayer(args[2]);
                    if (inviter != null) {
                        if (plugin.getPartyManager().isPlayerPartyOwner(inviter)) {
                            Invite invite = plugin.getPartyManager().getInvite(inviter, player);
                            if (invite == null) {
                                player.sendMessage(ChatUtil.addColor("&cError. You have not been invited to this party!"));
                                return;
                            }

                            if (invite.getParty().getPlayers().size() < plugin.getPartyManager().getMAX_PLAYERS()) {
                                invite.getParty().broadcastMessage(ChatUtil.addColor("&e" + player.getName() + " has joined the party!"));
                                invite.getParty().addPlayer(player);
                                player.sendMessage(ChatUtil.addColor("&eYou have joined the party!"));
                            } else {
                                player.sendMessage(ChatUtil.addColor("&cError. The party is full."));
                            }
                        } else {
                            player.sendMessage(ChatUtil.addColor("&cError. The party no longer exists."));
                        }
                    } else {
                        player.sendMessage(ChatUtil.addColor("&cError. Player not found."));
                    }
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. You must leave your current party!"));
                }
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments!"));
        }
    }

    private void kickParty(Player player, String[] args) {
        if (args.length >= 3) {
            if (args[1].equalsIgnoreCase("kick")) {
                if (plugin.getPartyManager().isPlayerInParty(player)) {
                    if (plugin.getPartyManager().isPlayerPartyOwner(player)) {
                        Player recipient = Bukkit.getPlayer(args[2]);
                        if (recipient != null) {
                            Party party = plugin.getPartyManager().getParty(player);
                            if (party.getOwner() == recipient.getUniqueId()) {
                                player.sendMessage(ChatUtil.addColor("&cYou can't kicked yourself!"));
                                return;
                            } else if (party.hasPlayer(recipient)) {
                                party.removePlayer(recipient);
                                recipient.sendMessage(ChatUtil.addColor("&eYou were kicked from the party!"));
                                party.broadcastMessage(ChatUtil.addColor("&e" + recipient.getName() + " has been kicked from the party!"));
                            } else {
                                player.sendMessage(ChatUtil.addColor("&cError. Player is not in the party."));
                            }
                        } else {
                            player.sendMessage(ChatUtil.addColor("&cError. Player not found."));
                        }
                    } else {
                        player.sendMessage(ChatUtil.addColor("&cError. Only the party leader can kick others!"));
                    }
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. You're not in a party!"));
                }
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments!"));
        }
    }

    private void leaveParty(Player player, String[] args) {
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("leave")) {
                if (plugin.getPartyManager().isPlayerInParty(player)) {
                    Party party = plugin.getPartyManager().getParty(player);

                    if (player.getUniqueId() == party.getOwner()) {
                        party.removePlayer(player);
                        player.sendMessage(ChatUtil.addColor("&eYou have left the party!"));
                    } else {
                        party.removePlayer(player);
                        player.sendMessage(ChatUtil.addColor("&eYou have left the party!"));
                        party.broadcastMessage(ChatUtil.addColor("&e" + player.getName() + " has left the party!"));
                    }
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. You're not in a party!"));
                }
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments!"));
        }
    }

    private void infoParty(Player player, String[] args) {
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("info")) {
                if (plugin.getPartyManager().isPlayerInParty(player)) {
                    Party party = plugin.getPartyManager().getParty(player);
                    player.sendMessage(ChatUtil.addColor("&bParty Info:"));
                    player.sendMessage(ChatUtil.addColor("&bOwner&7:&e " + Bukkit.getPlayer(party.getOwner()).getName()));

                    StringBuilder members = new StringBuilder();
                    for (UUID uuid : party.getPlayers()) {
                        if (uuid == party.getOwner()) {
                            continue;
                        }
                        if (party.getPlayers().size() > 1) {
                            members.append(Bukkit.getOfflinePlayer(uuid).getName() + ", ");
                        } else {
                            members.append(Bukkit.getOfflinePlayer(uuid).getName() + ".");
                        }
                    }
                    player.sendMessage(ChatUtil.addColor("&bMembers&7:&e " + members));
                    player.sendMessage(ChatUtil.addColor("&bSize&7:&e " + party.getPlayers().size() + "/" + plugin.getPartyManager().getMAX_PLAYERS()));
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. You're not in a party!"));
                }
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments!"));
        }
    }

    private void printHelp(Player sender) {
        sender.sendMessage(ChatUtil.addColor("&aDungeons v1.0"));
        sender.sendMessage(ChatUtil.addColor("&b/dungeon party create&7:&e Create a party."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeon party invite &3<player>&7:&e Invite a player to your party."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeon party accept &3<player>&7:&e Accept a player's party invite."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeon party kick &3<player>&7:&e Remove a player from the party."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeon party leave&7:&e Leave the party."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeon party info&7:&e Your current party information."));
    }
}
