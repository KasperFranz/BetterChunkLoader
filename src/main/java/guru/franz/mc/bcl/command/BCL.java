package guru.franz.mc.bcl.command;

import guru.franz.mc.bcl.BetterChunkLoader;
import guru.franz.mc.bcl.BetterChunkLoaderPluginInfo;
import guru.franz.mc.bcl.utils.Messages;
import guru.franz.mc.bcl.utils.Messenger;
import guru.franz.mc.bcl.utils.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class BCL implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) {

        if (!BetterChunkLoader.instance().enabled) {
            commandSource.sendMessage(Text.builder(Messages.PLUGIN_DISABLED_DATASTORE).color(Messenger.ERROR_COLOR).build());
            return CommandResult.empty();
        }

        //TODO we should really find a better way to handle this!
        Text.Builder message =
                Text.builder().append(BetterChunkLoaderPluginInfo.prefix).append(Text.builder("Commands").color(TextColors.LIGHT_PURPLE)
                        .build());

        if (commandSource.hasPermission(Permission.COMMAND_BALANCE)) {
            message.append(Text.NEW_LINE).append(Text.builder("/bcl bal").color(TextColors.BLUE).build());
        }
        if (commandSource.hasPermission(Permission.COMMAND_LIST_SELF)) {
            if (commandSource.hasPermission(Permission.COMMAND_LIST_OTHERS)) {
                message.append(Text.NEW_LINE).append(Text.builder("/bcl list [player]").color(TextColors.BLUE).build());
            } else {
                message.append(Text.NEW_LINE).append(Text.builder("/bcl list").color(TextColors.BLUE).build());
            }
        }
        if (commandSource.hasPermission(Permission.COMMAND_INFO)) {
            message.append(Text.NEW_LINE).append(Text.builder("/bcl info").color(TextColors.BLUE).build());
        }
        if (commandSource.hasPermission(Permission.COMMAND_CHUNKS)) {
            message.append(Text.NEW_LINE)
                    .append(Text.builder("/bcl chunks <add|set|remove> <player> <type> <amount>").color(TextColors.BLUE).build());
        }

        if (commandSource.hasPermission(Permission.COMMAND_DELETE_OWN)) {
            if (commandSource.hasPermission(Permission.COMMAND_DELETE_OTHERS)) {
                message.append(Text.NEW_LINE).append(Text.builder("/bcl delete [player]").color(TextColors.BLUE).build());
            } else {
                message.append(Text.NEW_LINE).append(Text.builder("/bcl delete").color(TextColors.BLUE).build());
            }
        }

        if (commandSource.hasPermission(Permission.COMMAND_PURGE)) {
            message.append(Text.NEW_LINE).append(Text.builder("/bcl purge").color(TextColors.BLUE).build());
        }
        commandSource.sendMessage(message.build());
        return CommandResult.success();
    }
}
