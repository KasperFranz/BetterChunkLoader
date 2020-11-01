package guru.franz.mc.bcl.utils;

import guru.franz.mc.bcl.model.CChunkLoader;
import org.spongepowered.api.entity.living.player.Player;

public class Permission {

    public static final String COMMAND_BALANCE = "betterchunkloader.balance.own";
    public static final String COMMAND_BALANCE_OTHERS = "betterchunkloader.balance.others";
    public static final String COMMAND_CHUNKS = "betterchunkloader.chunks";
    public static final String COMMAND_DELETE_OWN = "betterchunkloader.delete.own";
    public static final String COMMAND_DELETE_OTHERS = "betterchunkloader.delete.others";
    public static final String COMMAND_INFO = "betterchunkloader.info";
    public static final String COMMAND_LIST_OTHERS = "betterchunkloader.list.others";
    public static final String COMMAND_LIST_SELF = "betterchunkloader.list.own";
    public static final String COMMAND_LIST_ALL = "betterchunkloader.list.all";
    public static final String COMMAND_LIST_DELETE_OWN = "betterchunkloader.list.delete.own";
    public static final String COMMAND_LIST_DELETE_OTHERS = "betterchunkloader.list.delete.others";
    public static final String COMMAND_PURGE = "betterchunkloader.purge";
    public static final String COMMAND_RELOAD = "betterchunkloader.reload";

    public static final String ABILITY_EDIT_OTHERS = "betterchunkloader.edit.others";
    public static final String ABILITY_UNLIMITED = "betterchunkloader.unlimitedchunks";
    public static final String ABILITY_TELEPORT = "betterchunkloader.teleport";

    /**
     * Can the player delete the chunkloader
     * @param player The player to check
     * @param chunkLoader the chunkloader to check
     * @return if the player can delete the chunkloader or not
     */
    public static boolean canDeleteChunkLoader(Player player, CChunkLoader chunkLoader) {
        return player.hasPermission(COMMAND_LIST_DELETE_OTHERS) ||
                (chunkLoader.getPlayer().getUniqueId() == player.getUniqueId() && player.hasPermission(COMMAND_LIST_DELETE_OWN));
    }
}
