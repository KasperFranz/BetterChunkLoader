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

public class LoaderTypeElement extends CommandElement {

    public LoaderTypeElement(Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        String arg = commandArgs.next();
        if (arg.equalsIgnoreCase("world") || arg.equalsIgnoreCase("personal")) {
            return arg;
        }

        throw commandArgs.createError(Text.of(TextColors.RED, arg, " is not a valid argument!"));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        List<String> list = new ArrayList<>();
        list.add("world");
        list.add("personal");
        return list;
    }

}