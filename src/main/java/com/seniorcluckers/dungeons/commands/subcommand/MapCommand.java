package com.seniorcluckers.dungeons.commands.subcommand;

import com.seniorcluckers.dungeons.Dungeons;
import com.seniorcluckers.dungeons.commands.SubCommand;
import com.seniorcluckers.dungeons.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class MapCommand extends SubCommand {

    private final Dungeons plugin;

    public MapCommand(Dungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "map";
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
        if (!player.hasPermission("dungeons.map")) {
            player.sendMessage(ChatUtil.addColor("&cError. You do not have permission."));
            return;
        }

        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("list")) {
                listMap(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("tp")) {
                tpMap(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("unload")) {
                unloadMap(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("load")) {
                loadMap(player, args);
                return;
            }

            if (args[1].equalsIgnoreCase("refresh")) {
                refreshMaps(player, args);
                return;
            }

            player.sendMessage(ChatUtil.addColor("&cError. Command not found."));
        } else {
            printHelp(player);
        }
    }

    private void tpMap(Player player, String[] args) {
        if (args.length >= 3) {
            if (args[1].equalsIgnoreCase("tp")) {
                World world = Bukkit.getWorld(args[2]);
                if (world != null) {
                    player.teleport(world.getSpawnLocation());
                    player.sendMessage(ChatUtil.addColor("&eTeleporting you to map " + args[2] + "."));
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. Map doesn't seem to be loaded."));
                }
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments!"));
        }
    }

    private void listMap(Player player, String[] args) {
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("list")) {
                player.sendMessage(ChatUtil.addColor("&bMaps&7:"));
                for (String map : plugin.getMapManager().getMaps()) {
                    player.sendMessage(ChatUtil.addColor("&e" + map));
                }
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments!"));
        }
    }

    public void unloadMap(Player player, String[] args) {
        if (args.length >= 3) {
            if (args[1].equalsIgnoreCase("unload")) {
                if (plugin.getMapManager().hasMap(args[2])) {
                    if (plugin.getMapManager().unloadMap(args[2])) {
                        player.sendMessage(ChatUtil.addColor("&eMap has been unloaded!"));
                    } else {
                        player.sendMessage(ChatUtil.addColor("&cError. Map not loaded!"));
                    }
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. Could not find map!"));
                }
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments!"));
        }
    }

    public void loadMap(Player player, String[] args) {
        if (args.length >= 3) {
            if (args[1].equalsIgnoreCase("load")) {
                if (plugin.getMapManager().hasMap(args[2])) {
                    player.sendMessage(ChatUtil.addColor("&cCopying and loading map for dungeon set-up..."));
                    plugin.getMapManager().copyMap(args[2], player);
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. Could not find map!"));
                }
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments!"));
        }
    }

    public void refreshMaps(Player player, String[] args) {
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("refresh")) {
                plugin.getMapManager().findMaps();
                player.sendMessage(ChatUtil.addColor("&eMaps refreshed!"));
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments!"));
        }
    }

    public void printHelp(Player sender) {
        sender.sendMessage(ChatUtil.addColor("\n&aDungeons v1.0 - Map Commands"));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons map load &3<map>&7:&e Load a map."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons map unload &3<map>&7:&e Unload a map."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons map tp &3<map>&7:&e Teleport to map."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons map list&7:&e List all maps."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons map refresh&7:&e Refresh the maps list."));
    }
}
