package guru.franz.mc.bcl.command;

import guru.franz.mc.bcl.BetterChunkLoaderPluginInfo;
import guru.franz.mc.bcl.utils.Messenger;
import net.kaikk.mc.bcl.BetterChunkLoader;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class Reload  implements CommandExecutor {

    @Override public CommandResult execute(CommandSource src, CommandContext args) {
        Text.Builder message = Text.builder().append(BetterChunkLoaderPluginInfo.prefix);
        try {
            BetterChunkLoader.instance().setupPlugin();
            message.append(Text.builder("Reload success").color(Messenger.baseColor).build());
            src.sendMessage(message.build());
            return CommandResult.success();
        } catch (Exception e) {
            message.append(Text.builder("An exception happened while trying to reload the plugin, please see the console for more information.").color(Messenger.ERROR_COLOR).build());
            src.sendMessage(message.build());
            Messenger.logException(e);
        }

        return CommandResult.empty();
    }
}
