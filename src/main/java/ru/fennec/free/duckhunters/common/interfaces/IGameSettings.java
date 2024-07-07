package ru.fennec.free.duckhunters.common.interfaces;

import org.bukkit.Location;

import java.io.File;

public interface IGameSettings {

    String getId();

    String getName();

    File getConfigPath();

    Location getFirstArenaLocation();

    Location getSecondArenaLocation();

    Location getFirstWaitingLobbyLocation();

    Location getSecondWaitingLobbyLocation();

    Location getLobbyLocation();

    Location getDucksSpawnLocation();

    Location getHuntersSpawnLocation();

    Location getSpectatorsSpawnLocation();

    int getMinPlayers();

    int getMaxPlayers();

}
