package ru.fennec.free.duckhunters.common.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;

public class GamePlayerDeathEvent extends Event implements Cancellable {
    private IGamePlayer gamePlayer;
    private IGamePlayer killer;
    private boolean cancelled;
    private EntityDamageEvent.DamageCause damageCause;
    private boolean keepInventory;

    private static final HandlerList handlerList = new HandlerList();

    public GamePlayerDeathEvent(IGamePlayer gamePlayer, IGamePlayer killer, EntityDamageEvent.DamageCause damageCause) {
        this.gamePlayer = gamePlayer;
        this.killer = killer;
        this.damageCause = damageCause;
        this.cancelled = false;
        this.keepInventory = false;
    }

    public IGamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public IGamePlayer getKiller() {
        return killer;
    }

    public boolean isKeepInventory() {
        return keepInventory;
    }

    public EntityDamageEvent.DamageCause getDamageCause() {
        return damageCause;
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

    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }
}
