package net.kaikk.mc.bcl.commands;

import net.kaikk.mc.bcl.BetterChunkLoader;
import net.kaikk.mc.bcl.commands.elements.ChunksChangeOperatorElement;
import net.kaikk.mc.bcl.commands.elements.LoaderTypeElement;
import net.kaikk.mc.bcl.config.Config;
import net.kaikk.mc.bcl.datastore.DataStoreManager;
import net.kaikk.mc.bcl.datastore.PlayerData;
import net.kaikk.mc.bcl.utils.Messenger;
import net.kaikk.mc.bcl.utils.Utilities;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import java.util.Optional;

/**
 * Created by ROB on 08/12/2016.
 */
public class CmdChunks implements CommandExecutor{
    @Override
    public CommandResult execute(CommandSource sender, CommandContext commandContext) throws CommandException {


        if (!sender.hasPermission("betterchunkloader.chunks")) {
            Messenger.sendNoPermission(sender);
            return null;
        }

        String chunksChangeOperatorElement = commandContext.<String>getOne("change").get();
        String loaderTypeElement = commandContext.<String>getOne("type").get();
        Player player = commandContext.<Player>getOne("player").get();
        String playerName = player.getName();
        Integer changeValue = commandContext.<Integer>getOne("value").get();

        if(player == null) {
            Messenger.sendTargetNotExist(sender, playerName);
            return null;
        }
        if (chunksChangeOperatorElement.equalsIgnoreCase("add")) {
            PlayerData playerData = DataStoreManager.getDataStore().getPlayerData(player.getUniqueId());
            if (loaderTypeElement.equalsIgnoreCase("world")) {
                if (playerData.getAlwaysOnChunksAmount()+changeValue> Config.getConfig().get().getNode("MaxChunksAmount").getNode("World").getInt()) {
                    sender.sendMessage(Text.builder("Couldn't add "+changeValue+" world chunks to "+player.getName()+"'s balance because it would exceed the world chunks limit of "+ Config.getConfig().get().getNode("MaxChunksAmount").getNode("World").getInt()).color(TextColors.RED).build());
                    return null;
                }

                DataStoreManager.getDataStore().addAlwaysOnChunksLimit(player.getUniqueId(), changeValue);
                sender.sendMessage(Text.builder("Added "+changeValue+" world chunks to "+player.getName()+"'s balance!").color(TextColors.GOLD).build().concat(Text.NEW_LINE).concat(Text.builder("Their world chunk balance is now "+ playerData.getAlwaysOnChunksAmount()).build()));

            } else if (loaderTypeElement.equalsIgnoreCase("personal")) {
                if (playerData.getOnlineOnlyChunksAmount()+changeValue>Config.getConfig().get().getNode("MaxChunksAmount").getNode("Personal").getInt()) {
                    sender.sendMessage(Text.builder("Couldn't add "+changeValue+" personal chunks to "+player.getName()+"'s balance because it would exceed the personal chunks limit of "+Config.getConfig().get().getNode("MaxChunksAmount").getNode("Personal").getInt()).build());
                    return null;
                }

                DataStoreManager.getDataStore().addOnlineOnlyChunksLimit(player.getUniqueId(), changeValue);
                sender.sendMessage(Text.builder("Added "+changeValue+" personal chunks to "+player.getName()+"'s balance!").color(TextColors.GOLD).build().concat(Text.NEW_LINE).concat(Text.builder("Their personal chunk balance is now "+ playerData.getOnlineOnlyChunksAmount()).build()));
            } else {
                Messenger.sendUsage(sender, "chunks");
                return null;
            }
        } else if (chunksChangeOperatorElement.equalsIgnoreCase("set")) {
            if (changeValue < 0) {
                sender.sendMessage(Text.builder("Value cannot be less than 0.").build());
                return null;
            }

            if (loaderTypeElement.equalsIgnoreCase("world")) {
                DataStoreManager.getDataStore().setAlwaysOnChunksLimit(player.getUniqueId(), changeValue);
                sender.sendMessage(Text.builder("Set "+player.getName()+"'s world chunk balance to "+changeValue).color(TextColors.GOLD).build());
            } else if (loaderTypeElement.equalsIgnoreCase("personal")) {
                DataStoreManager.getDataStore().setOnlineOnlyChunksLimit(player.getUniqueId(), changeValue);

                sender.sendMessage(Text.builder("Set "+player.getName()+"'s personal chunk balance to "+changeValue).color(TextColors.GOLD).build());

            } else {
                Messenger.sendUsage(sender, "chunks");
                return null;
            }
        } else {
            Messenger.sendUsage(sender, "chunks");
            return null;
        }

        return CommandResult.success();
    }

}
