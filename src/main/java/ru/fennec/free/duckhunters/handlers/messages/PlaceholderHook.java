package ru.fennec.free.duckhunters.handlers.messages;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import ru.fennec.free.duckhunters.common.configs.ConfigManager;
import ru.fennec.free.duckhunters.common.interfaces.IDatabase;
import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;
import ru.fennec.free.duckhunters.handlers.database.configs.MainConfig;
import ru.fennec.free.duckhunters.handlers.players.PlayersContainer;

public class PlaceholderHook extends PlaceholderExpansion {

    private final String version;
    private final PlayersContainer playersContainer;
    private final IDatabase database;
    private MainConfig mainConfig;

    public PlaceholderHook(String version, PlayersContainer playersContainer, IDatabase database,
                           ConfigManager<MainConfig> mainConfigManager) {
        this.version = version;
        this.playersContainer = playersContainer;
        this.database = database;
        this.mainConfig = mainConfigManager.getConfigData();
    }

    @Override
    public String getIdentifier() {
        return "duckhunters";
    }

    @Override
    public String getAuthor() {
        return "BuseSo";
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        IGamePlayer gamePlayer = playersContainer.getCachedPlayerByUUID(player.getUniqueId());
        if (gamePlayer != null) {
            switch (params.toLowerCase()) {
                case "id": //%reputation_id% - показывает игроку его id из БД
                    return String.valueOf(gamePlayer.getId());
                default:
                    return params;
            }
        }
        return params;
    }

    public void updateConfigData(ConfigManager<MainConfig> mainConfigManager) {
        this.mainConfig = mainConfigManager.getConfigData();
    }

}
