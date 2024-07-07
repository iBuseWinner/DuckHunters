package ru.fennec.free.duckhunters.common.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;

public class GamePlayerGetDamageEvent extends Event implements Cancellable {
    private IGamePlayer player;
    private IGamePlayer damager;
    private boolean cancelled;

    private static final HandlerList handlerList = new HandlerList();

    public GamePlayerGetDamageEvent(IGamePlayer gamePlayer, IGamePlayer killer) {
        this.player = gamePlayer;
        this.damager = killer;
        this.cancelled = false;
    }

    public IGamePlayer getGamePlayer() {
        return player;
    }

    public IGamePlayer getDamager() {
        return damager;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
