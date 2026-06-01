package io.github.NoOne.nMLOverhealthSystem;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLOverhealthSystem extends JavaPlugin {
    private ProfileManager profileManager;
    private OverhealthManager overhealthManager;

    @Override
    public void onEnable() {
        profileManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getProfileManager();

        overhealthManager = new OverhealthManager(this);
        overhealthManager.overhealthRegenServerTask();

        getServer().getPluginManager().registerEvents(new OverhealthListener(this), this);
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public OverhealthManager getOverhealthManager() {
        return overhealthManager;
    }
}