package ru.fennec.free.duckhunters.common.interfaces;

import org.bukkit.Location;

import java.io.File;
import java.util.Set;
import java.util.stream.Stream;

public interface IGame {

    int getTime();

    void decrementTime(int seconds);

    Location getMinArenaLocation();

    Location getMaxArenaLocation();

    Location getMinWaitingLobbyLocation();

    Location getMaxWaitingLobbyLocation();

    Set<IGamePlayer> getPlayers();

    Stream<IGamePlayer> getAlivePlayers();

    boolean addPlayer(IGamePlayer gamePlayer);

    boolean removePlayer(IGamePlayer gamePlayer);

    boolean isInArenaBounds(Location location);

    boolean isInWaitingLobbyBounds(Location location);

    IGameSettings getGameSettings();

}
