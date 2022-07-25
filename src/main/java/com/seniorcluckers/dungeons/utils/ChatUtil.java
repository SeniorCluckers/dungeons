package com.seniorcluckers.dungeons.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtil {

    public static String addColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String convertMessage(Player player, String string) {
        return addColor(string.replaceAll("%PLAYER%", player.getName()));
    }

    public static boolean isStringNumeric(String value) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

}
