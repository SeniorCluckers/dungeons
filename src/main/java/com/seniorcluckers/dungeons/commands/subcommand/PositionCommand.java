package com.seniorcluckers.dungeons.commands.subcommand;

import com.seniorcluckers.dungeons.commands.SubCommand;
import com.seniorcluckers.dungeons.utils.ChatUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PositionCommand extends SubCommand {

    private Location pos1, pos2;

    @Override
    public String getName() {
        return "pos";
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
        if (!player.hasPermission("dungeons.position")) {
            player.sendMessage(ChatUtil.addColor("&cError. You do not have permission."));
            return;
        }

        if (args[0].equalsIgnoreCase("pos")) {
            position(player, args);
            return;
        }
    }

    private void position(Player player, String[] args) {
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("1") || args[1].equalsIgnoreCase("2")) {
                if (player.getTargetBlockExact(5) != null) {

                    // Why can you compare with double == ?
                    // player.getTargetBlockExact(5).getLocation() == pos2
                    if (player.getTargetBlockExact(5).getLocation().equals(pos1) || player.getTargetBlockExact(5).getLocation().equals(pos2)) {
                        player.sendMessage(ChatUtil.addColor("&eBlock already selected as position " + args[1] + "."));
                        return;
                    }

                    if (args[1].equalsIgnoreCase("1")) {
                        pos1 = player.getTargetBlockExact(5).getLocation();
                        player.sendMessage(ChatUtil.addColor("&ePosition 1 set to (" + pos1.getBlockX() + ", " + pos1.getBlockY() + ", " + pos1.getBlockZ() + ")."));
                    } else if (args[1].equalsIgnoreCase("2")) {
                        pos2 = player.getTargetBlockExact(5).getLocation();
                        player.sendMessage(ChatUtil.addColor("&ePosition 2 set to (" + pos2.getBlockX() + ", " + pos2.getBlockY() + ", " + pos2.getBlockZ() + ")."));
                    }
                } else {
                    player.sendMessage(ChatUtil.addColor("&cError. You must be looking directly at a block."));
                }
            } else {
                player.sendMessage(ChatUtil.addColor("&cError. Please select a position &3<1/2>&c."));
            }
        } else {
            player.sendMessage(ChatUtil.addColor("&cError. Not enough arguments!"));
        }
    }

    protected boolean isPositionsSelected() {
        return pos1 != null && pos2 != null;
    }

    protected void resetPositions() {
        pos1 = null;
        pos2 = null;
    }

    protected Location getPos1() {
        return pos1;
    }

    protected Location getPos2() {
        return pos2;
    }
}
