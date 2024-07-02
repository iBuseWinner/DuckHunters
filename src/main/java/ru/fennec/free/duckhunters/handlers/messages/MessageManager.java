package ru.fennec.free.duckhunters.handlers.messages;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import ru.fennec.free.duckhunters.common.configs.ConfigManager;
import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;
import ru.fennec.free.duckhunters.common.replacers.StaticReplacer;
import ru.fennec.free.duckhunters.handlers.database.configs.MessagesConfig;

public class MessageManager {

    private MessagesConfig messagesConfig;

    public MessageManager(ConfigManager<MessagesConfig> messagesConfigManager) {
        this.messagesConfig = messagesConfigManager.getConfigData();
    }

    public Component parseColors(String message) {
        return MiniMessage.miniMessage().deserialize(message);
    }

    /***
     * Заменяет все (1) общие плейхолдеры из плагина, а дальше конвертирует цвета
     *
     * @param message сообщение, в котором необходимо заменить
     * @return сообщение, в котором плейсхолдеры заменены, а цвета сконвертированы
     */
    private String parsePlaceholders(String message) {
        return StaticReplacer.replacer()
                .set("prefix", messagesConfig.prefix())
                .apply(message);
    }

    /***
     * Заменяет все (1) общие плейхолдеры из плагина, а дальше конвертирует цвета
     *
     * @param message сообщение, в котором необходимо заменить
     * @return сообщение, в котором плейсхолдеры заменены, а цвета сконвертированы
     */
    public Component parsePluginPlaceholders(String message) {
        message = StaticReplacer.replacer()
                .set("prefix", messagesConfig.prefix())
                .apply(message);
        return parseColors(message);
    }

    /***
     * Заменяет все плейсхолдеры из плагина, сначала общие, потом личные (reputation, id), дальше из PlaceholderAPI
     *
     * @param gamePlayer игрок, на которого должны быть нацелены плейсхолдеры
     * @param message исходное сообщение
     * @return отформатированное сообщение
     */
    public Component parsePlaceholders(IGamePlayer gamePlayer, String message) {
        message = PlaceholderAPI
                .setPlaceholders(gamePlayer.getBukkitPlayer(),
                        StaticReplacer.replacer()
                                .set("player_id", gamePlayer.getId())
                                .set("player_name", gamePlayer.getBukkitPlayer().getName())
                                .set("player_wins", gamePlayer.getStatistics().getOrDefault("wins", 0L))
                                .set("player_loses", gamePlayer.getStatistics().getOrDefault("loses", 0L))
                                .set("player_kills", gamePlayer.getStatistics().getOrDefault("kills", 0L))
                                .set("player_deaths", gamePlayer.getStatistics().getOrDefault("deaths", 0L))
                                .apply(parsePlaceholders(message)));
        return parseColors(message);
    }

    public void updateConfigData(ConfigManager<MessagesConfig> messagesConfigManager) {
        this.messagesConfig = messagesConfigManager.getConfigData();
    }

}
