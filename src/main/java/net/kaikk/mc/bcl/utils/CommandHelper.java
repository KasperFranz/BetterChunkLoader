package net.kaikk.mc.bcl.utils;


        import org.spongepowered.api.Sponge;
        import org.spongepowered.api.command.CommandSource;
        import org.spongepowered.api.entity.living.player.Player;
        import org.spongepowered.api.text.Text;
        import org.spongepowered.api.text.action.TextActions;
        import org.spongepowered.api.text.format.TextColors;
        import org.spongepowered.api.text.format.TextStyles;
        import org.spongepowered.api.world.Location;
        import org.spongepowered.api.world.World;

        import java.util.function.Consumer;

public class CommandHelper {


    public static Consumer<CommandSource> createTeleportConsumer(CommandSource src, Location<World> location) {
        return teleport -> {
            CommandHelper.sendTeleport(src, location);
        };
    }

    public static void sendTeleport(CommandSource src, Location<World> location) {

        if (!(src instanceof Player)) {
            return;
        }
        Player player = (Player) src;

        if (!player.hasPermission(BCLPermission.ABILITY_TELEPORT)) {
            player.sendMessage(Text.of(Messenger.errorColor, "You do not have permission to use the teleport feature."));
            return;
        }

        Location<World> safeLocation = Sponge.getGame().getTeleportHelper().getSafeLocation(location, 9, 9).orElse(null);
        if (safeLocation == null) {
            player.sendMessage(
                    Text.builder().append(Text.of(Messenger.errorColor, "Location is not safe. "),
                    Text.builder().append(Text.of(TextColors.GREEN, "Are you sure you want to teleport here anyway?")).onClick(TextActions.executeCallback(createForceTeleportConsumer(player, location)))
                            .style(TextStyles.UNDERLINE).build()).build());
        } else {
            player.setLocation(safeLocation);
        }

    }

    public static Consumer<CommandSource> createForceTeleportConsumer(CommandSource src, Location<World> location) {
        return teleport -> {
            if (!(src instanceof Player)) {
                // ignore
                return;
            }
            Player player = (Player) src;
            if (!player.hasPermission(BCLPermission.ABILITY_TELEPORT)) {
                player.sendMessage(Text.of(Messenger.errorColor, "You do not have permission to use the teleport feature."));
                return;
            }

            player.setLocation(location);
        };
    }

}
