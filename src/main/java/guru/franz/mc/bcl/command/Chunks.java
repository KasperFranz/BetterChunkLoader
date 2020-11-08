package guru.franz.mc.bcl.command;

import guru.franz.mc.bcl.command.types.EnabledCommand;
import guru.franz.mc.bcl.config.Config;
import guru.franz.mc.bcl.datastore.DataStoreManager;
import guru.franz.mc.bcl.exception.NegativeValueException;
import guru.franz.mc.bcl.model.PlayerData;
import guru.franz.mc.bcl.utils.Messenger;
import guru.franz.mc.bcl.utils.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

public class Chunks extends EnabledCommand {

    protected CommandResult executeCommand(CommandSource sender, CommandContext commandContext) {

        if (!sender.hasPermission(Permission.COMMAND_CHUNKS)) {
            Messenger.sendNoPermission(sender);
            return CommandResult.empty();

        }

        String chunksChangeOperatorElement = commandContext.<String>getOne("change").get();
        String loaderTypeElement = commandContext.<String>getOne("type").get();
        User user = commandContext.<Player>getOne("user").get();
        int changeValue = commandContext.<Integer>getOne("value").get();
        PlayerData playerData = DataStoreManager.getDataStore().getPlayerData(user.getUniqueId());

        if (changeValue < 0) {
            Messenger.sendNegativeValue(sender);
            return CommandResult.empty();
        }

        switch (chunksChangeOperatorElement) {

            case "add":
                return this.commandPartAdd(sender, playerData, user, changeValue, loaderTypeElement);

            case "set":
                return this.commandPartSet(sender, user, changeValue, loaderTypeElement);

            case "remove":
                return this.commandPartRemove(sender, playerData, user, changeValue, loaderTypeElement);

            default:
                Messenger.sendUsage(sender, "chunks");
                return CommandResult.success();
        }
    }

    private CommandResult commandPartAdd(CommandSource sender, PlayerData playerData, User user, int changeValue, String loaderTypeElement) {
        int newValue = changeValue;
        int maxWorldChunks = Config.getInstance().getMaxChunksAmount().getWorld();
        int maxOnlineOnlyChunks = Config.getInstance().getMaxChunksAmount().getPersonal();

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

    private CommandResult commandPartSet(CommandSource sender, User user, int changeValue, String loaderTypeElement) {

        try {

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
        } catch (NegativeValueException e) {
            Messenger.sendNegativeValue(sender);
            return CommandResult.empty();
        }
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
    private CommandResult commandPartRemove(CommandSource sender, PlayerData playerData, User user, int remove, String type) {
        try {
            int newValue;
            //todo we should be able to make it a bit prettier if we can get rid of the 2 blocks as the current setup is a code smell.
            if (type.equalsIgnoreCase("world")) {
                newValue = playerData.getAlwaysOnChunksAmount() - remove;
                DataStoreManager.getDataStore().setAlwaysOnChunksLimit(playerData.getPlayerId(), newValue);
                sender.sendMessage(Messenger.getRemoveMessage(user.getName(), remove, "world chunks"));
                return CommandResult.success();

            } else if (type.equalsIgnoreCase("personal")) {
                newValue = playerData.getOnlineOnlyChunksAmount() - remove;
                DataStoreManager.getDataStore().setOnlineOnlyChunksLimit(playerData.getPlayerId(), newValue);
                sender.sendMessage(Messenger.getRemoveMessage(user.getName(), remove, "personal chunks"));
                return CommandResult.success();
            }
        } catch (NegativeValueException e) {
            Messenger.sendNegativeValue(sender);
            return CommandResult.empty();
        }
        Messenger.sendUsage(sender, "chunks");
        return CommandResult.empty();
    }

}
