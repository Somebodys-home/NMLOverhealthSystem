package io.github.NoOne.nMLOverhealthSystem;

import io.github.NoOne.nMLPlayerStats.profileSystem.Profile;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
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
    private final Map<UUID, Integer> activeRegenTasks = new HashMap<>(); // Track active regeneration tasks

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
                long currentTime = System.currentTimeMillis(); // gets the current server time

                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    long last = overhealthRegenMap.getOrDefault(uuid, 0L); // gets the time on the overhealth regen map

                    if (currentTime - last >= 3000) { // if it's been 3 seconds
                        if (!activeRegenTasks.containsKey(uuid)) { // and they're not yet regenerating overhealth
                            overhealthRegen(player); // start overhealth regen
                        }
                        overhealthRegenMap.put(uuid, currentTime); // and update their time on the overhealth regen map
                    }
                }
            }
        }.runTaskTimer(nmlOverhealthSystem, 0L, 20L); // 1 second
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
                boolean done = false;

                if (currentOverhealth > maxOverhealth) {
                    newOverhealth = maxOverhealth;
                    done = true;
                } else {
                    newOverhealth = Math.min(currentOverhealth + (maxOverhealth / 15), maxOverhealth);
                }

                if (done) {
                    cancelRegenTask(uuid);
                }

                Bukkit.getPluginManager().callEvent(new OverhealthChangeEvent(player, currentOverhealth, newOverhealth));
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

        player.setAbsorptionAmount(currentOverhealth);
        player.getAttribute(Attribute.GENERIC_MAX_ABSORPTION).setBaseValue(maxOverhealth);
    }
}