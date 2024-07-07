package ru.fennec.free.duckhunters.handlers.game;

import org.bukkit.Bukkit;
import org.bukkit.World;
import ru.fennec.free.duckhunters.DuckHuntersPlugin;
import ru.fennec.free.duckhunters.common.events.GameStateChangeEvent;
import ru.fennec.free.duckhunters.common.interfaces.IGame;
import ru.fennec.free.duckhunters.common.interfaces.IGameSettings;
import ru.fennec.free.duckhunters.handlers.enums.GameState;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameManager {

    private DuckHuntersPlugin plugin;
    private IGame game;
    private GameState currentGameState;
    private File dataFolder;
    private WorldLoader worldLoader;

    private final Map<String, IGameSettings> activeGameSettings = new HashMap<>();

    public GameManager(DuckHuntersPlugin plugin, File dataFolder, WorldLoader worldLoader) {
        this.plugin = plugin;
        this.dataFolder = dataFolder;
        this.worldLoader = worldLoader;
        switchState(GameState.NOT_LOADED);
        //ToDo register listener loading
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
        //ToDo get world's name from config and load it from root
    }



}
