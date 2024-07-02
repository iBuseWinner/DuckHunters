package ru.fennec.free.duckhunters.handlers.players;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamePlayer implements IGamePlayer {
    private long id;
    private Player bukkitPlayer;
    private UUID gamePlayerUUID;
    Map<String, Long> statistics;

    public GamePlayer(Player bukkitPlayer) {
        this.id = -1;
        this.bukkitPlayer = bukkitPlayer;
        this.gamePlayerUUID = bukkitPlayer.getUniqueId();
        this.statistics = new HashMap<>();
        statistics.put("wins", 0L);
        statistics.put("loses", 0L);
        statistics.put("kills", 0L);
        statistics.put("deaths", 0L);
    }

    public GamePlayer(long id, UUID playerUUID, Map<String, Long> statistics) {
        this.id = id;
        this.gamePlayerUUID = playerUUID;
        this.statistics = statistics;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Player getBukkitPlayer() {
        return (bukkitPlayer == null ? Bukkit.getPlayer(gamePlayerUUID) : bukkitPlayer);
    }

    @Override
    public UUID getGamePlayerUUID() {
        return gamePlayerUUID;
    }

    @Override
    public Map<String, Long> getStatistics() {
        return statistics;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void setStatistic(String name, long value) {
        statistics.put(name, value);
    }
}
