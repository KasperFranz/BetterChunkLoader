package net.kaikk.mc.bcl.commands;

import net.kaikk.mc.bcl.datastore.DataStoreManager;
import net.kaikk.mc.bcl.datastore.IDataStore;
import net.kaikk.mc.bcl.datastore.PlayerData;
import net.kaikk.mc.bcl.utils.Messenger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import java.util.Optional;

/**
 * Created by Rob5Underscores on 10/12/2016.
 */
public class CmdBalance implements CommandExecutor {

    static void chunksInfo(CommandSource sender, User user) {
        IDataStore dataStore = DataStoreManager.getDataStore();
        int freeWorld = dataStore.getAlwaysOnFreeChunksAmount(user.getUniqueId());
        int freePersonal = dataStore.getOnlineOnlyFreeChunksAmount(user.getUniqueId());
        PlayerData pd = dataStore.getPlayerData(user.getUniqueId());
        int totalWorld = pd.getAlwaysOnChunksAmount();
        int totalPersonal = pd.getOnlineOnlyChunksAmount();

        Messenger.sendChunkBalance(sender, user.getName(), freePersonal, freeWorld, totalPersonal, totalWorld);
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        Optional<User> optionalUser = commandContext.getOne("user");
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String playername = user.getName();
            if (user != null) {
                chunksInfo(commandSource, user);
                return CommandResult.success();
            } else {
                Messenger.sendTargetNotExist(commandSource, playername);
            }
        } else {
            if (commandSource instanceof Player) {
                chunksInfo(commandSource, (Player) commandSource);
                return CommandResult.success();
            } else {
                Messenger.senderNotPlayerError(commandSource);
            }
        }

        return CommandResult.success();
    }
}
