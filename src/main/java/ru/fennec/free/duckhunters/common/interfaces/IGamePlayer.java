package ru.fennec.free.duckhunters.common.interfaces;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public interface IGamePlayer {

    long getId();

    Player getBukkitPlayer();

    UUID getGamePlayerUUID();

    //Победы, поражения, убийства, смерти
    Map<String, Long> getStatistics();

    void setId(long id);

    void setStatistic(String name, long value);

}
