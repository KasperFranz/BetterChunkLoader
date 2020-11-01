package guru.franz.mc.bcl.utils;


import guru.franz.mc.bcl.BetterChunkLoaderPluginInfo;
import guru.franz.mc.bcl.exception.ConfigLoadException;
import net.kaikk.mc.bcl.BetterChunkLoader;
import guru.franz.mc.bcl.model.CChunkLoader;
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

import java.util.function.Consumer;


public class Messenger {

    public static final TextColor ERROR_COLOR = TextColors.RED;
    public static final TextColor baseColor = TextColors.GOLD;


    public static void sendInfoMessage(CommandSource sender, Integer personalLoaders, Integer worldLoaders, Integer personalChunks,
            Integer worldChunks, Integer playersLoading) {
        Text message = BetterChunkLoaderPluginInfo.prefix.concat(Text.builder("Chunkloading Statistics").color(TextColors.LIGHT_PURPLE).build());
        message = message.concat(Text.NEW_LINE)
                .concat(Text.builder("Personal: " + personalLoaders + " loaders loading " + personalChunks + " chunks!").build());
        message =
                message.concat(Text.NEW_LINE).concat(Text.builder("World: " + worldLoaders + " loaders loading " + worldChunks + " chunks!").build());
        message = message.concat(Text.NEW_LINE).concat(Text.builder(playersLoading + " players loading chunks!").build());
        sender.sendMessage(message);
    }

    public static void sendNoInfoMessage(CommandSource sender) {
        sender.sendMessage(BetterChunkLoaderPluginInfo.prefix.concat(Text.builder("No statistics available!").color(ERROR_COLOR).build()));
    }

    public static void sendNoPermission(CommandSource sender) {
        sender.sendMessage(
                BetterChunkLoaderPluginInfo.prefix.concat(Text.builder("You do not have permission to execute this command!").color(TextColors.DARK_RED).build()));
    }

    public static void sendUsage(CommandSource sender, String command) {
        Text message = BetterChunkLoaderPluginInfo.prefix;
        switch (command) {
            case "delete":
                message = message.concat(Text.builder("Usage: /bcl delete (PlayerName)").color(ERROR_COLOR).build());
                break;
            case "chunks":
                message = message.concat(Text.builder("Usage:").color(baseColor).build());
                message = message.concat(Text.NEW_LINE)
                        .concat(Text.builder("/bcl chunks <add|set|remove> <PlayerName> <world|personal> <amount>").build());
                break;
            default:
                message = message.concat(Text.builder("Unkown usage").color(ERROR_COLOR).build());
                break;

        }
        sender.sendMessage(message);
    }

    public static void sendNegativeValue(CommandSource sender) {
        Text message = BetterChunkLoaderPluginInfo.prefix;
        message = message.concat(Text.builder("The value needs to be higher than 0.").color(ERROR_COLOR).build());
        sender.sendMessage(message);
    }

    public static Text senderNotPlayerError() {
        return  BetterChunkLoaderPluginInfo.prefix.concat(Text.builder("This command can only be executed by a player!").build());
    }

    /**
     *
     * @param targetName The player
     * @param freePersonal The amount of free personal chunk loaders
     * @param freeWorld The amount of free world loaders
     * @param totalPersonal The total amount of personal chunk loaders
     * @param totalWorld The total amount of world chunk loaders
     * @return The chunk balance text
     */
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
                .color(ERROR_COLOR).build();
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

    public static Text getChunkText(CommandSource source, CChunkLoader chunkLoader, boolean showUser){
        TextColor color = chunkLoader.isAlwaysOn() ? TextColors.AQUA : TextColors.GREEN;
        String text = chunkLoader.isAlwaysOn() ? "World" : "Personal";

        Text.Builder builder = Text.builder()
                .append(Text.builder("[" + text + "] ").color(color).build());


        if (showUser) {
            builder.append(Text.of(chunkLoader.getOwnerName() + " "));
        }

        builder
                .append(Text.builder(ChunkLoaderHelper.getRadiusFromRange(chunkLoader.getRange())).color(baseColor).build())
                .append(Text.of(" - "))
                .append(Text.builder(chunkLoader.getPrettyLocationString() + " ").color(baseColor).build());

        //TELEPORT ACTION
        if(source.hasPermission(Permission.ABILITY_TELEPORT) && source instanceof Player){
            Location<World> location = chunkLoader.getLoc().add(0,1,0);
            Text tp = Text.builder(Messages.LIST_ACTION_TELEPORT +" ").color(TextColors.DARK_BLUE)
                    .onClick(TextActions.executeCallback(CommandHelper.createTeleportConsumer(source, location)))
                    .onHover(TextActions.showText(Text.of(Messages.LIST_ACTION_TELEPORT_HOVER)))
                    .build();
            builder.append(tp);
        }

        //DELETE ACTION
        if(source instanceof Player && Permission.canDeleteChunkLoader((Player) source, chunkLoader)){
            Text tp = Text.builder(Messages.LIST_ACTION_DELETE + " ").color(TextColors.RED)
                    .onClick(TextActions.executeCallback(CommandHelper.createDeleteChunkConsumer((Player) source, chunkLoader)))
                    .onHover(TextActions.showText(Text.of(Messages.LIST_ACTION_DELETE_HOVER)))
                    .build();
            builder.append(tp);
        }


        return builder
                .toText();
    }

    public static void logException(Throwable e){
        Logger logger = BetterChunkLoader.instance().getLogger();
        StackTraceElement error = e.getStackTrace()[0];

        String type = "";
        if(e instanceof ConfigLoadException){
            type = "Configuration issue";
        }

        logger.error("===================================================================");

        if(!type.isEmpty()){
            logger.error(type);
        }

        logger.error(e.getMessage());
        logger.error( "Happened at " + error.getClassName() + ":" + error.getLineNumber() + "");
        logger.error("===================================================================");
    }

    /**
     * Create an error message with correct color.
     * @param message the message to format.
     * @return the Text element.
     */
    public static Text createErrorMessage(String message) {
        return Text.builder(message).color(Messenger.ERROR_COLOR).build();
    }

    public static Text getConfirmBox(String message, Consumer<CommandSource> action){


        Text.Builder builder = Text.builder().append(Text.of(Messenger.baseColor, message))
                .append(Text.NEW_LINE)
                .append(
                        Text.builder("[Yes]").color(TextColors.GREEN)
                                .onClick(TextActions.executeCallback(action))
                                .build()
                );


        return builder.build();

    }
}
