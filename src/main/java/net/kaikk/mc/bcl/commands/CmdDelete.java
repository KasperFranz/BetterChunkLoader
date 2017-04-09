package net.kaikk.mc.bcl.commands;

import net.kaikk.mc.bcl.BetterChunkLoader;
import net.kaikk.mc.bcl.CChunkLoader;
import net.kaikk.mc.bcl.datastore.DataStoreManager;
import net.kaikk.mc.bcl.utils.BCLPermission;
import net.kaikk.mc.bcl.utils.Messenger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
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
            return CommandResult.empty();
        }

        if (!commandContext.getOne("user").isPresent()) {
            Messenger.sendUsage(sender, "delete");
            return CommandResult.empty();
        }

        User user = commandContext.<User>getOne("user").get();
        UUID playerUUID = user.getUniqueId();

        List<CChunkLoader> clList = DataStoreManager.getDataStore().getChunkLoaders(playerUUID);
        if (clList == null) {
            sender.sendMessage(Text.builder("This player doesn't have any chunk loader.").color(TextColors.RED).build());
            return CommandResult.empty();
        }

        DataStoreManager.getDataStore().removeChunkLoaders(playerUUID);
        sender.sendMessage(Text.builder("All chunk loaders placed by this player have been removed!").color(TextColors.GREEN).build());
        BetterChunkLoader.instance().getLogger().info(sender.getName() + " deleted all chunk loaders placed by " + user.getName());

        return CommandResult.success();
    }
}
