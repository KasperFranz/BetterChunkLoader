package guru.franz.mc.bcl.command;

import guru.franz.mc.bcl.BetterChunkLoader;
import guru.franz.mc.bcl.command.types.EnabledCommand;
import guru.franz.mc.bcl.model.CChunkLoader;
import guru.franz.mc.bcl.utils.Messages;
import guru.franz.mc.bcl.utils.Messenger;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Info extends EnabledCommand {

    protected CommandResult executeCommand(CommandSource commandSource, CommandContext commandContext) {


        if (!BetterChunkLoader.instance().enabled) {
            commandSource.sendMessage(Text.builder(Messages.PLUGIN_DISABLED_DATASTORE).color(Messenger.ERROR_COLOR).build());
            return CommandResult.empty();
        }

        List<CChunkLoader> chunkLoaders = BetterChunkLoader.instance().getActiveChunkloaders();
        if (chunkLoaders.isEmpty()) {
            Messenger.sendNoInfoMessage(commandSource);
            return CommandResult.success();
        }

        int alwaysOnLoaders = 0;
        int onlineOnlyLoaders = 0;
        int alwaysOnChunks = 0;
        int onlineOnlyChunks = 0;
        int players;

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

        players = loadedChunksForPlayer.size();

        Messenger.sendInfoMessage(commandSource, onlineOnlyLoaders, alwaysOnLoaders, onlineOnlyChunks, alwaysOnChunks, players);
        return CommandResult.success();
    }
}
