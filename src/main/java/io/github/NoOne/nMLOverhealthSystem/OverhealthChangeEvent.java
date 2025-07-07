package io.github.NoOne.nMLOverhealthSystem;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class OverhealthChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final double previousOverhealth;
    private final double newOverhealth;

    public OverhealthChangeEvent(@NotNull Player player, double previousOverhealth, double newOverhealth) {
        this.player = player;
        this.previousOverhealth = previousOverhealth;
        this.newOverhealth = newOverhealth;
    }

    @Override
    public HandlerList getHandlers() { return handlers; }

    public static HandlerList getHandlerList() { return handlers; }

    public Player getPlayer() { return player; }

    public double getPreviousOverhealth() {
        return previousOverhealth;
    }

    public double getNewOverhealth() {
        return newOverhealth;
    }
}
