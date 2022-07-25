package com.seniorcluckers.dungeons.utils;

import com.seniorcluckers.dungeons.Dungeons;

public class MessagesDataManager extends DataManager {

    public MessagesDataManager(Dungeons plugin) {
        super(plugin, "messages.yml");
    }

    public String getMessage(String path) {
        return getConfig().getString(path);
    }

}
