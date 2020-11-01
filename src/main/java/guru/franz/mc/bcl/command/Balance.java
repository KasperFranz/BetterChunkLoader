package guru.franz.mc.bcl.command;

import guru.franz.mc.bcl.utils.Messages;
import guru.franz.mc.bcl.utils.Messenger;
import net.kaikk.mc.bcl.BetterChunkLoader;
import net.kaikk.mc.bcl.datastore.DataStoreManager;
import net.kaikk.mc.bcl.datastore.IDataStore;
import net.kaikk.mc.bcl.datastore.PlayerData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class Balance implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {

        if (!BetterChunkLoader.instance().enabled) {
            commandSource.sendMessage(Text.builder(Messages.PLUGIN_DISABLED_DATASTORE).color(Messenger.ERROR_COLOR).build());
            return CommandResult.empty();
        }

        Optional<User> optionalUser = commandContext.getOne("user");
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            commandSource.sendMessage(chunksInfo(user));
        } else if (commandSource instanceof Player) {
            commandSource.sendMessage(chunksInfo((Player) commandSource));
        } else {
            commandSource.sendMessage(Messenger.senderNotPlayerError());
        }


        return CommandResult.success();
    }

    private static Text chunksInfo(User user) {
        IDataStore dataStore = DataStoreManager.getDataStore();
        PlayerData playerData = dataStore.getPlayerData(user.getUniqueId());

        return Messenger.sendChunkBalance(
                user.getName(),
                dataStore.getOnlineOnlyFreeChunksAmount(user.getUniqueId()),
                dataStore.getAlwaysOnFreeChunksAmount(user.getUniqueId()),
                playerData.getOnlineOnlyChunksAmount(),
                playerData.getAlwaysOnChunksAmount()
        );
    }
}
