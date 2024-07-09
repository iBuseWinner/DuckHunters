package ru.fennec.free.duckhunters.common.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WorldEditHook {

    public static void pasteLobbySchematic(Plugin plugin, String pathToSchematic, Location location) {
        File file = new File(plugin.getDataFolder(), pathToSchematic);
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(file);
        try {
            ClipboardReader reader = clipboardFormat.getReader(new FileInputStream(file));
            Clipboard clipboard = reader.read();

            World adaptedWorld = BukkitAdapter.adapt(location.getWorld());
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory()
                    .getEditSession(adaptedWorld, -1);
            Location wait = location.clone();
            Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                    .to(BlockVector3.at(wait.getX(), wait.getY(), wait.getZ())).ignoreAirBlocks(false).build();
            try {
                Operations.complete(operation);
                editSession.flushSession();
            } catch (WorldEditException ex) {
                ex.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
