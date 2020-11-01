package guru.franz.mc.bcl.command;

import com.google.common.collect.Lists;
import guru.franz.mc.bcl.utils.Messages;
import net.kaikk.mc.bcl.BetterChunkLoader;
import guru.franz.mc.bcl.model.CChunkLoader;
import net.kaikk.mc.bcl.datastore.DataStoreManager;
import guru.franz.mc.bcl.utils.Messenger;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;
import java.util.UUID;

public class ListCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) {

        if(!BetterChunkLoader.instance().enabled){
            commandSource.sendMessage(Text.builder(Messages.PLUGIN_DISABLED_DATASTORE).color(Messenger.ERROR_COLOR).build());
            return CommandResult.empty();
        }

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
            commandSource.sendMessage(Messenger.senderNotPlayerError());
            return CommandResult.empty();
        }
        java.util.List<CChunkLoader> clList;
        boolean showUser = false;
        if (commandContext.getOne("all").isPresent()) {
            clList = DataStoreManager.getDataStore().getChunkLoaders();
            name = "all";
            showUser = true;
        } else {
            clList = DataStoreManager.getDataStore().getChunkLoaders(user);
        }

        java.util.List<Text> texts = Lists.newArrayList();
        boolean finalShowUser = showUser;
        clList.forEach(chunkLoader -> texts.add(chunkLoader.toText(finalShowUser,commandSource)));

        if (texts.isEmpty()) {
            texts.add(Text.of(
                    TextColors.GOLD,
                    String.format(Messages.LIST_NO_CHUNKLOADERS,name)
            ));
        }

        PaginationList.builder()
                .title(Text.of(
                        TextColors.GOLD,
                        String.format(Messages.LIST_CHUNKLOADERS_TITLE,name)
                ))
                .contents(texts)
                .padding(Text.of("-"))
                .sendTo(commandSource);

        return CommandResult.empty();
    }

}
