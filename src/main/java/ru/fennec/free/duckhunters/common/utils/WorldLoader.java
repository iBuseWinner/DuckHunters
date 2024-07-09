package ru.fennec.free.duckhunters.common.utils;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import ru.fennec.free.duckhunters.common.configs.ConfigManager;
import ru.fennec.free.duckhunters.handlers.database.configs.MainConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class WorldLoader {

    private static Plugin plugin;
    private MainConfig mainConfig;

    public WorldLoader(Plugin plugin, ConfigManager<MainConfig> mainConfigManager) {
        WorldLoader.plugin = plugin;
        this.mainConfig = mainConfigManager.getConfigData();
    }

    public World load(String worldName) {
        try {
            if (Bukkit.getWorld(worldName) != null) {
                return Bukkit.getWorld(worldName);
            }

            Path worldContainerDir = Bukkit.getWorldContainer().toPath().toAbsolutePath();
            Path worldBackupDir = plugin.getDataFolder().toPath()
                    .resolve(mainConfig.worldsDir())
                    .resolve(worldName);

            if (!Files.isDirectory(worldBackupDir)) {
                return null;
            }

            Path worldDir = worldContainerDir.resolve(worldName);
            FileUtils.copyDirectory(worldBackupDir, worldDir, StandardCopyOption.REPLACE_EXISTING);

            return loadFromRoot(worldName);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public World loadFromRoot(String worldName) {
        try {
            WorldCreator creator = new WorldCreator(worldName);
            creator.environment(World.Environment.NORMAL);
            creator.type(WorldType.FLAT);
            creator.generateStructures(false);
            creator.generator(new VoidChunkGenerator());

            World world = Bukkit.createWorld(creator);
            world.setAutoSave(false);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
            world.setGameRule(GameRule.DO_MOB_LOOT, false);
            world.setGameRule(GameRule.DO_INSOMNIA, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
            world.setTime(6000);
            return world;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void unload(World world) {
        try {
            world.getEntities().forEach(Entity::remove);
        } catch (Exception ex) { }
        Bukkit.getServer().unloadWorld(world, false);
        Path dir = world.getWorldFolder().toPath();
        FileUtils.deleteDirectory(dir);
    }

    public void updateConfigData(ConfigManager<MainConfig> mainConfigManager) {
        this.mainConfig = mainConfigManager.getConfigData();
    }

}
