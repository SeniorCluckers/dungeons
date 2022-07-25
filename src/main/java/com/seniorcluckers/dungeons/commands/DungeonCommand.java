package com.seniorcluckers.dungeons.commands;

import com.seniorcluckers.dungeons.Dungeons;
import com.seniorcluckers.dungeons.commands.subcommand.*;
import com.seniorcluckers.dungeons.utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DungeonCommand implements CommandExecutor, TabCompleter {

    private final Dungeons plugin;

    private Map<String, SubCommand> subCommands = new HashMap<>();

    public DungeonCommand(Dungeons plugin) {
        this.plugin = plugin;
        PositionCommand positionCommand = new PositionCommand();

        subCommands.put("portal", new PortalCommand(plugin, positionCommand));
        subCommands.put("instance", new ArenaCommand(plugin, positionCommand));
        subCommands.put("map", new MapCommand(plugin));
        subCommands.put("pos", positionCommand);
        subCommands.put("party", new PartyCommand(plugin));
        subCommands.put("leave", new LeaveCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatUtil.addColor("&cYou must be a player to use this command!"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("dungeons.main")) {
            player.sendMessage(ChatUtil.addColor("&cError. You do not have permission."));
            return true;
        }

        if (command.getName().equalsIgnoreCase("dungeons")) {
             if (args.length > 0) {
                    if (subCommands.containsKey(args[0])) {
                        subCommands.get(args[0]).perform(player, args);
                        return true;
                    } else {
                        player.sendMessage(ChatUtil.addColor("&cError. Command not found."));
                    }
                } else {
                    sender.sendMessage(ChatUtil.addColor("\n&aDungeons v1.0"));
                    printHelp(player);

                    if (player.hasPermission("dungeons.admin")) {
                        printHelpAdmin(player);
                    }
                    return true;
                }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> subCommands = new ArrayList<>();

        if (sender.hasPermission("dungeons.admin")) {
            if (args.length == 1) {
                subCommands.add("instance");
                subCommands.add("map");
                subCommands.add("portal");
                subCommands.add("pos");
                subCommands.add("party");
                return subCommands;
            }
        } else if (sender.hasPermission("dungeons.leave")) {
            subCommands.add("leave");
            return subCommands;
        }
        return null;
    }

    public void printHelp(Player sender) {
        sender.sendMessage(ChatUtil.addColor("&b/dungeons leave&7:&e Leave the current dungeon."));
    }

    public void printHelpAdmin(Player sender) {
        sender.sendMessage(ChatUtil.addColor("&b/dungeons pos&3 <1/2>&7:&e Set position to the targeted block."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons portal&7:&e All related portal commands."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons instance&7:&e All related instance commands."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons map&7:&e All related map commands."));
        sender.sendMessage(ChatUtil.addColor("&b/dungeons party&7:&e All related party commands."));
    }
}
