package ru.fennec.free.duckhunters.handlers.players;

import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayersContainer {

    //Список кэш игроков
    private final List<IGamePlayer> cachedPlayers;

    public PlayersContainer() {
        this.cachedPlayers = new ArrayList<>();
    }

    /***
     * Получение IGamePlayer из списка кэш игроков по UUID игрока (Player#getUniqueId())
     *
     * @param uuid UUID игрока
     * @return IGamePlayer или null, если игрока нет в кэше плагина
     */
    public IGamePlayer getCachedPlayerByUUID(UUID uuid) {
        IGamePlayer gamePlayer = null;
        for (IGamePlayer target : cachedPlayers) {
            if (uuid.equals(target.getGamePlayerUUID())) {
                gamePlayer = target;
            }
        }
        return gamePlayer;
    }

    /***
     * Добавить игрока IGamePlayer в кэш плагина
     *
     * @param gamePlayer объект IGamePlayer, который записывается в кэш
     */
    public void addCachedPlayer(IGamePlayer gamePlayer) {
        this.cachedPlayers.add(gamePlayer);
    }

    /***
     * Удалить игрока из кэша плагина по его UUID (Player#getUniqueId() или IGamePlayer#getGamePlayerUUID())
     *
     * @param uuid UUID, по которому идёт поиск игрока в кэше
     */
    public void removeCachedPlayerByUUID(UUID uuid) {
        this.cachedPlayers.removeIf(gamePlayer -> uuid.equals(gamePlayer.getGamePlayerUUID()));
    }

    /***
     * Возвращает полный список кэшированных игроков
     *
     * @return Список из IGamePlayer. Пустой список - нет игроков в кэше
     */
    public List<IGamePlayer> getAllCachedPlayers() {
        return this.cachedPlayers;
    }

}
