package net.kaikk.mc.bcl.commands;

import guru.franz.mc.bcl.utils.Messages;
import net.kaikk.mc.bcl.BetterChunkLoader;
import net.kaikk.mc.bcl.CChunkLoader;
import guru.franz.mc.bcl.utils.Messenger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by ROB on 08/12/2016.
 */
public class CmdInfo implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {


        if(!BetterChunkLoader.instance().enabled){
            commandSource.sendMessage(Text.builder(Messages.PLUGIN_DISABLED_DATASTORE).color(Messenger.ERROR_COLOR).build());
            return CommandResult.empty();
        }

        List<CChunkLoader> chunkLoaders = BetterChunkLoader.instance().getActiveChunkloaders();
        if (chunkLoaders.isEmpty()) {
            Messenger.sendNoInfoMessage(commandSource);
            return CommandResult.success();
        }

        int alwaysOnLoaders = 0, onlineOnlyLoaders = 0, alwaysOnChunks = 0, onlineOnlyChunks = 0, maxChunksCount = 0, players = 0;
        HashMap<UUID, Integer> loadedChunksForPlayer = new HashMap<>();

        for (CChunkLoader chunkLoader : chunkLoaders) {
            if (chunkLoader.isAlwaysOn()) {
                alwaysOnLoaders++;
                alwaysOnChunks += chunkLoader.getSize();
            } else {
                onlineOnlyLoaders++;
                onlineOnlyChunks += chunkLoader.getSize();
            }

            Integer count = loadedChunksForPlayer.get(chunkLoader.getOwner());
            if (count == null) {
                count = 0;
            }
            count += chunkLoader.getSize();
            loadedChunksForPlayer.put(chunkLoader.getOwner(), count);
        }

        loadedChunksForPlayer.remove(CChunkLoader.adminUUID);
        players = loadedChunksForPlayer.size();
/*
        for (Map.Entry<UUID, Integer> entry : loadedChunksForPlayer.entrySet()) {
            if (maxChunksCount < entry.getValue()) {
                maxChunksCount = entry.getValue();
                maxChunksPlayer = entry.getKey();
            }
        }

*/
        Messenger.sendInfoMessage(commandSource, onlineOnlyLoaders, alwaysOnLoaders, onlineOnlyChunks, alwaysOnChunks, players);
        return CommandResult.success();
    }
}
