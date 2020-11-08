package guru.franz.mc.bcl.command.elements;

import guru.franz.mc.bcl.utils.Messages;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

import javax.annotation.Nullable;

public abstract class SimpleValueElement extends CommandElement {

    public SimpleValueElement(Text key) {
        super(key);
    }

    protected abstract List<String> getValues();

    @Nullable
    @Override
    protected Object parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        String arg = commandArgs.next();
        if (!getValues().contains(arg)) {
            throw commandArgs.createError(
                    Text.of(
                            TextColors.RED,
                            String.format(
                                    Messages.ARGUMENT_INVALID,
                                    arg,
                                    String.join(",", getValues())
                            )
                    )
            );
        }

        return arg;
    }

    @Override
    public List<String> complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext context) {
        return getValues();
    }

}
