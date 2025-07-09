package io.github.NoOne.nMLOverhealthSystem;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLOverhealthSystem extends JavaPlugin {
    private NMLOverhealthSystem instance;
    private NMLPlayerStats nmlPlayerStats;
    private ProfileManager profileManager;
    private OverhealthManager overhealthManager;

    @Override
    public void onEnable() {
        instance = this;

        Plugin plugin = Bukkit.getPluginManager().getPlugin("NMLPlayerStats");
        if (plugin instanceof NMLPlayerStats statsPlugin) {
            nmlPlayerStats = statsPlugin;
            profileManager = nmlPlayerStats.getProfileManager();
        } else {
            getLogger().severe("Failed to find NMLPlayerStats! Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        overhealthManager = new OverhealthManager(this);
        overhealthManager.ovehealthRegenServerTask();

        getServer().getPluginManager().registerEvents(new OverhealthListener(this), this);
        getCommand("setMaxOverhealth").setExecutor(new SetMaxOverhealthCommand(instance));
    }

    public NMLOverhealthSystem getInstance() {
        return instance;
    }

    public NMLPlayerStats getNmlPlayerStats() {
        return nmlPlayerStats;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public OverhealthManager getOverhealthManager() {
        return overhealthManager;
    }
}