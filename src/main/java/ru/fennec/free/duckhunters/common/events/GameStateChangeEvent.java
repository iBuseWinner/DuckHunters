package ru.fennec.free.duckhunters.common.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.fennec.free.duckhunters.handlers.enums.GameState;

public class GameStateChangeEvent extends Event implements Cancellable {
    private GameState from, to;
    private boolean cancelled;

    private static final HandlerList handlerList = new HandlerList();

    public GameStateChangeEvent(GameState from, GameState to) {
        this.from = from;
        this.to = to;
        this.cancelled = false;
    }

    public GameState getFrom() {
        return from;
    }

    public GameState getTo() {
        return to;
    }

    public void setFrom(GameState from) {
        this.from = from;
    }

    public void setTo(GameState to) {
        this.to = to;
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
