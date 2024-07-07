package ru.fennec.free.duckhunters.common.events;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;

public class GamePlayerMoveEvent extends Event implements Cancellable {
    private IGamePlayer gamePlayer;
    private Location to;
    private Location from;
    private boolean cancelled;

    private static final HandlerList handlerList = new HandlerList();

    public GamePlayerMoveEvent(IGamePlayer gamePlayer, Location to, Location from) {
        this.gamePlayer = gamePlayer;
        this.to = to;
        this.from = from;
    }

    public IGamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public Location getTo() {
        return to;
    }

    public Location getFrom() {
        return from;
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
