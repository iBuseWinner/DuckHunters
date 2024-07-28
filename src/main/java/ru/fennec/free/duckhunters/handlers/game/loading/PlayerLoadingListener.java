package ru.fennec.free.duckhunters.handlers.game.loading;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.fennec.free.duckhunters.DuckHuntersPlugin;
import ru.fennec.free.duckhunters.common.configs.ConfigManager;
import ru.fennec.free.duckhunters.common.events.GamePlayerMoveEvent;
import ru.fennec.free.duckhunters.common.events.GamePlayerQuitEvent;
import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;
import ru.fennec.free.duckhunters.handlers.database.configs.MainConfig;
import ru.fennec.free.duckhunters.handlers.database.configs.MessagesConfig;
import ru.fennec.free.duckhunters.handlers.messages.MessageManager;

public class PlayerLoadingListener implements Listener {

    private DuckHuntersPlugin plugin;
    private MainConfig mainConfig;
    private MessagesConfig messagesConfig;
    private MessageManager messageManager;

    public PlayerLoadingListener(DuckHuntersPlugin plugin, ConfigManager<MainConfig> mainConfigManager,
                                 ConfigManager<MessagesConfig> messagesConfigManager, MessageManager messageManager) {
        this.plugin = plugin;
        this.mainConfig = mainConfigManager.getConfigData();
        this.messagesConfig = messagesConfigManager.getConfigData();
        this.messageManager = messageManager;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().kick(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().preparingKick()));
    }

    @EventHandler
    private void onGamePlayerMove(GamePlayerMoveEvent event) {
        Player player = event.getGamePlayer().getBukkitPlayer();
        player.setFallDistance(0);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
    }

    @EventHandler
    private void onGamePlayerQuit(GamePlayerQuitEvent event) {
        plugin.getDatabase().savePlayer(event.getGamePlayer());
        plugin.getPlayersContainer().unregisterPlayer(event.getGamePlayer().getPlayerUUID());
    }

}
