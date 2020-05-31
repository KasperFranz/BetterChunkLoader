package net.kaikk.mc.bcl.commands;

import net.kaikk.mc.bcl.exceptions.NegativeValueException;
import net.kaikk.mc.bcl.config.Config;
import net.kaikk.mc.bcl.datastore.DataStoreManager;
import net.kaikk.mc.bcl.datastore.PlayerData;
import net.kaikk.mc.bcl.utils.BCLPermission;
import guru.franz.mc.bcl.utils.Messenger;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

/**
 * Created by ROB on 08/12/2016.
 */
public class CmdChunks implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource sender, CommandContext commandContext) {


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
            try {
                if (changeValue < 0) {

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
            }catch(NegativeValueException e){
                Messenger.sendNegativeValue(sender);
                return CommandResult.empty();
            }
        }

        if (chunksChangeOperatorElement.equalsIgnoreCase("remove")) {
            return this.remove(sender,playerData, user, changeValue, loaderTypeElement);
        }


        Messenger.sendUsage(sender, "chunks");
        return CommandResult.success();
    }

    /**
     *  remove chunk loaders from the user.
     *
     * @param sender The person/console sending the message
     * @param playerData The player we want to change data on.
     * @param user The  user we want to change data on.
     * @param remove the amount we want to remove.
     * @param type what type of chunk loader do we want to remove?
     * @return If we have removed it or not
     */
    // Todo: we shouldn't need to pass in user!
    private CommandResult remove(CommandSource sender,PlayerData playerData, User user, int remove, String type){
        try {
            int newValue = 0;
            //todo we should be able to make it a bit prettier if we can get rid of the 2 blocks as the current setup is a code smell.
            if (type.equalsIgnoreCase("world")) {
                newValue = playerData.getAlwaysOnChunksAmount() - remove;
                DataStoreManager.getDataStore().setAlwaysOnChunksLimit(playerData.getPlayerId(), newValue);
                sender.sendMessage(Messenger.getRemoveMessage(user.getName(), remove, "world chunks"));
                return CommandResult.success();

            } else if (type.equalsIgnoreCase("personal")) {
                System.out.println(newValue);
                newValue = playerData.getOnlineOnlyChunksAmount() - remove ;
                DataStoreManager.getDataStore().setOnlineOnlyChunksLimit(playerData.getPlayerId(), newValue);
                sender.sendMessage(Messenger.getRemoveMessage(user.getName(), remove, "personal chunks"));
                return CommandResult.success();
            }
        }catch(NegativeValueException e){
            Messenger.sendNegativeValue(sender);
            return CommandResult.empty();
        }
        Messenger.sendUsage(sender, "chunks");
        return CommandResult.empty();
    }

}
