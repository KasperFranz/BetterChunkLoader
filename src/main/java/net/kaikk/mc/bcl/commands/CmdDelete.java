package net.kaikk.mc.bcl.commands;

import net.kaikk.mc.bcl.BetterChunkLoader;
import net.kaikk.mc.bcl.CChunkLoader;
import net.kaikk.mc.bcl.datastore.DataStoreManager;
import net.kaikk.mc.bcl.utils.BCLPermission;
import net.kaikk.mc.bcl.utils.Messenger;
import net.kaikk.mc.bcl.utils.Utilities;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.UUID;

/**
 * Created by ROB on 08/12/2016.
 */
public class CmdDelete implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource sender, CommandContext commandContext) throws CommandException {

        if (!sender.hasPermission(BCLPermission.COMMAND_DELETE)) {
            Messenger.sendNoPermission(sender);
            return null;
        }

        if (!commandContext.getOne("player").isPresent()) {
            Messenger.sendUsage(sender, "delete");
            return null;
        }
        String playerName = (String)commandContext.getOne("player").get();
        UUID playerUUID = Utilities.getUUIDFromName(playerName);
        if(playerUUID == null) {
            Messenger.sendTargetNotExist(sender, playerName);
        }

        List<CChunkLoader> clList = DataStoreManager.getDataStore().getChunkLoaders(playerUUID);
        if (clList==null) {
            sender.sendMessage(Text.builder("This player doesn't have any chunk loader.").color(TextColors.RED).build());
            return null;
        }

        DataStoreManager.getDataStore().removeChunkLoaders(playerUUID);
        sender.sendMessage(Text.builder("All chunk loaders placed by this player have been removed!").color(TextColors.GREEN).build());
        BetterChunkLoader.instance().getLogger().info(sender.getName()+" deleted all chunk loaders placed by "+playerName);

        return CommandResult.success();
    }
}
