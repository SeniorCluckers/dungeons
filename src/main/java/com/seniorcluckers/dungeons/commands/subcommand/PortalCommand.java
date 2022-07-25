package com.seniorcluckers.dungeons.commands.subcommand;

import com.seniorcluckers.dungeons.Dungeons;
import com.seniorcluckers.dungeons.commands.SubCommand;
import com.seniorcluckers.dungeons.portal.Portal;
import com.seniorcluckers.dungeons.utils.ChatUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class PortalCommand extends SubCommand {

    private final Dungeons plugin;
    private final PositionCommand positionCommand;

    public PortalCommand(Dungeons plugin, PositionCommand positionCommand) {
        this.plugin = plugin;
        this.positionCommand = positionCommand;
    }

    @Override
    public String getName() {
        return "portal";
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
        if (!player.hasPermission("dungeons.portal")) {
            player.sendMessage(ChatUtil.addColor("&cError. You do not have permission."));
            return;
        }

        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("create")) {
                createPortal(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("delete")) {
                deletePortal(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("info")) {
                infoPortal(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("tp")) {
                teleportPortal(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("list")) {
                listPortal(player, args);
                return;
            }

            player.sendMessage(ChatUtil.addColor("&cError. Command not found."));
        } else {
            printHelp(player);
        }
    }

    // /dgs portal create <arena id>
    private void createPortal(Player player, String[] args) {
        if (args.length >= 3) {
            if (args[1].equalsIgnoreCase("create")) {
                if (plugin.getArenaManager().getArenaDataManager().hasArena(Integer.parseInt(args[2]))) {
                    if (!positionCommand.isPositionsSelected()) {
                        player.sendMessage(ChatUtil.addColor("&cError. Please select the portal positions."));
                        return;
                    }
                    player.sendMessage(ChatUtil.addColor("&eA portal has been created!"));
                    plugin.getPortalManager().addPortal(positionCommand.getPos1(), positionCommand.getPos2(), args[2]);
                    positionCommand.resetPositions();
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. Dungeon not found!"));
                }
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments!"));
        }
    }

    private void deletePortal(Player player, String[] args) {
        if (args.length >= 3) {
            if (args[1].equalsIgnoreCase("delete")) {
                Portal portal = plugin.getPortalManager().getPortal(Integer.parseInt(args[2]));
                if (portal != null) {
                    player.sendMessage(ChatUtil.addColor("&eA portal has been deleted!"));
                    plugin.getPortalManager().removePortal(portal);
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. Portal was not found!"));
                }
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments!"));
        }
    }

    private void teleportPortal(Player player, String[] args) {
        if (args.length >= 3) {
            if (args[1].equalsIgnoreCase("tp")) {
                Portal portal = plugin.getPortalManager().getPortal(Integer.parseInt(args[2]));
                if (portal != null) {
                    player.sendMessage(ChatUtil.addColor("&eTeleported you to portal #" + portal.getID()));
                    player.teleport(portal.getCuboid().getPoint1().add(0, 0, -1));
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. Portal was not found!"));
                }
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments!"));
        }
    }

    private void infoPortal(Player player, String[] args) {
        if (args.length >= 3) {
            if (args[1].equalsIgnoreCase("info")) {
                Portal portal = plugin.getPortalManager().getPortal(Integer.parseInt(args[2]));
                if (portal != null) {
                    player.sendMessage(ChatUtil.addColor("&bPortal Info&7:"));
                    player.sendMessage(ChatUtil.addColor("&bPortal &e#" + portal.getID()));
                    player.sendMessage(ChatUtil.addColor("&bDestination&7:&e " + portal.getDestination()));
                    player.sendMessage(ChatUtil.addColor("&bBounds&7:&e " + "("
                    + portal.getCuboid().getPoint1().getBlockX() + "," + portal.getCuboid().getPoint1().getBlockY() +
                            "," + portal.getCuboid().getPoint1().getBlockZ() + ") -> (" +
                            portal.getCuboid().getPoint2().getBlockX() + "," + portal.getCuboid().getPoint2().getBlockY() +
                            "," + portal.getCuboid().getPoint2().getBlockZ() + ")"));
                BaseComponent component = new TextComponent(ChatUtil.addColor("&a[Click to teleport to portal]"));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dgs portal tp " + portal.getID()));
                player.spigot().sendMessage(component);
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. Portal was not found!"));
                }
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments!"));
        }
    }

    private void listPortal(Player player, String[] args) {
        if (args.length >= 1) {
            if (args[1].equalsIgnoreCase("list")) {
                player.sendMessage(ChatUtil.addColor("&bPortals&7:"));
                for (Portal portal : plugin.getPortalManager().getPortals()) {
                    BaseComponent dungeonIdInfoComponent = new TextComponent(ChatUtil.addColor("&e#" + portal.getID()));

                    BaseComponent dungeonInfoComponent = new TextComponent(ChatUtil.addColor(" &a[?]"));

                    BaseComponent[] dungeonInfoHoverComponent = new BaseComponent[1];
                    dungeonInfoHoverComponent[0] = new TextComponent(ChatUtil.addColor("&eClick here for details on this dungeon."));

                    dungeonInfoComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, dungeonInfoHoverComponent));
                    dungeonInfoComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dgs portal info " + portal.getID()));
                    player.spigot().sendMessage(dungeonIdInfoComponent, dungeonInfoComponent);                }
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments!"));
        }
    }

    public void printHelp(Player sender) {
        sender.sendMessage(ChatUtil.addColor("\n&aDungeons v1.0 - Portal Commands"));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons portal create &3<destination-ID>&7:&e Create portal that points to a dungeon ID."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons portal delete &3<ID>&7:&e Delete portal using ID."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons portal tp &3<arena>&7:&e Teleport to portal."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons portal info &3<ID>&7:&e Get portal information."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons portal list&7:&e List all portals."));
    }
}
