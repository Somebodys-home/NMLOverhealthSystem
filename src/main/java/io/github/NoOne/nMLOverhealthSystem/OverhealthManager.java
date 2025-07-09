package io.github.NoOne.nMLOverhealthSystem;

import io.github.NoOne.nMLPlayerStats.profileSystem.Profile;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLPlayerStats.statSystem.StatChangeEvent;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OverhealthManager {
    private final NMLOverhealthSystem nmlOverhealthSystem;
    private final ProfileManager profileManager;
    private final Map<UUID, Long> overhealthRegenMap = new HashMap<>();
    private final Map<UUID, Integer> activeRegenTasks = new HashMap<>();

    public OverhealthManager(NMLOverhealthSystem nmlOverhealthSystem) {
        this.nmlOverhealthSystem = nmlOverhealthSystem;
        profileManager = nmlOverhealthSystem.getProfileManager();
    }

    public void add2OverhealthMap(Player player) {
        overhealthRegenMap.put(player.getUniqueId(), System.currentTimeMillis());
        cancelRegenTask(player.getUniqueId());
    }

    public void ovehealthRegenServerTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    long last = overhealthRegenMap.getOrDefault(uuid, 0L);

                    if (currentTime - last >= 3000) {
                        if (!activeRegenTasks.containsKey(uuid)) {
                            overhealthRegen(player);
                        }
                        overhealthRegenMap.put(uuid, currentTime);
                    }
                }
            }
        }.runTaskTimer(nmlOverhealthSystem, 0L, 20L);
    }

    public void overhealthRegen(Player player) {
        UUID uuid = player.getUniqueId();

        int taskId = new BukkitRunnable() {
            @Override
            public void run() {
                Stats stats = profileManager.getPlayerProfile(uuid).getStats();
                double maxOverhealth = stats.getMaxOverhealth();
                double currentOverhealth = stats.getCurrentOverhealth();
                double newOverhealth;

                if (currentOverhealth >= maxOverhealth) {
                    cancelRegenTask(uuid);
                    return;
                }
                // number determines the seconds it takes to regen to max overhealth
                newOverhealth = Math.min(currentOverhealth + (maxOverhealth / 15), maxOverhealth);
                stats.setCurrentOverhealth(newOverhealth);

                Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, "overhealth"));
                player.setAbsorptionAmount(newOverhealth);

                if (newOverhealth >= maxOverhealth) {
                    cancelRegenTask(uuid);
                }
            }
        }.runTaskTimer(nmlOverhealthSystem, 0L, 20L).getTaskId();

        activeRegenTasks.put(uuid, taskId);
    }

    private void cancelRegenTask(UUID uuid) {
        Integer taskId = activeRegenTasks.get(uuid);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
            activeRegenTasks.remove(uuid);
        }
    }

    public void updateOverhealthFromProfile(Player player) {
        Profile profile = profileManager.getPlayerProfile(player.getUniqueId());
        double maxOverhealth = profile.getStats().getMaxOverhealth();
        double currentOverhealth = Math.min(profile.getStats().getCurrentOverhealth(), maxOverhealth);

        profile.getStats().setCurrentOverhealth(currentOverhealth);
        player.getAttribute(Attribute.GENERIC_MAX_ABSORPTION).setBaseValue(maxOverhealth);
        Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, "overhealth"));
    }
}