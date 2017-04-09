package net.kaikk.mc.bcl.commands;

import net.kaikk.mc.bcl.config.Config;
import net.kaikk.mc.bcl.datastore.DataStoreManager;
import net.kaikk.mc.bcl.datastore.PlayerData;
import net.kaikk.mc.bcl.utils.BCLPermission;
import net.kaikk.mc.bcl.utils.Messenger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

/**
 * Created by ROB on 08/12/2016.
 */
public class CmdChunks implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource sender, CommandContext commandContext) throws CommandException {


        if (!sender.hasPermission(BCLPermission.COMMAND_CHUNKS)) {
            Messenger.sendNoPermission(sender);
            return null;
        }

        String chunksChangeOperatorElement = commandContext.<String>getOne("change").get();
        String loaderTypeElement = commandContext.<String>getOne("type").get();
        User user = commandContext.<Player>getOne("user").get();
        Integer changeValue = commandContext.<Integer>getOne("value").get();
        PlayerData playerData = DataStoreManager.getDataStore().getPlayerData(user.getUniqueId());

        if (chunksChangeOperatorElement.equalsIgnoreCase("add")) {
            int newValue = changeValue;
            int maxWorldChunks = Config.getConfig().get().getNode("MaxChunksAmount").getNode("World").getInt();
            int maxOnlineOnlyChunks = Config.getConfig().get().getNode("MaxChunksAmount").getNode("Personal").getInt();

            if (loaderTypeElement.equalsIgnoreCase("world")) {
                newValue += playerData.getAlwaysOnChunksAmount();
                if (newValue > maxWorldChunks) {
                    sender.sendMessage(Messenger.getMaxChunkInfo(changeValue, user.getName(), maxWorldChunks, "world chunks"));
                    return CommandResult.empty();
                }

                DataStoreManager.getDataStore().addAlwaysOnChunksLimit(user.getUniqueId(), changeValue);
                sender.sendMessage(Messenger.getAddedInfo(changeValue, user.getName(), playerData.getAlwaysOnChunksAmount(), "world chunks"));
                return CommandResult.success();

            }

            if (loaderTypeElement.equalsIgnoreCase("personal")) {
                newValue += playerData.getOnlineOnlyChunksAmount();
                if (newValue > maxOnlineOnlyChunks) {
                    sender.sendMessage(Messenger.getMaxChunkInfo(changeValue, user.getName(), maxOnlineOnlyChunks, "personal chunks"));
                    return CommandResult.empty();
                }

                DataStoreManager.getDataStore().addOnlineOnlyChunksLimit(user.getUniqueId(), changeValue);
                sender.sendMessage(Messenger.getAddedInfo(changeValue, user.getName(), playerData.getOnlineOnlyChunksAmount(), "personal chunks"));
                return CommandResult.success();
            }


            Messenger.sendUsage(sender, "chunks");
            return CommandResult.empty();
        }

        if (chunksChangeOperatorElement.equalsIgnoreCase("set")) {
            if (changeValue < 0) {
                sender.sendMessage(Text.builder("Value cannot be less than 0.").build());
                return CommandResult.empty();
            }

            if (loaderTypeElement.equalsIgnoreCase("world")) {
                DataStoreManager.getDataStore().setAlwaysOnChunksLimit(user.getUniqueId(), changeValue);
                sender.sendMessage(Messenger.getSetMessage(user.getName(), changeValue, "world chunks"));
                return CommandResult.success();

            } else if (loaderTypeElement.equalsIgnoreCase("personal")) {
                DataStoreManager.getDataStore().setOnlineOnlyChunksLimit(user.getUniqueId(), changeValue);
                sender.sendMessage(Messenger.getSetMessage(user.getName(), changeValue, "personal chunks"));
                return CommandResult.success();
            }

            Messenger.sendUsage(sender, "chunks");
            return CommandResult.empty();
        }

        if (chunksChangeOperatorElement.equalsIgnoreCase("remove")) {
            int newValue = changeValue;
            if (changeValue < 0) {
                sender.sendMessage(Text.builder("Value cannot be less than 0.").build());
                return CommandResult.empty();
            }

            if (loaderTypeElement.equalsIgnoreCase("world")) {
                newValue += playerData.getAlwaysOnChunksAmount();
                DataStoreManager.getDataStore().setAlwaysOnChunksLimit(user.getUniqueId(), newValue);
                sender.sendMessage(Messenger.getRemoveMessage(user.getName(), changeValue, "world chunks"));
                return CommandResult.success();

            } else if (loaderTypeElement.equalsIgnoreCase("personal")) {
                newValue += playerData.getOnlineOnlyChunksAmount();
                DataStoreManager.getDataStore().setOnlineOnlyChunksLimit(user.getUniqueId(), newValue);
                sender.sendMessage(Messenger.getRemoveMessage(user.getName(), changeValue, "personal chunks"));
                return CommandResult.success();
            }

            Messenger.sendUsage(sender, "chunks");
            return CommandResult.empty();
        }


        Messenger.sendUsage(sender, "chunks");
        return CommandResult.success();
    }

}
