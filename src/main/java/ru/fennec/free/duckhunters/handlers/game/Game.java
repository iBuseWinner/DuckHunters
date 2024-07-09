package ru.fennec.free.duckhunters.handlers.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import ru.fennec.free.duckhunters.common.interfaces.IGame;
import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;
import ru.fennec.free.duckhunters.common.interfaces.IGameSettings;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class Game implements IGame {
    private int time;
    private Location minArenaLocation, maxArenaLocation, minWaitingLobbyLocation, maxWaitingLobbyLocation;
    private Set<IGamePlayer> players;
    private IGameSettings gameSettings;

    public Game(IGameSettings gameSettings) {
        this.time = 5;
        this.players = new HashSet<>();

        int minXArena = Math.min(gameSettings.getFirstArenaLocation().getBlockX(), gameSettings.getSecondArenaLocation().getBlockX());
        int minYArena = Math.min(gameSettings.getFirstArenaLocation().getBlockY(), gameSettings.getSecondArenaLocation().getBlockY());
        int minZArena = Math.min(gameSettings.getFirstArenaLocation().getBlockZ(), gameSettings.getSecondArenaLocation().getBlockZ());
        int maxXArena = Math.max(gameSettings.getFirstArenaLocation().getBlockX(), gameSettings.getSecondArenaLocation().getBlockX());
        int maxYArena = Math.max(gameSettings.getFirstArenaLocation().getBlockY(), gameSettings.getSecondArenaLocation().getBlockY());
        int maxZArena = Math.max(gameSettings.getFirstArenaLocation().getBlockZ(), gameSettings.getSecondArenaLocation().getBlockZ());
        this.minArenaLocation = new Location(gameSettings.getFirstArenaLocation().getWorld(), minXArena, minYArena, minZArena);
        this.maxArenaLocation = new Location(gameSettings.getFirstArenaLocation().getWorld(), maxXArena, maxYArena, maxZArena);

        int minXWaitingLobby = Math.min(gameSettings.getFirstWaitingLobbyLocation().getBlockX(), gameSettings.getSecondWaitingLobbyLocation().getBlockX());
        int minYWaitingLobby = Math.min(gameSettings.getFirstWaitingLobbyLocation().getBlockY(), gameSettings.getSecondWaitingLobbyLocation().getBlockY());
        int minZWaitingLobby = Math.min(gameSettings.getFirstWaitingLobbyLocation().getBlockZ(), gameSettings.getSecondWaitingLobbyLocation().getBlockZ());
        int maxXWaitingLobby = Math.max(gameSettings.getFirstWaitingLobbyLocation().getBlockX(), gameSettings.getSecondWaitingLobbyLocation().getBlockX());
        int maxYWaitingLobby = Math.max(gameSettings.getFirstWaitingLobbyLocation().getBlockY(), gameSettings.getSecondWaitingLobbyLocation().getBlockY());
        int maxZWaitingLobby = Math.max(gameSettings.getFirstWaitingLobbyLocation().getBlockZ(), gameSettings.getSecondWaitingLobbyLocation().getBlockZ());
        this.minWaitingLobbyLocation = new Location(gameSettings.getFirstWaitingLobbyLocation().getWorld(), minXWaitingLobby, minYWaitingLobby, minZWaitingLobby);
        this.maxWaitingLobbyLocation = new Location(gameSettings.getFirstWaitingLobbyLocation().getWorld(), maxXWaitingLobby, maxYWaitingLobby, maxZWaitingLobby);
    }

    @Override
    public int getTime() {
        return time;
    }

    public void decrementTime(int seconds) {
        this.time -= seconds;
    }

    @Override
    public void setTime(int seconds) {
        this.time = seconds;
    }

    @Override
    public Location getMinArenaLocation() {
        return minArenaLocation;
    }

    @Override
    public Location getMaxArenaLocation() {
        return maxArenaLocation;
    }

    @Override
    public Location getMinWaitingLobbyLocation() {
        return minArenaLocation;
    }

    @Override
    public Location getMaxWaitingLobbyLocation() {
        return maxArenaLocation;
    }

    @Override
    public Set<IGamePlayer> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    @Override
    public Stream<IGamePlayer> getAlivePlayers() {
        return players.stream().filter(gamePlayer -> !gamePlayer.isSpectator());
    }

    @Override
    public boolean addPlayer(IGamePlayer gamePlayer) {
        gamePlayer.setGame(this);
        boolean exists = false;
        for (IGamePlayer target : players) {
            if (target.getPlayerUUID().equals(gamePlayer.getPlayerUUID())) {
                exists = true;
                break;
            }
        }
        if (!exists) return players.add(gamePlayer);
        else return false;
    }

    @Override
    public boolean removePlayer(IGamePlayer gamePlayer) {
        gamePlayer.setGame(null);
        return players.remove(gamePlayer);
    }

    @Override
    public boolean isInArenaBounds(Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return x >= minArenaLocation.getX() && y >= minArenaLocation.getY() && z >= minArenaLocation.getZ()
                && x <= maxArenaLocation.getX() && y <= maxArenaLocation.getY() && z <= maxArenaLocation.getZ();
    }

    @Override
    public boolean isInWaitingLobbyBounds(Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return x >= minWaitingLobbyLocation.getX() && y >= minWaitingLobbyLocation.getY() && z >= minWaitingLobbyLocation.getZ()
                && x <= maxWaitingLobbyLocation.getX() && y <= maxWaitingLobbyLocation.getY() && z <= maxWaitingLobbyLocation.getZ();
    }

    @Override
    public IGameSettings getGameSettings() {
        return gameSettings;
    }
}
