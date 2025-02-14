package ru.fennec.free.duckhunters.handlers.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import ru.fennec.free.duckhunters.DuckHuntersPlugin;
import ru.fennec.free.duckhunters.common.configs.ConfigManager;
import ru.fennec.free.duckhunters.common.events.GameStateChangeEvent;
import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;
import ru.fennec.free.duckhunters.common.replacers.StaticReplacer;
import ru.fennec.free.duckhunters.common.utils.WorldEditHook;
import ru.fennec.free.duckhunters.handlers.database.configs.MainConfig;
import ru.fennec.free.duckhunters.handlers.database.configs.MessagesConfig;
import ru.fennec.free.duckhunters.handlers.enums.GameState;
import ru.fennec.free.duckhunters.handlers.enums.PlayerRole;
import ru.fennec.free.duckhunters.handlers.messages.MessageManager;

import java.time.Duration;

public class GameStateListener extends BukkitRunnable implements Listener {

    private DuckHuntersPlugin plugin;
    private GameManager gameManager;
    private MessageManager messageManager;

    private MainConfig mainConfig;
    private MessagesConfig messagesConfig;

    public GameStateListener(DuckHuntersPlugin plugin, GameManager gameManager, MessageManager messageManager,
                             ConfigManager<MainConfig> mainConfigManager, ConfigManager<MessagesConfig> messagesConfigManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.messageManager = messageManager;
        this.mainConfig = mainConfigManager.getConfigData();
        this.messagesConfig = messagesConfigManager.getConfigData();
    }

    @Override
    public void run() {
        if (gameManager.getGame() != null) {
            switchCurrentGameState(gameManager.getCurrentGameState());
            gameManager.getGame().decrementTime(1);
            global();
        }
    }

    private void switchCurrentGameState(GameState currentGameState) {
        switch (currentGameState) {
            case NOT_LOADED -> waitForNextGame();
            case LOADING -> prepareNextGame();
            case WAITING -> waitGameStart();
            case STARTING -> startLogic();
            case PLAYING -> gameLogic();
            case ENDED -> finishLogic();
        }
    }

    @EventHandler
    private void onGameStateChangeEvent(GameStateChangeEvent event) {
        GameState newGameState = event.getTo();
        GameState oldGameState = event.getFrom();

        if (newGameState.equals(oldGameState)) {
            event.setCancelled(true);
            return;
        }
        if (oldGameState == GameState.WAITING && newGameState == GameState.LOADING) {
            event.setCancelled(true);
            return;
        }

        switchGameState(newGameState);
    }

    private void switchGameState(GameState newGameState) {
        switch (newGameState) {
            case NOT_LOADED -> setStoppedState();
            case LOADING -> setPreparingState();
            case ENDED -> setFinishedState();
            case STARTING -> setStartingState();
            case WAITING -> setWaitingState();
            case PLAYING -> setRunningState();
        }
    }

    public void setPreparingState() {
        //Register loading events listener and unregister other all
        HandlerList.unregisterAll(plugin.getPlayerPlayingListener());
        HandlerList.unregisterAll(plugin.getPlayerWaitingListener());
        Bukkit.getPluginManager().registerEvents(plugin.getPlayerLoadingListener(), plugin);

        //Teleport players to limbo
        Location locationToTeleport = new Location(Bukkit.getWorld(mainConfig.limboWorld()), 0, 100, 0);
        for (IGamePlayer gamePlayer : gameManager.getGame().getPlayers()) {
            Player bukkitPlayer = gamePlayer.getBukkitPlayer();
            bukkitPlayer.setGameMode(GameMode.SPECTATOR);
            bukkitPlayer.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 255, true, false));
            bukkitPlayer.teleport(locationToTeleport);
        }

        //Unload old world
        plugin.getWorldLoader().unload(gameManager.getGame().getGameSettings().getLobbyLocation().getWorld());

        //Unload old data
        for (IGamePlayer gamePlayer : gameManager.getGame().getPlayers()) {
            gamePlayer.reset();
            gamePlayer.getBukkitPlayer().getActivePotionEffects().forEach(pe -> gamePlayer.getBukkitPlayer().removePotionEffect(pe.getType()));
        }

        //Start next game
        gameManager.setGame(gameManager.startNextGame());
    }

    public void setFinishedState() {
        gameManager.getGame().setTime(10);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            player.setFoodLevel(20);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        }
    }

    public void setStoppedState() {
        //Stop current game
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kick(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().stopped()));
        }

        plugin.getWorldLoader().unload(gameManager.getGame().getMinArenaLocation().getWorld());
        gameManager.setGame(null);
    }

    public void setWaitingState() {
        //Register Waiting events listener and unregister other all
        HandlerList.unregisterAll(plugin.getPlayerLoadingListener());
        HandlerList.unregisterAll(plugin.getPlayerPlayingListener());
        Bukkit.getPluginManager().registerEvents(plugin.getPlayerWaitingListener(), plugin);

        //Change world's difficulty
        try {
            gameManager.getGame().getGameSettings().getFirstWaitingLobbyLocation().getWorld().setDifficulty(Difficulty.PEACEFUL);
        } catch (RuntimeException e) {
            e.printStackTrace();
            System.out.println("§cПовторный выбор арены!");
            System.out.println("§cПовторный выбор арены!");
            System.out.println("§cПовторный выбор арены!");
            System.out.println("§cПовторный выбор арены!");
            gameManager.getGame().setTime(30);
            gameManager.switchState(GameState.LOADING);
            return;
        }

        //Process player join
        int joined = 0;
        int max = gameManager.getGame().getGameSettings().getMaxPlayers();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(GameMode.SURVIVAL);
            player.sendTitlePart(TitlePart.TITLE, Component.empty().content("§01"));
            player.sendTitlePart(TitlePart.SUBTITLE, Component.empty().content("§01"));
            player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(2 * 50), Duration.ofMillis(0)));
            player.setCanPickupItems(true);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);

            if (joined >= max) {
                player.kick(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().serverIsFull()));
            } else {
                player.teleport(gameManager.getGame().getGameSettings().getLobbyLocation());
                plugin.getPlayerWaitingListener().processPlayerJoin(plugin.getPlayersContainer().getCachedPlayerByUUID(player.getUniqueId()));
                joined++;
            }
            for (Player target : Bukkit.getOnlinePlayers()) {
                player.showPlayer(plugin, target);
            }
        }
    }

    public void setStartingState() {
        gameManager.getGame().setTime(mainConfig.arena().startTime());
        for (IGamePlayer gamePlayer : gameManager.getGame().getPlayers()) {
            gamePlayer.getBukkitPlayer().sendMessage(messageManager.parsePlaceholders(gamePlayer, messagesConfig.playerSection().start()));
            for (IGamePlayer targetGamePlayer : gameManager.getGame().getPlayers()) {
                gamePlayer.getBukkitPlayer().showPlayer(plugin, targetGamePlayer.getBukkitPlayer());
            }
        }
    }

    public void setRunningState() {
        //Register Running events listener and unregister other all
        HandlerList.unregisterAll(plugin.getPlayerLoadingListener());
        HandlerList.unregisterAll(plugin.getPlayerWaitingListener());
        Bukkit.getPluginManager().registerEvents(plugin.getPlayerPlayingListener(), plugin);

        gameManager.getGame().setTime(mainConfig.arena().playTime());
        gameManager.getGame().getGameSettings().getFirstArenaLocation().getWorld().setDifficulty(Difficulty.NORMAL);

        //ToDo random select hunter, other players are ducks

        WorldEditHook.pasteLobbySchematic(plugin, mainConfig.arena().schematicPath(), gameManager.getGame().getGameSettings().getLobbyLocation());

        for (IGamePlayer gamePlayer : gameManager.getGame().getPlayers()) {
            gamePlayer.getBukkitPlayer().sendTitlePart(TitlePart.TITLE, messageManager.parsePlaceholders(gamePlayer, messagesConfig.playerSection().startTitle()));
            gamePlayer.getBukkitPlayer().sendTitlePart(TitlePart.TITLE, messageManager.parsePlaceholders(gamePlayer, messagesConfig.playerSection().startSubtitle()));
            gamePlayer.getBukkitPlayer().sendTitlePart(TitlePart.TIMES, Title.Times.times(
                    Duration.ofMillis(50 * messagesConfig.playerSection().startFadeIn()),
                    Duration.ofMillis(50 * messagesConfig.playerSection().startStay()),
                    Duration.ofMillis(50 * messagesConfig.playerSection().startFadeOut())));
        }
    }

    public void prepareNextGame() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitlePart(TitlePart.TITLE, messageManager.parsePluginPlaceholders(messagesConfig.playerSection().preparingTitle()));
            player.sendTitlePart(TitlePart.TITLE, messageManager.parsePluginPlaceholders(messagesConfig.playerSection().preparingSubtitle()));
            player.sendTitlePart(TitlePart.TIMES, Title.Times.times(
                    Duration.ofMillis(50 * messagesConfig.playerSection().preparingFadeIn()),
                    Duration.ofMillis(50 * messagesConfig.playerSection().preparingStay()),
                    Duration.ofMillis(50 * messagesConfig.playerSection().preparingFadeOut())));
        }

        if (gameManager.getGame().getTime() == 0) {
            gameManager.switchState(GameState.WAITING);
        }
    }

    public void waitForNextGame() {
        //Nothing ??
    }

    public void waitGameStart() {
        //Start countdown
        if (Bukkit.getOnlinePlayers().size() >= gameManager.getGame().getGameSettings().getMinPlayers()) {
            gameManager.switchState(GameState.STARTING);
        }

        //ToDo: give items???
    }

    public void startLogic() {
        //Disable countdown
        if (Bukkit.getOnlinePlayers().size() < gameManager.getGame().getGameSettings().getMinPlayers()) {
            gameManager.switchState(GameState.WAITING);
            for (IGamePlayer gamePlayer : gameManager.getGame().getPlayers()) {
                Player bukkitPlayer = gamePlayer.getBukkitPlayer();
                bukkitPlayer.setLevel(0);
                bukkitPlayer.setExp(0);
                bukkitPlayer.sendMessage(messageManager.parsePlaceholders(gamePlayer,
                        StaticReplacer.replacer()
                                .set("players-min", gameManager.getGame().getGameSettings().getMinPlayers())
                                .apply(messagesConfig.playerSection().noPlayers())));
            }
        } else {
            if (gameManager.getGame().getTime() <= 10) {
                if (gameManager.getGame().getTime() == 0) {
                    gameManager.switchState(GameState.PLAYING);
                } else {
                    startCountdown();
                }
            }
        }
    }

    public void startCountdown() {
        for (IGamePlayer gamePlayer : gameManager.getGame().getPlayers()) {
            Player bukkitPlayer = gamePlayer.getBukkitPlayer();
            bukkitPlayer.setLevel(gameManager.getGame().getTime());
            bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.valueOf(mainConfig.arena().countdownSoundName()), SoundCategory.MASTER,
                    mainConfig.arena().countdownSoundVolume(), mainConfig.arena().countdownSoundPitch());
            bukkitPlayer.setExp(0.99f);
        }
    }

    public void gameLogic() {
        for (Entity entity : gameManager.getGame().getMinArenaLocation().getWorld().getEntities()) {
            if (entity instanceof Arrow) {
                if (entity.isOnGround()) entity.remove();
            }
        }

        boolean forceEndGame = false;

        for (IGamePlayer gamePlayer : gameManager.getGame().getPlayers()) {
            if (gamePlayer.getPlayerRole().equals(PlayerRole.DUCK)) {
                forceEndGame = gamePlayer.didEndedRace();
            }
        }

        if (gameManager.getGame().getTime() == 0 || forceEndGame) {
            gameManager.endGame();
        }
    }

    public void finishLogic() {
        if (gameManager.getGame().getTime() == 0) {
            gameManager.getGame().setTime(30);
            gameManager.switchState(GameState.LOADING);
        }
    }

    public void global() {
        //Nothing ??
    }

    public void updateConfigData(ConfigManager<MainConfig> mainConfigManager, ConfigManager<MessagesConfig> messagesConfigManager) {
        this.mainConfig = mainConfigManager.getConfigData();
        this.messagesConfig = messagesConfigManager.getConfigData();
    }
}
