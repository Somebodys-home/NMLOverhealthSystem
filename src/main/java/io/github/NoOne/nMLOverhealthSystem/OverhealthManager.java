package io.github.NoOne.nMLOverhealthSystem;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OverhealthManager {
    private final NMLPlayerStats nmlPlayerStats;
    private final ProfileManager profileManager;
    private final Map<UUID, Long> overhealthRegenMap = new HashMap<>();
    private final Map<UUID, Integer> activeRegenTasks = new HashMap<>(); // Track active regeneration tasks

    public OverhealthManager(NMLPlayerStats nmlPlayerStats) {
        this.nmlPlayerStats = nmlPlayerStats;
        profileManager = nmlPlayerStats.getProfileManager();
    }

    public void add2OverhealthMap(Player player) {
        overhealthRegenMap.put(player.getUniqueId(), System.currentTimeMillis());

        // Cancel any existing regeneration task for this player
        cancelRegenTask(player.getUniqueId());
    }

    public void startOverhealthTracker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    long last = overhealthRegenMap.getOrDefault(uuid, 0L);

                    if (currentTime - last >= 3000) {
                        // Only start regeneration if not already regenerating
                        if (!activeRegenTasks.containsKey(uuid)) {
                            overhealthRegen(player);
                        }
                        overhealthRegenMap.put(uuid, currentTime); // update timestamp
                    }
                }
            }
        }.runTaskTimer(nmlPlayerStats, 0L, 20L); // 1 second
    }

    public void overhealthRegen(Player player) {
        Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();
        double maxOverhealth = stats.getMaxOverhealth();
        double currentOverhealth = stats.getCurrentOverhealth();
        UUID uuid = player.getUniqueId();

        if (currentOverhealth >= maxOverhealth) {
            return;
        }

        int taskId = new BukkitRunnable() {
            @Override
            public void run() {
                Stats currentStats = profileManager.getPlayerProfile(uuid).getStats();
                double current = currentStats.getCurrentOverhealth();
                double max = currentStats.getMaxOverhealth();

                if (current >= max) {
                    cancelRegenTask(uuid);
                    return;
                }

                OverhealthChangeEvent overhealthChangeEvent = new OverhealthChangeEvent(player, current, current + (max / 15));
                Bukkit.getPluginManager().callEvent(overhealthChangeEvent);
            }
        }.runTaskTimer(nmlPlayerStats, 0L, 20L).getTaskId(); // 20 ticks = 1 second

        activeRegenTasks.put(uuid, taskId);
    }

    private void cancelRegenTask(UUID uuid) {
        if (activeRegenTasks.containsKey(uuid)) {
            Bukkit.getScheduler().cancelTask(activeRegenTasks.get(uuid));
            activeRegenTasks.remove(uuid);
        }
    }
}