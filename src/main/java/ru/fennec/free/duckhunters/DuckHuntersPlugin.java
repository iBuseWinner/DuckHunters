package ru.fennec.free.duckhunters;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.fennec.free.duckhunters.common.configs.ConfigManager;
import ru.fennec.free.duckhunters.common.interfaces.IDatabase;
import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;
import ru.fennec.free.duckhunters.handlers.database.configs.MainConfig;
import ru.fennec.free.duckhunters.handlers.database.configs.MessagesConfig;
import ru.fennec.free.duckhunters.handlers.database.data.MySQLDatabase;
import ru.fennec.free.duckhunters.handlers.database.data.SQLDatabase;
import ru.fennec.free.duckhunters.handlers.listeners.commands.DuckHuntersCommand;
import ru.fennec.free.duckhunters.handlers.messages.MessageManager;
import ru.fennec.free.duckhunters.handlers.messages.PlaceholderHook;
import ru.fennec.free.duckhunters.handlers.players.PlayersContainer;

import java.util.logging.Level;

public final class DuckHuntersPlugin extends JavaPlugin {

    private ConfigManager<MainConfig> mainConfigManager;
    private ConfigManager<MessagesConfig> messagesConfigManager;
    private IDatabase database;
    private PlayersContainer playersContainer;
    private MessageManager messageManager;
    private PlaceholderHook placeholderHook;

    @Override
    public void onEnable() {
        loadConfigs();
        initializeDatabase();
        initializeHandlers();
        //ToDo слушатели...
//        registerListeners();
        registerCommand();
    }

    private void loadConfigs() {
        this.mainConfigManager = ConfigManager.create(this.getDataFolder().toPath(), "config.yml", MainConfig.class);
        this.mainConfigManager.reloadConfig(getLogger());
        this.messagesConfigManager = ConfigManager.create(this.getDataFolder().toPath(), "lang.yml", MessagesConfig.class);
        this.messagesConfigManager.reloadConfig(getLogger());
    }

    private void initializeDatabase() {
        switch (mainConfigManager.getConfigData().database().type()) {
            case MYSQL -> this.database = new MySQLDatabase(this.mainConfigManager);
            case SQL -> this.database = new SQLDatabase(this.mainConfigManager);
        }
        if (this.database != null) {
            this.database.initializeTables();
        }
    }

    private void initializeHandlers() {
        this.playersContainer = new PlayersContainer();
        this.messageManager = new MessageManager(messagesConfigManager);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.placeholderHook = new PlaceholderHook(getPluginMeta().getVersion(), playersContainer, database, this.mainConfigManager);
            this.placeholderHook.register();
        } else {
            getLogger().log(Level.WARNING, "Плагин PlaceholderAPI не обнаружен на данном сервере!");
            getLogger().log(Level.WARNING, "Плагин DuckHunters не сможет обрабатывать глобальные плейсхолдеры!");
        }
    }

    private void registerCommand() {
        new DuckHuntersCommand(this, messagesConfigManager, mainConfigManager, database, playersContainer, messageManager);
    }

    @Override
    public void onDisable() {
        for (IGamePlayer gamePlayer : playersContainer.getAllCachedPlayers()) {
            database.savePlayer(gamePlayer);
        }
    }

    public void updateConfigData() {
        //ToDo: update config in all files like there
        // https://github.com/iBuseWinner/Reputation/blob/master/src/main/java/ru/fennec/free/reputation/ReputationPlugin.java#L96
        this.messageManager.updateConfigData(this.messagesConfigManager);
    }
}
