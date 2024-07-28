package ru.fennec.free.duckhunters.handlers.game.waiting;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.fennec.free.duckhunters.DuckHuntersPlugin;
import ru.fennec.free.duckhunters.common.configs.ConfigManager;
import ru.fennec.free.duckhunters.common.events.*;
import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;
import ru.fennec.free.duckhunters.common.replacers.StaticReplacer;
import ru.fennec.free.duckhunters.handlers.database.configs.MainConfig;
import ru.fennec.free.duckhunters.handlers.database.configs.MessagesConfig;
import ru.fennec.free.duckhunters.handlers.messages.MessageManager;

public class PlayerWaitingListener implements Listener {

    private DuckHuntersPlugin plugin;
    private MainConfig mainConfig;
    private MessagesConfig messagesConfig;
    private MessageManager messageManager;

    public PlayerWaitingListener(DuckHuntersPlugin plugin, ConfigManager<MainConfig> mainConfigManager,
                                 ConfigManager<MessagesConfig> messagesConfigManager, MessageManager messageManager) {
        this.plugin = plugin;
        this.mainConfig = mainConfigManager.getConfigData();
        this.messagesConfig = messagesConfigManager.getConfigData();
        this.messageManager = messageManager;
    }

    @EventHandler
    private void onGamePlayerJoin(GamePlayerJoinEvent event) {
        IGamePlayer gamePlayer = event.getGamePlayer();
        processPlayerJoin(gamePlayer);
    }

    public void processPlayerJoin(IGamePlayer gamePlayer) {
        if (Bukkit.getOnlinePlayers().size() > plugin.getGameManager().getGame().getGameSettings().getMaxPlayers()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                gamePlayer.getBukkitPlayer().kick(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().serverIsFull()));
            }, 1);
            return;
        }

        giveItemsOnJoin(gamePlayer);
        broadcastMessageOnJoin(gamePlayer);
        gamePlayer.getBukkitPlayer().teleport(plugin.getGameManager().getGame().getGameSettings().getLobbyLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        plugin.getGameManager().getGame().addPlayer(gamePlayer);
        gamePlayer.reset();
    }

    private void giveItemsOnJoin(IGamePlayer gamePlayer) {
        Inventory inventory = gamePlayer.getBukkitPlayer().getInventory();
        inventory.clear();

        //ToDo: give items from config i think?
    }

    private void broadcastMessageOnJoin(IGamePlayer gamePlayer) {
        Component text = messageManager.parsePlaceholders(gamePlayer, StaticReplacer.replacer()
                .set("${players-now}", Bukkit.getOnlinePlayers().size())
                .set("${players-max}", plugin.getGameManager().getGame().getGameSettings().getMaxPlayers())
                .apply(messagesConfig.playerSection().joinBroadcast()));
        for (IGamePlayer listener : plugin.getGameManager().getGame().getPlayers()) {
            listener.getBukkitPlayer().sendMessage(text);
        }

        if (gamePlayer.getBukkitPlayer().hasPlayedBefore()) {
            gamePlayer.getBukkitPlayer().sendMessage(messageManager.parsePlaceholders(gamePlayer, messagesConfig.playerSection().notifyOnJoin()));
        } else {
            gamePlayer.getBukkitPlayer().sendMessage(messageManager.parsePlaceholders(gamePlayer, messagesConfig.playerSection().notifyOnFirstJoin()));
        }
    }

    @EventHandler
    private void onGamePlayerInteract(GamePlayerInteractEvent event) {
        IGamePlayer gamePlayer = event.getGamePlayer();
        Action action = event.getAction();

        event.setCancelled(true);
        if (action != Action.PHYSICAL) {
            ItemStack itemStack = gamePlayer.getBukkitPlayer().getInventory().getItemInMainHand();
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                //ToDo: items from config ?
            }
        }
    }

    @EventHandler
    private void onGamePlayerMove(GamePlayerMoveEvent event) {
        Player player = event.getGamePlayer().getBukkitPlayer();
        player.setFallDistance(0);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
    }

    @EventHandler
    private void onGamePlayerQuit(GamePlayerQuitEvent event) {
        IGamePlayer gamePlayer = event.getGamePlayer();
        broadcastMessageOnQuit(gamePlayer);
        plugin.getGameManager().getGame().removePlayer(gamePlayer);
        plugin.getPlayersContainer().unregisterPlayer(gamePlayer.getPlayerUUID());
    }

    private void broadcastMessageOnQuit(IGamePlayer gamePlayer) {
        Component text = messageManager.parsePlaceholders(gamePlayer, StaticReplacer.replacer()
                .set("${players-now}", Bukkit.getOnlinePlayers().size() - 1)
                .set("${players-max}", plugin.getGameManager().getGame().getGameSettings().getMaxPlayers())
                .apply(messagesConfig.playerSection().quitBroadcast()));
        for (IGamePlayer listener : plugin.getGameManager().getGame().getPlayers()) {
            listener.getBukkitPlayer().sendMessage(text);
        }
    }

    @EventHandler
    private void onGamePlayerGetDamage(GamePlayerGetDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onGamePlayerChat(GamePlayerChatEvent event) {
        event.setCancelled(true);
        IGamePlayer gamePlayer = event.getGamePlayer();
        Component message = event.getMessage();

        for (IGamePlayer listener : plugin.getGameManager().getGame().getPlayers()) {
            listener.getBukkitPlayer().sendMessage(messageManager.parsePlaceholders(gamePlayer,
                    StaticReplacer.replacer()
                            .set("${message}", message)
                            .apply(messagesConfig.playerSection().chatFormatInWaitingLobby())));
        }
    }

    @EventHandler
    private void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerSwapItem(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);
    }

}
