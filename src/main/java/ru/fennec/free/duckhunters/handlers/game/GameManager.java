package ru.fennec.free.duckhunters.handlers.game;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import ru.fennec.free.duckhunters.DuckHuntersPlugin;
import ru.fennec.free.duckhunters.common.configs.ConfigManager;
import ru.fennec.free.duckhunters.common.events.GameStateChangeEvent;
import ru.fennec.free.duckhunters.common.interfaces.IGame;
import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;
import ru.fennec.free.duckhunters.common.interfaces.IGameSettings;
import ru.fennec.free.duckhunters.common.replacers.StaticReplacer;
import ru.fennec.free.duckhunters.common.utils.WorldLoader;
import ru.fennec.free.duckhunters.handlers.database.configs.MainConfig;
import ru.fennec.free.duckhunters.handlers.database.configs.MessagesConfig;
import ru.fennec.free.duckhunters.handlers.enums.GameState;
import ru.fennec.free.duckhunters.handlers.enums.PlayerRole;
import ru.fennec.free.duckhunters.handlers.messages.MessageManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameManager {

    private DuckHuntersPlugin plugin;
    private IGame game;
    private GameState currentGameState;
    private File dataFolder;
    private WorldLoader worldLoader;
    private MainConfig mainConfig;
    private MessagesConfig messagesConfig;
    private MessageManager messageManager;

    private final Map<String, IGameSettings> activeGameSettings = new HashMap<>();

    public GameManager(DuckHuntersPlugin plugin, File dataFolder, WorldLoader worldLoader,
                       ConfigManager<MainConfig> mainConfigManager, ConfigManager<MessagesConfig> messagesConfigManager,
                       MessageManager messageManager) {
        this.plugin = plugin;
        this.dataFolder = dataFolder;
        this.worldLoader = worldLoader;
        this.mainConfig = mainConfigManager.getConfigData();
        this.messagesConfig = messagesConfigManager.getConfigData();
        this.messageManager = messageManager;
        switchState(GameState.NOT_LOADED);
    }

    public void switchState(GameState to) {
        GameStateChangeEvent gameStateChangeEvent = new GameStateChangeEvent(currentGameState, to);
        Bukkit.getPluginManager().callEvent(gameStateChangeEvent);
        if (!gameStateChangeEvent.isCancelled()) this.currentGameState = to;
    }

    public IGame getGame() {
        return game;
    }

    public void setGame(IGame game) {
        this.game = game;
    }

    public GameState getCurrentGameState() {
        return currentGameState;
    }

    public IGame startNextGame() {
        if (activeGameSettings.isEmpty()) return null;

        Random random = new Random();
        IGameSettings settings = activeGameSettings.get(random.nextInt(activeGameSettings.size()));

        String worldName = settings.getId();
        World world = worldLoader.load(worldName);
        if (world == null) return null;

        world.setTime(6000);

        game = new Game(settings);
        Bukkit.setMaxPlayers(settings.getMaxPlayers());
        switchState(GameState.WAITING);
        return game;
    }

    public void loadGameSettings() {
        File dir = new File(dataFolder, "games");
        if (!dir.isDirectory()) dir.mkdirs();
        if (dir.listFiles().length == 0) {
            new GameSettings("example", dataFolder);
        } else {
            for (File file : dir.listFiles()) {
                if (file.getName().endsWith(".yml")) {
                    String id = file.getName();
                    id = id.substring(0, id.lastIndexOf('.'));
                    IGameSettings gameSettings = new GameSettings(id, dataFolder);
                    activeGameSettings.put(id, gameSettings);
                }
            }
        }
    }

    public void initGame() {
        this.game = startNextGame();
    }

    public void prepareLimboWorld() {
        worldLoader.loadFromRoot(mainConfig.limboWorld());
    }

    public void endGame() {
        boolean ducksWin = false;
        StringBuilder winners = new StringBuilder();

        for (IGamePlayer gamePlayer : game.getPlayers()) {
            if (gamePlayer.getPlayerRole().equals(PlayerRole.DUCK) && gamePlayer.didEndedRace()) {
                ducksWin = true;
                winners.append(gamePlayer.getBukkitPlayer().getName()).append(", ");
                gamePlayer.addStatistic("wins", 1);
                mainConfig.winnersCommand().forEach(command -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), messageManager.parsePlaceholdersWithoutColors(gamePlayer, command));
                });

                gamePlayer.getBukkitPlayer().sendActionBar(Component.empty());
                gamePlayer.getBukkitPlayer().setAllowFlight(true);
                gamePlayer.getBukkitPlayer().setFlying(true);
                gamePlayer.getBukkitPlayer().setFallDistance(0);
            }
        }

        if (!ducksWin) {
            for (IGamePlayer gamePlayer : game.getPlayers()) {
                if (gamePlayer.getPlayerRole().equals(PlayerRole.HUNTER)) {
                    winners.append(gamePlayer.getBukkitPlayer().getName()).append(", ");
                    gamePlayer.addStatistic("wins", 1);
                    mainConfig.winnersCommand().forEach(command -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), messageManager.parsePlaceholdersWithoutColors(gamePlayer, command));
                    });

                    gamePlayer.getBukkitPlayer().sendActionBar(Component.empty());
                    gamePlayer.getBukkitPlayer().setAllowFlight(true);
                    gamePlayer.getBukkitPlayer().setFlying(true);
                    gamePlayer.getBukkitPlayer().setFallDistance(0);
                }
            }
        } else {
            for (IGamePlayer gamePlayer : game.getPlayers()) {
                if (gamePlayer.getPlayerRole().equals(PlayerRole.HUNTER)) {
                    gamePlayer.addStatistic("loses", 1);
                    gamePlayer.getBukkitPlayer().sendActionBar(Component.empty());
                    gamePlayer.getBukkitPlayer().setAllowFlight(true);
                    gamePlayer.getBukkitPlayer().setFlying(true);
                    gamePlayer.getBukkitPlayer().setFallDistance(0);
                }
            }
        }

        String players = winners.toString();
        players = players.substring(0, players.length() - 2);

        for (IGamePlayer gamePlayer : game.getPlayers()) {
            gamePlayer.getBukkitPlayer().sendMessage(messageManager.parsePlaceholders(gamePlayer, StaticReplacer.replacer()
                    .set("players", players)
                    .apply(messagesConfig.playerSection().winners())));
        }
    }

    public void updateConfigData(ConfigManager<MainConfig> mainConfigManager, ConfigManager<MessagesConfig> messagesConfigManager) {
        this.mainConfig = mainConfigManager.getConfigData();
        this.messagesConfig = messagesConfigManager.getConfigData();
    }
}
