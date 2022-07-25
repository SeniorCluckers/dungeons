package com.seniorcluckers.dungeons.commands.subcommand;

import com.seniorcluckers.dungeons.Dungeons;
import com.seniorcluckers.dungeons.arena.models.Arena;
import com.seniorcluckers.dungeons.commands.SubCommand;
import com.seniorcluckers.dungeons.utils.ChatUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.List;

public class ArenaCommand extends SubCommand {

    private final Dungeons plugin;
    private final PositionCommand positionCommand;

    public ArenaCommand(Dungeons plugin, PositionCommand positionCommand) {
        this.plugin = plugin;
        this.positionCommand = positionCommand;
    }

    @Override
    public String getName() {
        return "instance";
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

        if (!player.hasPermission("dungeons.instance")) {
            player.sendMessage(ChatUtil.addColor("&cError. You do not have permission."));
            return;
        }

        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("create")) {
                create(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("list")) {
                list(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("live")) {
                liveArenas(player, args);
                return;
            }

            // Join dungeon using command
            if (args[1].equalsIgnoreCase("join")) {
                join(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("info")) {
                info(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("live-info")) {
                instanceInfo(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("set-map")) {
                setMap(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("spawn")) {
                spawn(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("exit-portal")) {
                exitPortal(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("delete")) {
                delete(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("reload")) {
                reload(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("trigger")) {
                trigger(player, args);
                return;
            }

            player.sendMessage(ChatUtil.addColor("&cError. Command not found."));
        } else {
            printHelp(player);
        }
    }

    private void trigger(Player player, String[] args) {
        if (args.length >= 4) {
            if (ChatUtil.isStringNumeric(args[2])) {
                int ID = Integer.parseInt(args[2]);
                if (plugin.getArenaManager().getArenaDataManager().hasArena(ID)) {
                    String triggerName = args[3];

                    if (!checkPlayerWorld(player, ID)) {
                        return;
                    }

                    if (!positionCommand.isPositionsSelected()) {
                        player.sendMessage(ChatUtil.addColor("&cError. Please select the trigger positions."));
                        return;
                    }

                    plugin.getTriggerDataManager().addTrigger(ID, triggerName, positionCommand.getPos1(), positionCommand.getPos2());
                    player.sendMessage(ChatUtil.addColor("&eTrigger has been added for instance #" + ID + "!" + " Edit config triggers.yml."));
                    positionCommand.resetPositions();
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. Could not find dungeon!"));
                }
            } else {
                player.sendMessage(ChatUtil.addColor("&cError. Please enter a valid ID."));
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments."));
        }
    }

    private void reload(Player player, String[] args) {
        if (args.length >= 1) {
            plugin.getArenaManager().getArenaDataManager().reloadConfig();
            plugin.getTriggerDataManager().reloadConfig();
            player.sendMessage(ChatUtil.addColor("&eReloaded configs!"));
        }
    }

    private void delete(Player player, String[] args) {
        if (args.length >= 3) {
            if (ChatUtil.isStringNumeric(args[2])) {
                if (plugin.getArenaManager().getArenaDataManager().hasArena(Integer.parseInt(args[2]))) {
                    plugin.getArenaManager().getArenaDataManager().deleteArena(Integer.parseInt(args[2]));
                    player.sendMessage(ChatUtil.addColor("&cDungeon #" + args[2] + " has been deleted!"));
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. Could not find dungeon!"));
                }
            } else {
                player.sendMessage(ChatUtil.addColor("&cError. Please enter a valid ID."));
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments."));
        }
    }

    private void join(Player player, String[] args) {
        if (args.length >= 3) {
            if (plugin.getArenaManager().getArenaDataManager().hasArena(Integer.parseInt(args[2]))) {

                player.sendMessage("Join command is under work!");

            } else {
                player.sendMessage(ChatUtil.addColor("&cError. Could not find dungeon!"));
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments."));
        }
    }

    // /dgs arena create <displayname>
    private void create(Player player, String[] args) {
        if (args.length >= 4) {
            //TODO Check if map exists
            if (plugin.getMapManager().hasMap(args[3])) {
                int ID = plugin.getArenaManager().saveArena(args[2], args[3]);
                player.sendMessage(ChatUtil.addColor("&eA new instance has been created with ID: " + ID + "!"));

            } else {
                player.sendMessage(ChatUtil.addColor("&cError. Could not find map!"));
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments!"));
        }
    }

    // Returns a list of dungeons from config but doesn't necessarily mean they're running...
    private void list(Player player, String[] args) {
        if (args.length >= 1) {
            List<String> arenas = plugin.getArenaManager().getArenaDataManager().getArenaList();
            player.sendMessage(ChatUtil.addColor("&bDungeons:"));
            if (arenas != null) {
                for (String ID : arenas) {
                    BaseComponent dungeonIdInfoComponent = new TextComponent(ChatUtil.addColor("&e#" + ID));

                    BaseComponent dungeonInfoComponent = new TextComponent(ChatUtil.addColor(" &a[?]"));

                    BaseComponent[] dungeonInfoHoverComponent = new BaseComponent[1];
                    dungeonInfoHoverComponent[0] = new TextComponent(ChatUtil.addColor("&eClick here for details on this dungeon."));

                    dungeonInfoComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, dungeonInfoHoverComponent));
                    dungeonInfoComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dgs instance info " + ID));
                    player.spigot().sendMessage(dungeonIdInfoComponent, dungeonInfoComponent);
                }
            }
        }
    }

    private void instanceInfo(Player player, String[] args) {
        if (args.length >= 3) {
            if (ChatUtil.isStringNumeric(args[2])) {
                if (plugin.getArenaManager().hasArena(Integer.parseInt(args[2]))) {
                    player.sendMessage(ChatUtil.addColor("&bInstance Info: "));
                    for (String string : plugin.getArenaManager().getLiveArenaInfo(Integer.parseInt(args[2]))) {
                        player.sendMessage(string);
                    }
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. Could not find dungeon!"));
                }
            } else {
                player.sendMessage(ChatUtil.addColor("&cError. Please enter a valid ID."));
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments."));
        }
    }

    private void info(Player player, String[] args) {
        if (args.length >= 3) {
            if (ChatUtil.isStringNumeric(args[2])) {
                if (plugin.getArenaManager().getArenaDataManager().hasArena(Integer.parseInt(args[2]))) {
                    player.sendMessage(ChatUtil.addColor("&bDungeon Info: "));
                    for (String string : plugin.getArenaManager().getArenaDataManager().getArenaInfo(Integer.parseInt(args[2]))) {
                        player.sendMessage(string);
                    }
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. Could not find dungeon!"));
                }
            } else {
                player.sendMessage(ChatUtil.addColor("&cError. Please enter a valid ID."));
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments."));
        }
    }

    private void setMap(Player player, String[] args) {
        if (args.length >= 4) {
            int ID = Integer.parseInt(args[2]);
            if (plugin.getArenaManager().getArenaDataManager().hasArena(ID)) {
                if (plugin.getMapManager().hasMap(args[3])) {
                    if (plugin.getArenaManager().getArenaDataManager().saveArena(ID, "map", args[3])) {
                        plugin.getArenaManager().getArenaDataManager().saveArena(ID, "enterSpawn", "");
                        plugin.getArenaManager().getArenaDataManager().saveArena(ID, "exitSpawn", "");
                        plugin.getArenaManager().getArenaDataManager().saveArena(ID, "exitPortal.pos1", "");
                        plugin.getArenaManager().getArenaDataManager().saveArena(ID, "exitPortal.pos2", "");
                        player.sendMessage(ChatUtil.addColor("&eDungeon map has been set!"));
                    }
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. Could not find map!"));
                }
            } else {
                player.sendMessage(ChatUtil.addColor("&cError. Could not find dungeon!"));
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments."));
        }
    }

    private boolean checkPlayerWorld(Player player, int ID) {
        String map = plugin.getArenaManager().getArenaDataManager().getArenaMap(ID);
        if (map == null) {
            player.sendMessage(ChatUtil.addColor("&cError. Dungeon does not have a map assigned!"));
            return false;
        } else if (!player.getWorld().getName().equalsIgnoreCase(map)) {
            player.sendMessage(ChatUtil.addColor("&cError. You need to be in the map " + map + "!"));
            return false;
        }
        return true;
    }

    private void spawn(Player player, String[] args) {
        if (args.length >= 4) {
            if (args[2].equalsIgnoreCase("enter") || args[2].equalsIgnoreCase("exit")) {
                int ID = Integer.parseInt(args[3]);
                if (plugin.getArenaManager().getArenaDataManager().hasArena(ID)) {
                    if (args[2].equalsIgnoreCase("enter")) {

                        if (!checkPlayerWorld(player, ID)) {
                            return;
                        }

                        if (plugin.getArenaManager().getArenaDataManager().saveArena(ID, "enterSpawn", player.getLocation().serialize())) {
                            player.sendMessage(ChatUtil.addColor("&eSpawn enter for dungeon #" + ID + " has been set!"));
                        }
                    } else if (args[2].equalsIgnoreCase("exit")) {

                        if (plugin.getArenaManager().getArenaDataManager().getArenaMap(ID) != null) {
                            if (plugin.getArenaManager().getArenaDataManager().getArenaMap(ID).equalsIgnoreCase(player.getWorld().getName())) {
                                player.sendMessage(ChatUtil.addColor("&cError. The exit spawn must be outside of the dungeon world!"));
                                return;
                            }
                        } else {
                            player.sendMessage(ChatUtil.addColor("&cError. Dungeon does not have a map assigned!"));
                            return;
                        }

                        if (plugin.getArenaManager().getArenaDataManager().saveArena(ID, "exitSpawn", player.getLocation().serialize())) {
                            player.sendMessage(ChatUtil.addColor("&eSpawn exit for dungeon #" + ID + " has been set!"));
                        }
                    }
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. Could not find dungeon!"));
                }
            } else {
                player.sendMessage(ChatUtil.addColor("&cError. Please select a spawn &3<enter/exit>&c."));
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments."));
        }
    }

    private void exitPortal(Player player, String[] args) {
        if (args.length >= 3) {
            if (args[1].equalsIgnoreCase("exit-portal")) {
                int ID = Integer.parseInt(args[2]);
                if (plugin.getArenaManager().getArenaDataManager().hasArena(ID)) {

                    if (!checkPlayerWorld(player, ID)) {
                        return;
                    }

                    if (!positionCommand.isPositionsSelected()) {
                        player.sendMessage(ChatUtil.addColor("&cError. Please select the portal positions."));
                        return;
                    }
                    if (plugin.getArenaManager().getArenaDataManager().saveArena(ID, "exitPortal.pos1", positionCommand.getPos1().serialize()) &&
                            plugin.getArenaManager().getArenaDataManager().saveArena(ID, "exitPortal.pos2", positionCommand.getPos2().serialize())) {
                        player.sendMessage(ChatUtil.addColor("&eExit portal for dungeon #" + ID + " has been set!"));
                    }
                    positionCommand.resetPositions();
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. Could not find dungeon!"));
                }
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments."));
        }
    }

    private void liveArenas(Player player, String[] args) {
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("live")) {
                if (plugin.getArenaManager().getArenas().size() > 0 ) {
                    player.sendMessage(ChatUtil.addColor("&bLive Dungeons&7: "));
                    for (Arena arena : plugin.getArenaManager().getArenas()) {

                        BaseComponent dungeonIdInfoComponent = new TextComponent(ChatUtil.addColor("&e#" + arena.getID()));

                        BaseComponent instanceInfoComponent = new TextComponent(ChatUtil.addColor("&c [!]"));
                        instanceInfoComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dgs instance live-info " + arena.getID()));

                        BaseComponent dungeonInfoComponent = new TextComponent(ChatUtil.addColor("&a [?]"));
                        dungeonInfoComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dgs instance info " + arena.getID()));

                        player.spigot().sendMessage(dungeonIdInfoComponent, instanceInfoComponent, dungeonInfoComponent);
                    }
                } else {
                    player.sendMessage(ChatUtil.addColor("&cNo dungeons are currently running."));
                }
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments."));
        }
    }

    private void printHelp(Player sender) {
        sender.sendMessage(ChatUtil.addColor("\n&aDungeons v1.0 - Instance Commands"));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons instance create &3<displayName> &3<map/none>&7:&e Create a new dungeon."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons instance delete &3<ID>&7:&e Delete a dungeon using ID."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons instance reload &7:&e Reloads dungeons & triggers config."));

        sender.sendMessage(ChatUtil.addColor("&b/dungeons instance set-map &3<ID> &3<map>&7:&e Set dungeon map."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons instance exit-portal &3<ID>&7:&e Set the dungeon portal to trigger portal completion."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons instance spawn &3<enter/exit> &3<ID>&7:&e Set the dungeon start and end spawns."));

        sender.sendMessage(ChatUtil.addColor("&b/dungeons instance trigger &3<ID> &3<name>&7:&e Add a trigger."));

        sender.sendMessage(ChatUtil.addColor("&b/dungeons instance edit &3<ID>&7:&e Edit dungeon"));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons instance info &3<ID>&7:&e Get dungeon info."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons instance list&7:&e List all dungeons."));

        sender.sendMessage(ChatUtil.addColor("&b/dungeons instance live&7:&e Get all live instances."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons instance live-info &3<ID>&7:&e Get an instance live info."));
    }
}
