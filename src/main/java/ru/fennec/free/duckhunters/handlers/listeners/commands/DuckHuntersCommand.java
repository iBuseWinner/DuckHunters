package ru.fennec.free.duckhunters.handlers.listeners.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import ru.fennec.free.duckhunters.DuckHuntersPlugin;
import ru.fennec.free.duckhunters.common.abstracts.AbstractCommand;
import ru.fennec.free.duckhunters.common.configs.ConfigManager;
import ru.fennec.free.duckhunters.common.interfaces.IDatabase;
import ru.fennec.free.duckhunters.handlers.database.configs.MainConfig;
import ru.fennec.free.duckhunters.handlers.database.configs.MessagesConfig;
import ru.fennec.free.duckhunters.handlers.messages.MessageManager;
import ru.fennec.free.duckhunters.handlers.players.PlayersContainer;

public class DuckHuntersCommand extends AbstractCommand {

    private final DuckHuntersPlugin plugin;
    private final ConfigManager<MessagesConfig> messagesConfigManager;
    private final ConfigManager<MainConfig> mainConfigManager;
    private MainConfig mainConfig;
    private MessagesConfig messagesConfig;
    private final IDatabase database;
    private final PlayersContainer playersContainer;
    private final MessageManager messageManager;

    public DuckHuntersCommand(DuckHuntersPlugin plugin, ConfigManager<MessagesConfig> messagesConfigManager, ConfigManager<MainConfig> mainConfigManager, IDatabase database,
                              PlayersContainer playersContainer, MessageManager messageManager) {
        super(plugin, "duckhunters");
        this.plugin = plugin;
        this.messagesConfigManager = messagesConfigManager;
        this.mainConfigManager = mainConfigManager;
        this.mainConfig = mainConfigManager.getConfigData();
        this.messagesConfig = messagesConfigManager.getConfigData();
        this.database = database;
        this.playersContainer = playersContainer;
        this.messageManager = messageManager;
    }

    @Override
    public void execute(CommandSender commandSender, String label, String[] args) {

    }

    /*
    Сообщение о версии плагина и его разработчике
     */
    private void aboutPlugin(CommandSender commandSender) {
        commandSender.sendMessage(messageManager.parsePluginPlaceholders("${prefix} &aПлагин Reputation от BuseSo (iBuseWinner). " +
                "Установлена версия "+plugin.getDescription().getVersion()));
        commandSender.sendMessage(messageManager.parsePluginPlaceholders("${prefix} &aСтраница плагина: https://spigotmc.ru/resources/124/"));
    }

    /*
    Отправить игроку сообщение со списком доступных команд плагина
     */
    private void sendHelp(CommandSender commandSender) {
        messagesConfig.playerSection().helpStrings().forEach(str -> commandSender.sendMessage(messageManager.parsePluginPlaceholders(str)));
        if (commandSender.hasPermission("reputation.admin.help")) {
            messagesConfig.adminSection().helpStrings().forEach(str -> commandSender.sendMessage(messageManager.parsePluginPlaceholders(str)));
        }
    }

    /*
    Перезагрузить файлы конфигурации плагина (config.yml, lang.yml)
     */
    private void reloadPlugin(CommandSender commandSender) {
        if (commandSender.hasPermission("reputation.admin.reload")) {
            messagesConfigManager.reloadConfig(plugin.getLogger());
            mainConfigManager.reloadConfig(plugin.getLogger());
            plugin.updateConfigData();
            mainConfig = mainConfigManager.getConfigData();
            messagesConfig = messagesConfigManager.getConfigData();
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().configsReloadedSuccessfully()));
            return;
        }

        commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().noPermission()));
    }
}
