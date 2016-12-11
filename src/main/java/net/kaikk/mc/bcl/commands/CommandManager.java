package net.kaikk.mc.bcl.commands;

import net.kaikk.mc.bcl.commands.elements.ChunksChangeOperatorElement;
import net.kaikk.mc.bcl.commands.elements.LoaderTypeElement;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

/**
 * Created by ROB on 08/12/2016.
 */
public class CommandManager {

    private CommandSpec cmdBalance = CommandSpec.builder()
            .arguments(GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.string(Text.of("player")))))
            .executor(new CmdBalance())
            .build();

    private CommandSpec cmdInfo = CommandSpec.builder()
            .arguments(GenericArguments.none())
            .executor(new CmdInfo())
            .build();

    private CommandSpec cmdChunks = CommandSpec.builder()
            .arguments(new CommandElement[]{new ChunksChangeOperatorElement(Text.of("change")), GenericArguments.string(Text.of("player")), new LoaderTypeElement(Text.of("type")), GenericArguments.integer(Text.of("value"))})
            .executor(new CmdChunks())
            .permission("betterchunkloader.chunks")
            .build();

    //private CommandSpec .cmdList = CommandSpec.builder().arguments(new WorldElement(Text.of("world"))).executor(new CmdList()).build();

    private CommandSpec cmdDelete = CommandSpec.builder()
            .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("player"))))
            .executor(new CmdDelete())
            .permission("betterchunkloader.delete")
            .build();

    private CommandSpec cmdPurge = CommandSpec.builder()
            .executor(new CmdPurge())
            .permission("betterchunkloader.purge")
            .build();

    public CommandSpec bclCmdSpec = CommandSpec.builder()
            .child(this.cmdBalance, new String[]{"balance", "bal"})
            .child(this.cmdInfo, new String[]{"info", "i"})
            //.child(this.cmdList, new String[] { "list", "ls" })
            .child(this.cmdChunks, new String[]{"chunks", "c"})
            .child(this.cmdDelete, new String[]{"delete", "d"})
            .child(this.cmdPurge, new String[]{"purge"})
            .executor(new CmdBCL())
            .build();


}
