package net.kaikk.mc.bcl.commands;

import net.kaikk.mc.bcl.commands.elements.ChunksChangeElement;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

/**
 * Created by ROB on 08/12/2016.
 */
public class CommandManager {

    private CommandSpec cmdChunks;
    private CommandSpec cmdDelete;
    private CommandSpec cmdInfo;
    private CommandSpec cmdPurge;
    private CommandSpec cmdReload;

    public CommandManager()
    {
        this.cmdInfo = CommandSpec.builder().arguments(GenericArguments.none()).executor(new CmdInfo()).build();

        this.cmdChunks = CommandSpec.builder().arguments(new CommandElement[] { new ChunksChangeElement(Text.of("world")), new ItemElement(Text.of("itemType[:id]")) }).executor(new CmdChunks()).build();

        this.cmdList = CommandSpec.builder().arguments(new WorldElement(Text.of("world"))).executor(new CMDList()).build();

        this.cmdLog = CommandSpec.builder().arguments(GenericArguments.bool(Text.of("true|false"))).executor(new CMDLog()).build();

        this.cmdWhatsThis = CommandSpec.builder().executor(new CMDWhatsThis()).build();
    }

    public CommandSpec bclCmdSpec = CommandSpec.builder()
            .child(this.cmdInfo, new String[] { "info", "i" })
            .child(this.cmdChunks, new String[] { "chunks", "c" })
            .child(this.cmdList, new String[] { "list", "ls" })
            .child(this.cmdLog, new String[] { "log", "l" })
            .child(this.cmdWhatsThis, new String[] { "whatsthis", "wt" })
            .executor(new CmdBCL())
            .build();
}
