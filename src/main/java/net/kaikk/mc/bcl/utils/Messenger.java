package net.kaikk.mc.bcl.utils;

import static org.spongepowered.api.text.format.TextColors.BLUE;
import static org.spongepowered.api.text.format.TextColors.DARK_RED;
import static org.spongepowered.api.text.format.TextColors.GOLD;
import static org.spongepowered.api.text.format.TextColors.LIGHT_PURPLE;
import static org.spongepowered.api.text.format.TextColors.RED;
import static org.spongepowered.api.text.format.TextStyles.BOLD;

import net.kaikk.mc.bcl.BetterChunkLoader;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Rob5Underscores on 10/12/2016.
 */
public class Messenger {

    public static void sendInfoMessage(CommandSource sender, Integer personalLoaders, Integer worldLoaders, Integer personalChunks,
            Integer worldChunks, Integer playersLoading) {
        Text message = BetterChunkLoader.getPrefix().concat(Text.builder("Chunkloading Statistics").color(LIGHT_PURPLE).build());
        message = message.concat(Text.NEW_LINE)
                .concat(Text.builder("Personal: " + personalLoaders + " loaders loading " + personalChunks + " chunks!").build());
        message =
                message.concat(Text.NEW_LINE).concat(Text.builder("World: " + worldLoaders + " loaders loading " + worldChunks + " chunks!").build());
        message = message.concat(Text.NEW_LINE).concat(Text.builder(playersLoading + " players loading chunks!").build());
        sender.sendMessage(message);
    }

    public static void sendNoInfoMessage(CommandSource sender) {
        sender.sendMessage(BetterChunkLoader.getPrefix().concat(Text.builder("No statistics available!").color(RED).build()));
    }

    public static void sendNoPermission(CommandSource sender) {
        sender.sendMessage(
                BetterChunkLoader.getPrefix().concat(Text.builder("You do not have permission to execute this command!").color(DARK_RED).build()));
    }

    public static void sendUsage(CommandSource sender, String command) {
        Text message = BetterChunkLoader.getPrefix();
        switch (command) {
            case "delete":
                message = message.concat(Text.builder("Usage: /bcl delete (PlayerName)").color(RED).build());
                break;
            case "chunks":
                message = message.concat(Text.builder("Usage:").color(GOLD).build());
                message = message.concat(Text.NEW_LINE)
                        .concat(Text.builder("/bcl chunks <add|set|remove> <PlayerName> <world|personal> <amount>").build());
                break;
        }
        sender.sendMessage(message);
    }

    public static void senderNotPlayerError(CommandSource sender) {
        sender.sendMessage(BetterChunkLoader.getPrefix().concat(Text.builder("This command can only be executed by a player!").build()));
    }

    public static void sendTargetNotExist(CommandSource sender, String targetName) {
        sender.sendMessage(BetterChunkLoader.getPrefix()
                .concat(Text.builder(targetName + " is not online or has never joined this server!").color(DARK_RED).build()));
    }

    public static void sendChunkBalance(CommandSource sender, String targetName, Integer freePersonal, Integer freeWorld, Integer totalPersonal,
            Integer totalWorld) {
        Text message = Text.builder(targetName + "'s Chunk Loader Balance").color(BLUE).build();
        message = message.concat(Text.NEW_LINE).concat(Text.builder("Personal - ").style(BOLD).build())
                .concat(Text.builder("Free: " + freePersonal + " Used: " + (totalPersonal - freePersonal) + " Total: " + totalPersonal).build());
        message = message.concat(Text.NEW_LINE).concat(Text.builder("World - ").style(BOLD).build())
                .concat(Text.builder("Free: " + freeWorld + " Used: " + (totalWorld - freeWorld) + " Total: " + totalWorld).build());

        sender.sendMessage(message);
    }

    public static Text getMaxChunkInfo(int added, String user, int limit, String type) {
        return Text.builder(
                "Couldn't add " + added + " " + type + " to " + user + "'s balance because it would exceed the " + type + " limit of " + limit)
                .color(TextColors.RED).build();
    }

    public static Text getAddedInfo(int added, String user, int newValue, String type) {
        return Text.builder("Added " + added + " " + type + " to " + user + "'s balance!").color(TextColors.GOLD).build().concat(Text.NEW_LINE)
                .concat(Text.builder("Their world chunk balance is now " + newValue).build());
    }

    public static Text getSetMessage(String user, int newValue, String type) {
        return Text.builder("Set " + user + "'s " + type + " balance to " + newValue).color(TextColors.GOLD).build();
    }

    public static Text getRemoveMessage(String user, int newValue, String type) {
        return Text.builder("Removed " + newValue + " " + type + " from " + user).color(TextColors.GOLD).build();
    }


    public static Text getNoChunkLoaders(String name) {
        return Text.builder(name + " has no chunkloaders").color(TextColors.GOLD).build();
    }
}
