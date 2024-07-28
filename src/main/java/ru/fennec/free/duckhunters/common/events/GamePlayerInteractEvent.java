package ru.fennec.free.duckhunters.common.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;
import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;

public class GamePlayerInteractEvent extends Event implements Cancellable {
    private IGamePlayer gamePlayer;
    private Action action;
    private boolean cancelled;

    private static final HandlerList handlerList = new HandlerList();

    public GamePlayerInteractEvent(IGamePlayer gamePlayer, Action action) {
        this.gamePlayer = gamePlayer;
        this.action = action;
    }

    public IGamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
