package ru.fennec.free.duckhunters.handlers.game.conventer;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import ru.fennec.free.duckhunters.DuckHuntersPlugin;
import ru.fennec.free.duckhunters.common.configs.ConfigManager;
import ru.fennec.free.duckhunters.common.events.*;
import ru.fennec.free.duckhunters.handlers.database.configs.MainConfig;
import ru.fennec.free.duckhunters.handlers.database.configs.MessagesConfig;
import ru.fennec.free.duckhunters.handlers.enums.GameState;
import ru.fennec.free.duckhunters.handlers.messages.MessageManager;
import ru.fennec.free.duckhunters.handlers.players.PlayersContainer;

public class PlayerEventsConventer implements Listener {

    private DuckHuntersPlugin plugin;
    private MessageManager messageManager;
    private MainConfig mainConfig;
    private MessagesConfig messagesConfig;
    private PlayersContainer playersContainer;

    public PlayerEventsConventer(DuckHuntersPlugin plugin, MessageManager messageManager,
                                 ConfigManager<MainConfig> mainConfigManager, ConfigManager<MessagesConfig> messagesConfigManager,
                                 PlayersContainer playersContainer) {
        this.plugin = plugin;
        this.messageManager = messageManager;
        this.mainConfig = mainConfigManager.getConfigData();
        this.messagesConfig = messagesConfigManager.getConfigData();
        this.playersContainer = playersContainer;
    }

    @EventHandler
    private void onPrePlayerLogin(AsyncPlayerPreLoginEvent event) {
        int max = plugin.getGameManager().getGame().getGameSettings().getMaxPlayers();
        if (Bukkit.getOnlinePlayers().size() == max) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, messageManager.parsePluginPlaceholders(messagesConfig.playerSection().serverIsFull()));
        } else if (plugin.getGameManager().getCurrentGameState() == GameState.PLAYING
                || plugin.getGameManager().getCurrentGameState() == GameState.ENDED) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, messageManager.parsePluginPlaceholders(messagesConfig.playerSection().gameRunning()));
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.joinMessage(Component.empty());

        GamePlayerJoinEvent gamePlayerJoinEvent = new GamePlayerJoinEvent(playersContainer.registerPlayer(player));
        Bukkit.getPluginManager().callEvent(gamePlayerJoinEvent);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.quitMessage(Component.empty());

        GamePlayerQuitEvent gamePlayerQuitEvent = new GamePlayerQuitEvent(playersContainer.unregisterPlayer(player.getUniqueId()));
        Bukkit.getPluginManager().callEvent(gamePlayerQuitEvent);
    }

    @EventHandler
    private void onPlayerMoveEvent(PlayerMoveEvent event) {
        GamePlayerMoveEvent gamePlayerMoveEvent = new GamePlayerMoveEvent(playersContainer.getCachedPlayerByUUID(event.getPlayer().getUniqueId()),
                event.getTo(), event.getFrom());
        Bukkit.getPluginManager().callEvent(gamePlayerMoveEvent);
        event.setCancelled(gamePlayerMoveEvent.isCancelled());
    }

    @EventHandler
    private void onPlayerInteractEvent(PlayerInteractEvent event) {
        GamePlayerInteractEvent gamePlayerInteractEvent = new GamePlayerInteractEvent(
                playersContainer.getCachedPlayerByUUID(event.getPlayer().getUniqueId()), event.getAction());
        Bukkit.getPluginManager().callEvent(gamePlayerInteractEvent);
        event.setCancelled(gamePlayerInteractEvent.isCancelled());
    }

    @EventHandler
    private void onPlayerDeathEvent(PlayerDeathEvent event) {
        EntityDamageEvent.DamageCause damageCause =
                event.getEntity().getKiller() == null ? EntityDamageEvent.DamageCause.VOID : EntityDamageEvent.DamageCause.ENTITY_ATTACK;
        GamePlayerDeathEvent gamePlayerDeathEvent = new GamePlayerDeathEvent(
                playersContainer.getCachedPlayerByUUID(event.getPlayer().getUniqueId()),
                playersContainer.getCachedPlayerByUUID(event.getPlayer().getKiller().getUniqueId()), damageCause);
        event.deathMessage(Component.empty());
        Bukkit.getPluginManager().callEvent(gamePlayerDeathEvent);
        event.setKeepInventory(gamePlayerDeathEvent.isKeepInventory());
    }

    @EventHandler
    private void onBlockBreakEvent(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockPlaceEvent(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerGetDamageEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            GamePlayerGetDamageEvent gamePlayerGetDamageEvent = new GamePlayerGetDamageEvent(
                    playersContainer.getCachedPlayerByUUID(event.getDamager().getUniqueId()),
                    playersContainer.getCachedPlayerByUUID(event.getEntity().getUniqueId()));
            Bukkit.getPluginManager().callEvent(gamePlayerGetDamageEvent);
            event.setCancelled(gamePlayerGetDamageEvent.isCancelled());
        } else if (event.getDamager() instanceof Projectile && event.getEntity() instanceof Player) {
            GamePlayerGetDamageEvent gamePlayerGetDamageEvent = new GamePlayerGetDamageEvent(
                    playersContainer.getCachedPlayerByUUID(((Player) ((Projectile)event.getDamager()).getShooter()).getUniqueId()),
                    playersContainer.getCachedPlayerByUUID(event.getEntity().getUniqueId()));
            Bukkit.getPluginManager().callEvent(gamePlayerGetDamageEvent);
            event.setCancelled(gamePlayerGetDamageEvent.isCancelled());
        }
        //Атака возможна либо руками, либо запускаемыми энтити, другие атаки мы не обрабатываем, по крайней мере, до тех пор, пока о них не заявят
    }

    @EventHandler
    private void onPlayerGetDamageFromBlock(EntityDamageByBlockEvent event) {
        event.setCancelled(true); //Возможно это плохо и не будет урона от магма блока какого-нибудь, но мы посмотрим...
    }

    @EventHandler
    private void onPlayerChatEvent(AsyncChatEvent event) {
        event.setCancelled(true);
        Bukkit.getScheduler().runTask(plugin, () -> {
            GamePlayerChatEvent gamePlayerChatEvent = new GamePlayerChatEvent(playersContainer.getCachedPlayerByUUID(event.getPlayer().getUniqueId()), event.message());
            Bukkit.getPluginManager().callEvent(gamePlayerChatEvent);
        });
    }

}
