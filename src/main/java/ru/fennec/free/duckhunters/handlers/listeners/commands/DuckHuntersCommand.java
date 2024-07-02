package ru.fennec.free.duckhunters.handlers.listeners.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.fennec.free.duckhunters.DuckHuntersPlugin;
import ru.fennec.free.duckhunters.common.abstracts.AbstractCommand;
import ru.fennec.free.duckhunters.common.configs.ConfigManager;
import ru.fennec.free.duckhunters.common.interfaces.IDatabase;
import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;
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
        switch (args.length) {
            case 1:
                switch (args[0].toLowerCase()) {
                    case "self", "me", "info" -> sendSelfInfo(commandSender);
                    case "player" -> sendHelp(commandSender);
                    case "reload" -> reloadPlugin(commandSender);
                    case "about" -> aboutPlugin(commandSender);
                    case "start" -> startGame(commandSender);
                    case "forcestart" -> forceStartGame(commandSender);
                    default -> sendOtherInfo(commandSender, args[0]);
                }
                break;
            case 3:
                if (args[0].equalsIgnoreCase("player") && args[2].equalsIgnoreCase("reset")) {
                    resetPlayerStatistics(commandSender, args[1]);
                } else {
                    sendHelp(commandSender);
                }
                break;
            case 5:
                if (args[0].equalsIgnoreCase("player")) {
                    if (args[2].equalsIgnoreCase("set")) {
                        setPlayerStatistic(commandSender, args[1], args[3], args[4]);
                    } else if (args[2].equalsIgnoreCase("add")) {
                        addPlayerStatistic(commandSender, args[1], args[3], args[4]);
                    }
                } else {
                    sendHelp(commandSender);
                }
                break;
            default:
                sendHelp(commandSender);
        }
    }

    private void addPlayerStatistic(CommandSender commandSender, String targetName, String key, String value) {
        if (commandSender.hasPermission("duckhunters.admin.add")) {
            Player targetPlayer = Bukkit.getPlayer(targetName);
            if (targetPlayer == null) {
                commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerIsOffline()));
                return;
            }

            IGamePlayer targetGamePlayer = playersContainer.getCachedPlayerByUUID(targetPlayer.getUniqueId());
            if (targetGamePlayer == null) {
                commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerNotInCache()));
                return;
            }

            boolean isNumber = value.matches("[+-]?[0-9]+");

            if (isNumber) {
                if (key.equalsIgnoreCase("wins") || key.equalsIgnoreCase("loses")
                        || key.equalsIgnoreCase("kills") || key.equalsIgnoreCase("deaths")) {
                    targetGamePlayer.setStatistic(key, targetGamePlayer.getStatistics().getOrDefault(key, 0L) + Long.parseLong(value));
                    commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.adminSection().playerAdd()));
                    return;
                }
                commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().noStatistic()));
                return;
            }

            commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.adminSection().mustBeNumber()));
            return;
        }
        commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().noPermission()));
    }

    private void setPlayerStatistic(CommandSender commandSender, String targetName, String key, String value) {
        if (commandSender.hasPermission("duckhunters.admin.set")) {
            Player targetPlayer = Bukkit.getPlayer(targetName);
            if (targetPlayer == null) {
                commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerIsOffline()));
                return;
            }

            IGamePlayer targetGamePlayer = playersContainer.getCachedPlayerByUUID(targetPlayer.getUniqueId());
            if (targetGamePlayer == null) {
                commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerNotInCache()));
                return;
            }

            boolean isNumber = value.matches("[+-]?[0-9]+");

            if (isNumber) {
                if (key.equalsIgnoreCase("wins") || key.equalsIgnoreCase("loses")
                        || key.equalsIgnoreCase("kills") || key.equalsIgnoreCase("deaths")) {
                    targetGamePlayer.setStatistic(key, Long.parseLong(value));
                    commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.adminSection().playerSet()));
                    return;
                }
                commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().noStatistic()));
                return;
            }

            commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.adminSection().mustBeNumber()));
            return;
        }
        commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().noPermission()));
    }

    private void resetPlayerStatistics(CommandSender commandSender, String targetName) {
        if (commandSender.hasPermission("duckhunters.admin.reset")) {
            Player targetPlayer = Bukkit.getPlayer(targetName);
            if (targetPlayer == null) {
                commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerIsOffline()));
                return;
            }

            IGamePlayer targetGamePlayer = playersContainer.getCachedPlayerByUUID(targetPlayer.getUniqueId());
            if (targetGamePlayer == null) {
                commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerNotInCache()));
                return;
            }

            targetGamePlayer.setStatistic("wins", 0L);
            targetGamePlayer.setStatistic("loses", 0L);
            targetGamePlayer.setStatistic("kills", 0L);
            targetGamePlayer.setStatistic("deaths", 0L);
            commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.adminSection().playerReset()));
            return;
        }
        commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().noPermission()));
    }

    private void sendOtherInfo(CommandSender commandSender, String targetName) {
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer == null) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerIsOffline()));
            return;
        }

        IGamePlayer targetGamePlayer = playersContainer.getCachedPlayerByUUID(targetPlayer.getUniqueId());
        if (targetGamePlayer == null) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerNotInCache()));
            return;
        }

        commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.playerSection().selfInfo()));
    }

    private void forceStartGame(CommandSender commandSender) {
        //ToDo: force start game (5 seconds to game start)
    }

    private void startGame(CommandSender commandSender) {
        //ToDo: start timer to game start (30 or 60 seconds)
    }

    private void sendSelfInfo(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().notAPlayer()));
            return;
        }

        IGamePlayer gamePlayer = playersContainer.getCachedPlayerByUUID(((Player) commandSender).getUniqueId());
        commandSender.sendMessage(messageManager.parsePlaceholders(gamePlayer, messagesConfig.playerSection().selfInfo()));
    }

    /*
    Сообщение о версии плагина и его разработчике
     */
    private void aboutPlugin(CommandSender commandSender) {
        commandSender.sendMessage(messageManager.parsePluginPlaceholders("${prefix} &aПлагин DuckHunters от BuseSo (iBuseWinner). " +
                "Установлена версия " + plugin.getDescription().getVersion()));
        commandSender.sendMessage(messageManager.parsePluginPlaceholders("${prefix} &aСтраница плагина: Next update..."));
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
