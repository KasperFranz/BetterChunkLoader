package guru.franz.mc.bcl.command;

import guru.franz.mc.bcl.command.types.EnabledCommand;
import guru.franz.mc.bcl.datastore.DataStoreManager;
import guru.franz.mc.bcl.datastore.IDataStore;
import guru.franz.mc.bcl.model.PlayerData;
import guru.franz.mc.bcl.utils.Messenger;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class Balance extends EnabledCommand {


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

    protected CommandResult executeCommand(CommandSource commandSource, CommandContext commandContext) {
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
}
