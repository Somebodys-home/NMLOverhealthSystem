package io.github.NoOne.nMLOverhealthSystem;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class OverhealthListener implements Listener {
    private final NMLPlayerStats nmlPlayerStats;
    private final ProfileManager profileManager;
    private final OverhealthManager overhealthManager;

    public OverhealthListener(NMLOverhealthSystem nmlOverhealthSystem) {
        this.nmlPlayerStats = nmlOverhealthSystem.getNmlPlayerStats();
        this.profileManager = nmlOverhealthSystem.getProfileManager();
        this.overhealthManager = nmlOverhealthSystem.getOverhealthManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        overhealthManager.add2OverhealthMap(player);
        overhealthManager.updateOverhealthFromProfile(player);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            double prevOverhealth = profileManager.getPlayerProfile(player.getUniqueId()).getStats().getCurrentOverhealth();

            Bukkit.getScheduler().runTask(nmlPlayerStats, () -> {
                double newOverhealth = player.getAbsorptionAmount();
                Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();

                stats.setCurrentOverhealth(newOverhealth);
                Bukkit.getPluginManager().callEvent(new OverhealthChangeEvent(player, prevOverhealth, newOverhealth));
                overhealthManager.add2OverhealthMap(player);
            });
        }
    }

    @EventHandler
    public void updateOverhealthVisually(OverhealthChangeEvent event) {
        Player player = event.getPlayer();
        player.setAbsorptionAmount(event.getNewOverhealth());
    }
}