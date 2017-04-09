package net.kaikk.mc.bcl.commands.elements;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by Rob5Underscores on 10/12/2016.
 */
public class LoaderTypeElement extends CommandElement {

    public LoaderTypeElement(Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        String arg = commandArgs.next();
        if (arg.equalsIgnoreCase("world")) {
            return arg;
        }
        if (arg.equalsIgnoreCase("personal")) {
            return arg;
        }
        throw commandArgs.createError(Text.of(new Object[]{TextColors.RED, arg, " is not a valid argument!"}));
    }

    @Override
    public List<String> complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext commandContext) {
        List<String> list = new ArrayList();
        list.add("world");
        list.add("personal");
        return list;
    }

}