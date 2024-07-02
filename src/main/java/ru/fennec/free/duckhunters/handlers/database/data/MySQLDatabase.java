package ru.fennec.free.duckhunters.handlers.database.data;

import org.bukkit.entity.Player;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlite3.SQLitePlugin;
import ru.fennec.free.duckhunters.common.configs.ConfigManager;
import ru.fennec.free.duckhunters.common.interfaces.IDatabase;
import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;
import ru.fennec.free.duckhunters.handlers.database.configs.MainConfig;
import ru.fennec.free.duckhunters.handlers.database.data.mappers.GamePlayerMapper;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class MySQLDatabase implements IDatabase {

    private final MainConfig mainConfig;
    private final MainConfig.DatabaseSection databaseSection;
    private final Jdbi jdbi;

    /*
    Удалённая БД, работает через MySQL (MariaDB)
     */
    public MySQLDatabase(ConfigManager<MainConfig> mainConfigManager) {
        this.mainConfig = mainConfigManager.getConfigData();
        this.databaseSection = mainConfig.database();
        this.jdbi = Jdbi.create("jdbc:mysql://" + databaseSection.url() + "/" + databaseSection.database() + databaseSection.args(),
                databaseSection.username(), databaseSection.password());
    }

    /*
    Создание таблиц для плагина, если их нет
     */
    @Override
    public void initializeTables() {
        this.jdbi.useHandle(handle -> {
            handle.execute("CREATE TABLE IF NOT EXISTS \"" + this.databaseSection.tableName() + "\" (" +
                    "`id` BIGINT(50) auto_increment, " +
                    "`uuid` VARCHAR(50), " +
                    "`wins` BIGINT(50), " +
                    "`loses` BIGINT(50), " +
                    "`kills` BIGINT(50), " +
                    "`deaths` BIGINT(50), " +
                    "PRIMARY KEY (`id`) USING BTREE);"); //Таблица со статистикой игроков
        });
    }

    /*
    Добавить нового игрока в бд
     */
    @Override
    public void insertNewPlayer(IGamePlayer gamePlayer) {
        jdbi.useHandle(handle -> {
            handle.execute("INSERT OR IGNORE INTO \"" + this.databaseSection.tableName() + "\" " +
                            "(`uuid`, `wins`, `loses`, `kills`, `deaths`) " +
                            "VALUES (?, ?, ?, ?, ?);",
                    gamePlayer.getGamePlayerUUID().toString(),
                    0, 0, 0, 0);
        });
    }

    /*
    Сохранить очки репутации игрока в бд
     */
    @Override
    public void savePlayer(IGamePlayer gamePlayer) {
        this.jdbi.useHandle(handle -> {
            handle.execute("UPDATE \"" + this.databaseSection.tableName() + "\" SET " +
                            "`wins`=?, `loses`=?, `kills`=?, `deaths`=? WHERE `id`=?",
                    gamePlayer.getStatistics().getOrDefault("wins", 0L),
                    gamePlayer.getStatistics().getOrDefault("loses", 0L),
                    gamePlayer.getStatistics().getOrDefault("kills", 0L),
                    gamePlayer.getStatistics().getOrDefault("deaths", 0L),
                    gamePlayer.getId());
        });
    }

    /*
    Получить игрока и его список фаворитов из бд, засунуть в объект GamePlayer (implements IGamePlayer)
     */
    @Override
    public IGamePlayer wrapPlayer(Player player) {
        AtomicReference<IGamePlayer> atomicGamePlayer = new AtomicReference<>();
        try {
            this.jdbi.useHandle(handle -> {
                IGamePlayer gamePlayer = handle.createQuery("SELECT * FROM \"" + this.databaseSection.tableName() + "\" WHERE `uuid`=?;")
                        .bind(0, player.getUniqueId().toString())
                        .map(new GamePlayerMapper())
                        .first();

                atomicGamePlayer.set(gamePlayer);
            });
        } catch (IllegalStateException ignored) { atomicGamePlayer.set(null); }
        return atomicGamePlayer.get();
    }

    @Override
    public UUID getTopGamePlayerUUIDBySomething(int place, String category) {
        return null;
    }

}
