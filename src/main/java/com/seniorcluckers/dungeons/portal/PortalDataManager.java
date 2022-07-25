package com.seniorcluckers.dungeons.portal;

import com.seniorcluckers.dungeons.Dungeons;
import com.seniorcluckers.dungeons.utils.DataManager;

public class PortalDataManager extends DataManager {

    private final Dungeons plugin;

    public PortalDataManager(Dungeons plugin) {
        super(plugin, "portals.yml");
        this.plugin = plugin;
    }

    public void savePortal(Portal portal) {
        getConfig().set("portals." + portal.getID(), portal.serialize());
        saveConfig();
    }

    public void deletePortal(Portal portal) {
        getConfig().set("portals." + portal.getID(), null);
        saveConfig();
    }
}
