package guru.franz.mc.bcl.command;

import guru.franz.mc.bcl.command.types.EnabledCommand;
import guru.franz.mc.bcl.config.Config;
import guru.franz.mc.bcl.utils.Messages;
import guru.franz.mc.bcl.utils.Messenger;
import guru.franz.mc.bcl.utils.Permission;
import guru.franz.mc.bcl.BetterChunkLoader;
import guru.franz.mc.bcl.model.CChunkLoader;
import guru.franz.mc.bcl.datastore.DataStoreManager;
import guru.franz.mc.bcl.datastore.IDataStore;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class Purge extends EnabledCommand {

    protected CommandResult executeCommand(CommandSource commandSource, CommandContext commandContext) {

        if(!BetterChunkLoader.instance().enabled){
            commandSource.sendMessage(Text.builder(Messages.PLUGIN_DISABLED_DATASTORE).color(Messenger.ERROR_COLOR).build());
            return CommandResult.empty();
        }

        if (!commandSource.hasPermission(Permission.COMMAND_PURGE)) {
            Messenger.sendNoPermission(commandSource);
            return CommandResult.empty();
        }

        IDataStore ds = DataStoreManager.getDataStore();
        List<CChunkLoader> chunkLoaders = new ArrayList<>(DataStoreManager.getDataStore().getChunkLoaders());
        for (CChunkLoader cl : chunkLoaders) {
            if (!cl.blockCheck() && cl.getServerName().equalsIgnoreCase(Config.getInstance().getServerName())) {
                ds.removeChunkLoader(cl);
            }
        }

        commandSource.sendMessage(Text.builder("All invalid chunk loaders have been removed.").color(TextColors.GREEN).build());

        return CommandResult.success();
    }
}
