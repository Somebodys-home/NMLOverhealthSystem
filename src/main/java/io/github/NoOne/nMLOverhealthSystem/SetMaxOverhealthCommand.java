package io.github.NoOne.nMLOverhealthSystem;

import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLPlayerStats.statSystem.StatChangeEvent;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetMaxOverhealthCommand implements CommandExecutor {
    private ProfileManager profileManager;

    public SetMaxOverhealthCommand(NMLOverhealthSystem nmlOverhealthSystem) {
        profileManager = nmlOverhealthSystem.getProfileManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();
            double prevMaxOverhealth = stats.getMaxOverhealth();
            double newMaxOverhealth = Double.parseDouble(args[0]);

            Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, "maxoverhealth", newMaxOverhealth - prevMaxOverhealth));
            player.sendMessage("set max overhealth to " + newMaxOverhealth);
        }

        return true;
    }
}
