package net.kaikk.mc.bcl.commands;

import net.kaikk.mc.bcl.BetterChunkLoader;
import net.kaikk.mc.bcl.utils.BCLPermission;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by ROB on 08/12/2016.
 */
public class CmdBCL implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        Text.Builder message = Text.builder().append(BetterChunkLoader.getPrefix()).append(Text.builder("Commands").color(TextColors.LIGHT_PURPLE)
                .build());
        if (commandSource.hasPermission(BCLPermission.COMMAND_BALANCE)) {
            message.append(Text.NEW_LINE).append(Text.builder("/bcl bal").color(TextColors.BLUE).build());
        }
        if (commandSource.hasPermission(BCLPermission.COMMAND_LIST_SELF)) {
            if(commandSource.hasPermission(BCLPermission.COMMAND_LIST_OTHERS)){
                message.append(Text.NEW_LINE).append(Text.builder("/bcl list [player]").color(TextColors.BLUE).build());
            }else {
                message.append(Text.NEW_LINE).append(Text.builder("/bcl list").color(TextColors.BLUE).build());
            }
        }
        if (commandSource.hasPermission(BCLPermission.COMMAND_INFO)) {
            message.append(Text.NEW_LINE).append(Text.builder("/bcl info").color(TextColors.BLUE).build());
        }
        if (commandSource.hasPermission(BCLPermission.COMMAND_CHUNKS)) {
            message.append(Text.NEW_LINE)
                    .append(Text.builder("/bcl chunks <add|set|remove> <player> <type> <amount>").color(TextColors.BLUE).build());
        }
        if (commandSource.hasPermission(BCLPermission.COMMAND_DELETE)) {
            message.append(Text.NEW_LINE).append(Text.builder("/bcl delete <player>").color(TextColors.BLUE).build());
        }
        if (commandSource.hasPermission(BCLPermission.COMMAND_PURGE)) {
            message.append(Text.NEW_LINE).append(Text.builder("/bcl purge").color(TextColors.BLUE).build());
        }
        commandSource.sendMessage(message.build());
        return CommandResult.success();
    }
}
