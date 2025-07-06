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
    private ProfileManager profileManager;
    private OverhealthManager overhealthManager;

    public OverhealthListener(NMLOverhealthSystem nmlOverhealthSystem) {
        this.nmlPlayerStats = nmlOverhealthSystem.getNmlPlayerStats();
        profileManager = nmlOverhealthSystem.getProfileManager();
        overhealthManager = nmlOverhealthSystem.getOverhealthManager();
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            double absorptionBefore = player.getAbsorptionAmount();

            overhealthManager.add2OverhealthMap(player);

            Bukkit.getScheduler().runTask(nmlPlayerStats, () -> {
                double absorptionAfter = player.getAbsorptionAmount();
                OverhealthChangeEvent overhealthChangeEvent = new OverhealthChangeEvent(player, absorptionBefore, absorptionAfter);

                Bukkit.getPluginManager().callEvent(overhealthChangeEvent);
            });
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        overhealthManager.add2OverhealthMap(player);
    }

    @EventHandler
    public void overhealth(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        double maxOverhealth = profileManager.getPlayerProfile(player.getUniqueId()).getStats().getMaxOverhealth();

        profileManager.updateStatsFromProfile(player);
    }

    @EventHandler
    public void updateOverhealth(OverhealthChangeEvent event) {
        Player player = event.getPlayer();
        Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();

        player.setAbsorptionAmount(event.getNewOverhealth());
        stats.setCurrentOverhealth(event.getNewOverhealth());
        profileManager.getPlayerProfile(player.getUniqueId()).setStats(stats);
        profileManager.updateStatsFromProfile(player);
    }
}
