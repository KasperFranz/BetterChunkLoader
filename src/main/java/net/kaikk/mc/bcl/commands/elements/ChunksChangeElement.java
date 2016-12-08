package net.kaikk.mc.bcl.commands.elements;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ROB on 08/12/2016.
 */
public class ChunksChangeElement extends CommandElement {

    public ChunksChangeElement(Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        String arg = commandArgs.next();
        if(arg.equalsIgnoreCase("set")){
            return arg;
        }
        if(arg.equalsIgnoreCase("add")){
            return arg;
        }
        if(arg.equalsIgnoreCase("remove")){
            return arg;
        }
        throw commandArgs.createError(Text.of(new Object[] { TextColors.RED, arg, " is not a valid argument!" }));
    }

    @Override
    public List<String> complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext commandContext) {
        List<String> list = new ArrayList();
        list.add("set");
        list.add("add");
        list.add("remove");
        return list;
    }
}
