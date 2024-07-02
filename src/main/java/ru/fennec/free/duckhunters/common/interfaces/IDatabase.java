package ru.fennec.free.duckhunters.common.interfaces;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface IDatabase {

    void initializeTables();

    void insertNewPlayer(IGamePlayer gamePlayer);

    void savePlayer(IGamePlayer gamePlayer);

    IGamePlayer wrapPlayer(Player player);

    UUID getTopGamePlayerUUIDBySomething(int place, String category);
}
