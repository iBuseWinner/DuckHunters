package ru.fennec.free.duckhunters.handlers.game;

import org.bukkit.Location;
import ru.fennec.free.duckhunters.common.interfaces.IGameSettings;

import java.io.File;

public class GameSettings implements IGameSettings {
    private final String id;
    private String name;
    private final File configPath;

    public GameSettings(IGameSettings other) {
        this.id = other.getId();
        this.configPath = other.getConfigPath();
        this.name = other.getName();
    }

    public GameSettings(String id, File configPath) {
        this.id = id;
        this.configPath = configPath;
        //ToDo get other settings
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public File getConfigPath() {
        return configPath;
    }

    //ToDo get all settings from config

    @Override
    public Location getFirstArenaLocation() {
        return null;
    }

    @Override
    public Location getSecondArenaLocation() {
        return null;
    }

    @Override
    public Location getFirstWaitingLobbyLocation() {
        return null;
    }

    @Override
    public Location getSecondWaitingLobbyLocation() {
        return null;
    }

    @Override
    public Location getLobbyLocation() {
        return null;
    }

    @Override
    public Location getDucksSpawnLocation() {
        return null;
    }

    @Override
    public Location getHuntersSpawnLocation() {
        return null;
    }

    @Override
    public Location getSpectatorsSpawnLocation() {
        return null;
    }

    @Override
    public int getMinPlayers() {
        return 0;
    }

    @Override
    public int getMaxPlayers() {
        return 0;
    }
}
