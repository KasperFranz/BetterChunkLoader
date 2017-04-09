package net.kaikk.mc.bcl.commands;

import com.google.common.collect.Lists;
import net.kaikk.mc.bcl.CChunkLoader;
import net.kaikk.mc.bcl.datastore.DataStoreManager;
import net.kaikk.mc.bcl.utils.Messenger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by ROB on 08/12/2016.
 */
public class CmdList implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        Optional<User> optionalUser = commandContext.getOne("user");
        UUID user;
        String name;

        if (optionalUser.isPresent()) {
            user = optionalUser.get().getUniqueId();
            name = optionalUser.get().getName();
        } else if (commandSource instanceof Player) {
            user = ((Player) commandSource).getUniqueId();
            name = commandSource.getName();

        } else {
            Messenger.senderNotPlayerError(commandSource);
            return CommandResult.empty();
        }
        List<CChunkLoader> clList;
        boolean showUser = false;
        if (commandContext.getOne("all").isPresent()) {
            clList = DataStoreManager.getDataStore().getChunkLoaders();
            name = "all";
            showUser = true;
        } else {
            clList = DataStoreManager.getDataStore().getChunkLoaders(user);
        }

        List<Text> texts = Lists.newArrayList();
        boolean finalShowUser = showUser;
        clList.forEach(chunkLoader -> {
            texts.add(chunkLoader.toText(finalShowUser));
        });

        if (texts.isEmpty()) {
            texts.add(Messenger.getNoChunkLoaders(name));
        }

        PaginationList.builder()
                .title(Text.of(TextColors.GOLD,name + " Chunkloaders"))
                .contents(texts)
                .padding(Text.of("-"))
                .sendTo(commandSource);

        return CommandResult.empty();
    }

}
