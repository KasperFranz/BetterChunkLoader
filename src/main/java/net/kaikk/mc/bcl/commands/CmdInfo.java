package net.kaikk.mc.bcl.commands;

import net.kaikk.mc.bcl.CChunkLoader;
import net.kaikk.mc.bcl.datastore.DataStoreManager;
import net.kaikk.mc.bcl.utils.Messenger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ROB on 08/12/2016.
 */
public class CmdInfo implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {

        List<CChunkLoader> chunkLoaders = DataStoreManager.getDataStore().getChunkLoaders();
        if (chunkLoaders.isEmpty()) {
            Messenger.sendNoInfoMessage(commandSource);
            return CommandResult.success();
        }

        int alwaysOnLoaders = 0, onlineOnlyLoaders = 0, alwaysOnChunks = 0, onlineOnlyChunks = 0, maxChunksCount = 0, players = 0;
        UUID maxChunksPlayer = null;
        HashMap<UUID, Integer> loadedChunksForPlayer = new HashMap<>();

        for (CChunkLoader chunkLoader : chunkLoaders) {
            if (chunkLoader.isAlwaysOn()) {
                alwaysOnLoaders++;
                alwaysOnChunks += chunkLoader.size();
            } else {
                onlineOnlyLoaders++;
                onlineOnlyChunks += chunkLoader.size();
            }

            Integer count = loadedChunksForPlayer.get(chunkLoader.getOwner());
            if (count == null) {
                count = 0;
            }
            count += chunkLoader.size();
            loadedChunksForPlayer.put(chunkLoader.getOwner(), count);
        }

        loadedChunksForPlayer.remove(CChunkLoader.adminUUID);
        players = loadedChunksForPlayer.size();

        for (Map.Entry<UUID, Integer> entry : loadedChunksForPlayer.entrySet()) {
            if (maxChunksCount < entry.getValue()) {
                maxChunksCount = entry.getValue();
                maxChunksPlayer = entry.getKey();
            }
        }


        Messenger.sendInfoMessage(commandSource, onlineOnlyLoaders, alwaysOnLoaders, onlineOnlyChunks, alwaysOnChunks, players);
        return CommandResult.success();
    }
}
