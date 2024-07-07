package ru.fennec.free.duckhunters.handlers.players;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.fennec.free.duckhunters.common.interfaces.IGame;
import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;
import ru.fennec.free.duckhunters.handlers.enums.PlayerRole;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamePlayer implements IGamePlayer {
    private long id;
    private Player bukkitPlayer;
    private UUID playerUUID;
    private Map<String, Long> statistics;
    private PlayerRole playerRole;
    private boolean spectator;
    private IGame game;

    public GamePlayer(Player bukkitPlayer) {
        this.id = -1;
        this.bukkitPlayer = bukkitPlayer;
        this.playerUUID = bukkitPlayer.getUniqueId();
        this.statistics = new HashMap<>();
        statistics.put("wins", 0L);
        statistics.put("loses", 0L);
        statistics.put("kills", 0L);
        statistics.put("deaths", 0L);
        this.spectator = false;
    }

    public GamePlayer(long id, UUID playerUUID, Map<String, Long> statistics) {
        this.id = id;
        this.playerUUID = playerUUID;
        this.statistics = statistics;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Player getBukkitPlayer() {
        return (bukkitPlayer == null ? Bukkit.getPlayer(playerUUID) : bukkitPlayer);
    }

    @Override
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @Override
    public Map<String, Long> getStatistics() {
        return statistics;
    }

    @Override
    public PlayerRole getPlayerRole() {
        return playerRole;
    }

    @Override
    public boolean isSpectator() {
        return spectator;
    }

    @Override
    public IGame getGame() {
        return game;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void setStatistic(String name, long value) {
        statistics.put(name, value);
    }

    @Override
    public void setPlayerRole(PlayerRole playerRole) {
        this.playerRole = playerRole;
    }

    @Override
    public void setSpectator(boolean spectator) {
        this.spectator = spectator;
    }

    @Override
    public void setGame(IGame game) {
        this.game = game;
    }
}
