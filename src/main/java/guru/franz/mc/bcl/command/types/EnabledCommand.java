package guru.franz.mc.bcl.command.types;

import guru.franz.mc.bcl.BetterChunkLoader;
import guru.franz.mc.bcl.utils.Messages;
import guru.franz.mc.bcl.utils.Messenger;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public abstract class EnabledCommand implements CommandExecutor {

    protected abstract CommandResult executeCommand(CommandSource sender, CommandContext commandContext);

    @Override
    public CommandResult execute(CommandSource sender, CommandContext commandContext) {
        if (!BetterChunkLoader.instance().enabled) {
            sender.sendMessage(Text.builder(Messages.PLUGIN_DISABLED_DATASTORE).color(Messenger.ERROR_COLOR).build());
            return CommandResult.empty();
        }
        return executeCommand(sender, commandContext);
    }
}
