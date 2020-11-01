package guru.franz.mc.bcl.command;

import guru.franz.mc.bcl.command.types.EnabledCommand;
import guru.franz.mc.bcl.utils.Messages;
import guru.franz.mc.bcl.utils.Messenger;
import guru.franz.mc.bcl.utils.Permission;
import net.kaikk.mc.bcl.BetterChunkLoader;
import guru.franz.mc.bcl.model.CChunkLoader;
import net.kaikk.mc.bcl.datastore.DataStoreManager;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.function.Consumer;

public class Delete extends EnabledCommand {

    protected CommandResult executeCommand(CommandSource sender, CommandContext commandContext) {
        User user = (User) commandContext.getOne("user").orElse(null);
        if (sender.hasPermission(Permission.COMMAND_DELETE_OTHERS) && user != null) {
            return deleteOther(sender, user);
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Messenger.senderNotPlayerError());
            return CommandResult.empty();
        }

        return deleteOwn((Player) sender);
    }


    /**
     * Delete all Chunk Loaders of a specific user
     *
     * @param sender the Sender of the Command
     * @param user   The user you want to delete
     * @return The command Result.
     */
    private CommandResult deleteOther(CommandSource sender, User user) {
        if (!sender.hasPermission(Permission.COMMAND_DELETE_OTHERS)) {
            sender.sendMessage(Messenger.createErrorMessage(Messages.CMD_DELETE_OTHER_PERMISSION));
            return CommandResult.empty();
        }


        List<CChunkLoader> clList = DataStoreManager.getDataStore().getChunkLoaders(user.getUniqueId());
        if (clList.isEmpty()) {
            sender.sendMessage(Messenger.createErrorMessage(
                    String.format(
                            Messages.CMD_DELETE_OTHER_NO_CHUNKLOADERS,
                            user.getName()
                    )));
            return CommandResult.empty();
        }

        Integer removed = DataStoreManager.getDataStore().removeChunkLoaders(user.getUniqueId());
        sender.sendMessage(Text.builder(
                String.format(
                        Messages.CMD_DELETE_OTHER_SUCCESS,
                        removed,
                        user.getName()
                )).color(TextColors.GREEN).build());

        BetterChunkLoader.instance().getLogger().info(
                String.format(
                        Messages.CMD_DELETE_OTHER_SUCCESS_LOG,
                        sender.getName(),
                        user.getName()
                ));

        return CommandResult.success();

    }


    /**
     * Delete all chunkloaders of a Player
     *
     * @param player The player that wants to delete their chunk loaders
     * @return The command Result.
     */
    private CommandResult deleteOwn(Player player) {
        player.sendMessage(Messenger.getConfirmBox(Messages.CMD_DELETE_OWN_CONFIRM, createDeleteConsumer(player)));
        return CommandResult.empty();


    }

    /**
     * Create a Delete Chunk Loader consumer for an Player
     * @param player The player that wants to delete their Chunk Loaders
     * @return  The consumer to delete the players Chunk Loaders
     */
    public static Consumer<CommandSource> createDeleteConsumer(Player player) {
        return action -> {
            if (player == null) {
                return;
            }

            if (!player.hasPermission(Permission.COMMAND_DELETE_OWN)) {
                player.sendMessage(Messenger.createErrorMessage(Messages.CMD_DELETE_OWN_PERMISSION));
                return;
            }

            List<CChunkLoader> clList = DataStoreManager.getDataStore().getChunkLoaders(player.getUniqueId());
            if (clList.isEmpty()) {
                player.sendMessage(Messenger.createErrorMessage(Messages.CMD_DELETE_OWN_NO_CHUNKLOADERS));
                return;
            }

            Integer removed = DataStoreManager.getDataStore().removeChunkLoaders(player.getUniqueId());
            player.sendMessage(Text.builder(
                    String.format(
                            Messages.CMD_DELETE_OWN_SUCCESS,
                            removed
                    )).color(TextColors.GREEN).build());

            BetterChunkLoader.instance().getLogger().info(
                    String.format(
                            Messages.CMD_DELETE_OWN_SUCCESS_LOG,
                            player.getName()
                    ));
        };
    }

}
