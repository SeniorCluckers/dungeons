package com.seniorcluckers.dungeons;

import com.seniorcluckers.dungeons.arena.ArenaManager;
import com.seniorcluckers.dungeons.arena.listeners.ArenaListener;
import com.seniorcluckers.dungeons.commands.DungeonCommand;
import com.seniorcluckers.dungeons.map.MapManager;
import com.seniorcluckers.dungeons.party.PartyManager;
import com.seniorcluckers.dungeons.portal.PortalManager;
import com.seniorcluckers.dungeons.portal.listeners.PlayerTriggerPortal;
import com.seniorcluckers.dungeons.trigger.TriggerDataManager;
import com.seniorcluckers.dungeons.utils.MessagesDataManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Dungeons extends JavaPlugin {

    private PortalManager portalManager;
    private ArenaManager arenaManager;
    private MapManager mapManager;
    private PartyManager partyManager;

    private TriggerDataManager triggerDataManager;
    private MessagesDataManager messageDataManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        triggerDataManager = new TriggerDataManager(this);
        messageDataManager = new MessagesDataManager(this);

        arenaManager = new ArenaManager(this);
        portalManager = new PortalManager(this);
        mapManager = new MapManager(this);
        partyManager = new PartyManager(this);

        this.getServer().getPluginManager().registerEvents(new PlayerTriggerPortal(this), this);
        this.getServer().getPluginManager().registerEvents(new ArenaListener(this), this);

        this.getCommand("dungeons").setExecutor(new DungeonCommand(this));
    }

    @Override
    public void onDisable() {

    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public PortalManager getPortalManager() {
        return portalManager;
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public MessagesDataManager getMessageDataManager() {
        return messageDataManager;
    }

    public TriggerDataManager getTriggerDataManager() {
        return triggerDataManager;
    }
}
