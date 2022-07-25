package com.seniorcluckers.dungeons.commands.subcommand;

import com.seniorcluckers.dungeons.Dungeons;
import com.seniorcluckers.dungeons.arena.ArenaState;
import com.seniorcluckers.dungeons.arena.models.Arena;
import com.seniorcluckers.dungeons.commands.SubCommand;
import com.seniorcluckers.dungeons.utils.ChatUtil;
import com.seniorcluckers.dungeons.utils.MessagesDataManager;
import org.bukkit.entity.Player;

public class LeaveCommand extends SubCommand {

    private final Dungeons plugin;
    private final MessagesDataManager messagesDataManager;

    public LeaveCommand(Dungeons plugin) {
        this.plugin = plugin;
        this.messagesDataManager = plugin.getMessageDataManager();
    }

    @Override
    public String getName() {
        return "leave";
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
        if (!player.hasPermission("dungeons.leave")) {
            player.sendMessage(ChatUtil.addColor("&cError. You do not have permission."));
            return;
        }

        if (plugin.getArenaManager().hasPlayer(player)) {
            Arena arena = plugin.getArenaManager().getPlayer(player);
            if (arena.getState() == ArenaState.WAITING || arena.getState() == ArenaState.OPEN) {
                plugin.getArenaManager().removePlayer(player);
                player.sendMessage(ChatUtil.convertMessage(player, messagesDataManager.getMessage("leave")));
            } else {
                player.sendMessage(ChatUtil.convertMessage(player, messagesDataManager.getMessage("leave-error")));
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. You're not in a dungeon!"));
        }
    }

}
