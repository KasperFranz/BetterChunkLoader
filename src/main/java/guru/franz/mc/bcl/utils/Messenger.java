package guru.franz.mc.bcl.utils;


import net.kaikk.mc.bcl.BetterChunkLoader;
import net.kaikk.mc.bcl.CChunkLoader;
import net.kaikk.mc.bcl.utils.CommandHelper;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;


public class Messenger {

    public static TextColor errorColor = TextColors.RED;
    public static TextColor baseColor = TextColors.GOLD;

    public static void sendInfoMessage(CommandSource sender, Integer personalLoaders, Integer worldLoaders, Integer personalChunks,
            Integer worldChunks, Integer playersLoading) {
        Text message = BetterChunkLoader.getPrefix().concat(Text.builder("Chunkloading Statistics").color(TextColors.LIGHT_PURPLE).build());
        message = message.concat(Text.NEW_LINE)
                .concat(Text.builder("Personal: " + personalLoaders + " loaders loading " + personalChunks + " chunks!").build());
        message =
                message.concat(Text.NEW_LINE).concat(Text.builder("World: " + worldLoaders + " loaders loading " + worldChunks + " chunks!").build());
        message = message.concat(Text.NEW_LINE).concat(Text.builder(playersLoading + " players loading chunks!").build());
        sender.sendMessage(message);
    }

    public static void sendNoInfoMessage(CommandSource sender) {
        sender.sendMessage(BetterChunkLoader.getPrefix().concat(Text.builder("No statistics available!").color(errorColor).build()));
    }

    public static void sendNoPermission(CommandSource sender) {
        sender.sendMessage(
                BetterChunkLoader.getPrefix().concat(Text.builder("You do not have permission to execute this command!").color(TextColors.DARK_RED).build()));
    }

    public static void sendUsage(CommandSource sender, String command) {
        Text message = BetterChunkLoader.getPrefix();
        switch (command) {
            case "delete":
                message = message.concat(Text.builder("Usage: /bcl delete (PlayerName)").color(errorColor).build());
                break;
            case "chunks":
                message = message.concat(Text.builder("Usage:").color(baseColor).build());
                message = message.concat(Text.NEW_LINE)
                        .concat(Text.builder("/bcl chunks <add|set|remove> <PlayerName> <world|personal> <amount>").build());
                break;
            default:
                message = message.concat(Text.builder("Unkown usage").color(errorColor).build());
                break;

        }
        sender.sendMessage(message);
    }

    public static void sendNegativeValue(CommandSource sender) {
        Text message = BetterChunkLoader.getPrefix();
        message = message.concat(Text.builder("The value needs to be higher than 0.").color(errorColor).build());
        sender.sendMessage(message);
    }

    public static Text senderNotPlayerError() {
        return  BetterChunkLoader.getPrefix().concat(Text.builder("This command can only be executed by a player!").build());
    }


    public static Text sendChunkBalance(String targetName, Integer freePersonal, Integer freeWorld, Integer totalPersonal,
            Integer totalWorld) {
        Text message = Text.builder(targetName + "'s Chunk Loader Balance").color(TextColors.BLUE).build();
        message = message.concat(Text.NEW_LINE).concat(Text.builder("Personal - ").style(TextStyles.BOLD).build())
                .concat(Text.builder("Free: " + freePersonal + " Used: " + (totalPersonal - freePersonal) + " Total: " + totalPersonal).build());
        message = message.concat(Text.NEW_LINE).concat(Text.builder("World - ").style(TextStyles.BOLD).build())
                .concat(Text.builder("Free: " + freeWorld + " Used: " + (totalWorld - freeWorld) + " Total: " + totalWorld).build());

        return message;
    }

    public static Text getMaxChunkInfo(int added, String user, int limit, String type) {
        return Text.builder(
                "Couldn't add " + added + " " + type + " to " + user + "'s balance because it would exceed the " + type + " limit of " + limit)
                .color(errorColor).build();
    }

    public static Text getAddedInfo(int added, String user, int newValue, String type) {
        return Text.builder("Added " + added + " " + type + " to " + user + "'s balance!").color(baseColor).build().concat(Text.NEW_LINE)
                .concat(Text.builder("Their world chunk balance is now " + newValue).build());
    }

    public static Text getSetMessage(String user, int newValue, String type) {
        return Text.builder("Set " + user + "'s " + type + " balance to " + newValue).color(baseColor).build();
    }

    public static Text getRemoveMessage(String user, int newValue, String type) {
        return Text.builder("Removed " + newValue + " " + type + " from " + user).color(baseColor).build();
    }


    public static Text getNoChunkLoaders(String name) {
        return Text.builder(name + " has no chunkloaders").color(baseColor).build();
    }


    public static Text getChunkText(CommandSource source, CChunkLoader chunkLoader, boolean showUser){
        TextColor color = chunkLoader.isAlwaysOn() ? TextColors.AQUA : TextColors.GREEN;
        String text = chunkLoader.isAlwaysOn() ? "World" : "Personal";

        Text.Builder builder = Text.builder()
                .append(Text.builder("[" + text + "] ").color(color).build());

        if(source.hasPermission(Permission.ABILITY_TELEPORT) && source instanceof Player){
            Location<World> location = chunkLoader.getLoc().add(0,1,0);
            Text tp = Text.builder("[TP] ").color(TextColors.DARK_BLUE)
                    .onClick(TextActions.executeCallback(CommandHelper.createTeleportConsumer(source, location )))
                    .onHover(TextActions.showText(Text.of("Click to teleport on top of the chunkloader")))
                    .build();
            builder.append(tp);
        }

        if (showUser) {
            builder.append(Text.of(chunkLoader.getOwnerName() + " "));
        }


        return builder
                .append(Text.builder(chunkLoader.sizeX(chunkLoader.getRange())).color(baseColor).build())
                .append(Text.of(" - "))
                .append(Text.builder(chunkLoader.getPrettyLocationString() + " ").color(baseColor).build())
                .toText();


    }

    public static void logException(Throwable e){
        Logger logger = BetterChunkLoader.instance().getLogger();
        StackTraceElement error = e.getStackTrace()[0];
        logger.error("Load failed: " + e.getMessage() + " (" + error.getClassName() + ":" + error.getLineNumber() + ")");
    }
}
