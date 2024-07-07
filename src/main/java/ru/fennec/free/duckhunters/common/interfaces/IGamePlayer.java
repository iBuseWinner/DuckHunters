package ru.fennec.free.duckhunters.common.interfaces;

import org.bukkit.entity.Player;
import ru.fennec.free.duckhunters.handlers.enums.PlayerRole;

import java.util.Map;
import java.util.UUID;

public interface IGamePlayer {

    long getId();

    Player getBukkitPlayer();

    UUID getPlayerUUID();

    //Победы, поражения, убийства, смерти
    Map<String, Long> getStatistics();

    PlayerRole getPlayerRole();

    boolean isSpectator();

    IGame getGame();

    void setId(long id);

    void setStatistic(String name, long value);

    void setPlayerRole(PlayerRole playerRole);

    void setSpectator(boolean spectator);

    void setGame(IGame game);

}
