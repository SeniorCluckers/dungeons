package com.seniorcluckers.dungeons.trigger;

import com.seniorcluckers.dungeons.Dungeons;
import com.seniorcluckers.dungeons.arena.models.Arena;
import com.seniorcluckers.dungeons.utils.Cuboid;
import com.seniorcluckers.dungeons.utils.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class TriggerDataManager extends DataManager {

    public TriggerDataManager(Dungeons plugin) {
        super(plugin, "triggers.yml");
    }

    public void addTrigger(int ID, String name, Location pos1, Location pos2) {
        getConfig().set("dungeons." + ID + "." + name, new Trigger(new Cuboid(pos1, pos2)).serialize());
        saveConfig();
        reloadConfig();
    }

    public Set<Trigger> createTriggers(int ID, Arena arena) {
        ConfigurationSection dungeonTriggers = getConfig().getConfigurationSection("dungeons." + ID);
        if (dungeonTriggers != null) {
            Set<Trigger> triggers = new HashSet<>();
            for (String key : dungeonTriggers.getKeys(false)) {
                if (isTriggerSetup(dungeonTriggers.getConfigurationSection(key))) {
                    ConfigurationSection trigger = dungeonTriggers.getConfigurationSection(key);

                    TriggerType triggerType = TriggerType.equalsIgnoreCase(trigger.getString("type"));
                    int uses = trigger.getInt("uses");
                    Cuboid cuboid = new Cuboid(createLocation(trigger.getConfigurationSection("bounds.pos1"), ID), createLocation(trigger.getConfigurationSection("bounds.pos2"), ID));
                    List<String> commands = trigger.getStringList("commands");

                    Bukkit.getLogger().log(Level.SEVERE, triggerType.name() + uses + cuboid.toString() + commands.toString());

                    triggers.add(new Trigger(triggerType, uses, cuboid, commands, arena));
                }
            }
            return triggers;
        }
        return null;
    }

    private Location createLocation(ConfigurationSection section, int ID) {
        String world = section.getString("world") + "_" + ID;
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        float yaw = (float) section.getDouble("yaw");
        float pitch = (float) section.getDouble("pitch");

        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public boolean isTriggerSetup(ConfigurationSection trigger) {
//        TriggerType triggerType = TriggerType.valueOf(trigger.getString("type"));
//        int uses = trigger.getInt("uses");
//        trigger.getConfigurationSection("bounds.1");
//        List<String> commands = trigger.getStringList("commands");

        return true;
    }

}
