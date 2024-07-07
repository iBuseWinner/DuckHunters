package ru.fennec.free.duckhunters.common.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;

public class GamePlayerQuitEvent extends Event {
    private IGamePlayer gamePlayer;

    private static final HandlerList handlerList = new HandlerList();

    public GamePlayerQuitEvent(IGamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public IGamePlayer getGamePlayer() {
        return gamePlayer;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
