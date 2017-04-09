package net.kaikk.mc.bcl.commands;

import net.kaikk.mc.bcl.CChunkLoader;
import net.kaikk.mc.bcl.config.Config;
import net.kaikk.mc.bcl.datastore.DataStoreManager;
import net.kaikk.mc.bcl.datastore.IDataStore;
import net.kaikk.mc.bcl.utils.BCLPermission;
import net.kaikk.mc.bcl.utils.Messenger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ROB on 08/12/2016.
 */
public class CmdPurge implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {

        if (!commandSource.hasPermission(BCLPermission.COMMAND_PURGE)) {
            Messenger.sendNoPermission(commandSource);
            return CommandResult.empty();
        }

        IDataStore ds = DataStoreManager.getDataStore();
        List<CChunkLoader> chunkLoaders = new ArrayList<CChunkLoader>(DataStoreManager.getDataStore().getChunkLoaders());
        for (CChunkLoader cl : chunkLoaders) {
            if (!cl.blockCheck() && Config.getConfig().get().getNode("ServerName").getString().equalsIgnoreCase(cl.getServerName())) {
                ds.removeChunkLoader(cl);
            }
        }

        commandSource.sendMessage(Text.builder("All invalid chunk loaders have been removed.").color(TextColors.GREEN).build());

        return CommandResult.success();
    }
}
