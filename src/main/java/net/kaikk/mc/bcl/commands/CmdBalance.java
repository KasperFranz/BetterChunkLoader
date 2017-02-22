package net.kaikk.mc.bcl.commands;

import net.kaikk.mc.bcl.datastore.DataStoreManager;
import net.kaikk.mc.bcl.datastore.IDataStore;
import net.kaikk.mc.bcl.datastore.PlayerData;
import net.kaikk.mc.bcl.utils.Messenger;
import net.kaikk.mc.bcl.utils.Utilities;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

/**
 * Created by Rob5Underscores on 10/12/2016.
 */
public class CmdBalance implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        Optional<Player> optionalPlayer = commandContext.getOne("player");
        if(optionalPlayer.isPresent()){
            Player player = optionalPlayer.get();
            String playername = player.getName();
            if(player != null) {
                chunksInfo(commandSource, player);
                return CommandResult.success();
            } else {
                Messenger.sendTargetNotExist(commandSource, playername);
            }
        } else {
            if(commandSource instanceof Player) {
                chunksInfo(commandSource, (Player)commandSource);
                return CommandResult.success();
            } else {
                Messenger.senderNotPlayerError(commandSource);
            }
        }

        return null;
    }

    static void chunksInfo(CommandSource sender, Player player) {
        IDataStore dataStore = DataStoreManager.getDataStore();
        int freeWorld = dataStore.getAlwaysOnFreeChunksAmount(player.getUniqueId());
        int freePersonal = dataStore.getOnlineOnlyFreeChunksAmount(player.getUniqueId());
        PlayerData pd=dataStore.getPlayerData(player.getUniqueId());
        int totalWorld = pd.getAlwaysOnChunksAmount();
        int totalPersonal = pd.getOnlineOnlyChunksAmount();

        Messenger.sendChunkBalance(sender, player.getName(), freePersonal, freeWorld, totalPersonal, totalWorld);
    }
}
