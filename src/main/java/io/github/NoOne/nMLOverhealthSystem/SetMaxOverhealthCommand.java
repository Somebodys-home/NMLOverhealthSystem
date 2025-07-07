package io.github.NoOne.nMLOverhealthSystem;

import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetMaxOverhealthCommand implements CommandExecutor {
    private ProfileManager profileManager;
    private OverhealthManager overhealthManager;

    public SetMaxOverhealthCommand(NMLOverhealthSystem nmlOverhealthSystem) {
        profileManager = nmlOverhealthSystem.getProfileManager();
        overhealthManager = nmlOverhealthSystem.getOverhealthManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();
            double newOverhealth = Double.parseDouble(args[0]);

            stats.setMaxOverhealth(newOverhealth);
            overhealthManager.updateOverhealthFromProfile(player);
            player.sendMessage("set max overhealth to " + newOverhealth);
        }

        return true;
    }
}
