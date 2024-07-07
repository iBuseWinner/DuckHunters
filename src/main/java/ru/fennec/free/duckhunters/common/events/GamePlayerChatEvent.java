package ru.fennec.free.duckhunters.common.events;

import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;

public class GamePlayerChatEvent extends Event implements Cancellable {
    private IGamePlayer gamePlayer;
    private Component message;
    private boolean cancelled;

    private static final HandlerList handlerList = new HandlerList();

    public GamePlayerChatEvent(IGamePlayer gamePlayer, Component message) {
        this.gamePlayer = gamePlayer;
        this.message = message;
        this.cancelled = false;
    }

    public IGamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public Component getMessage() {
        return message;
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

    public void setMessage(Component message) {
        this.message = message;
    }
}
