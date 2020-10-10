package guru.franz.mc.bcl.utils;

import net.kaikk.mc.bcl.BetterChunkLoader;
import net.kaikk.mc.bcl.CChunkLoader;
import net.kaikk.mc.bcl.datastore.DataStoreManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ChunkLoaderHelper {


    /**
     * Calculate one side of the radius from the range
     * @param range the range
     * @return the amount of blocks per side
     */
    public static int calculateSide(int range) {
        return 1 + (range * 2);
    }

    /**
     * Get the radius from a range (e.g.
     * 0 > 1x1
     * 1 > 3x3
     * 2 > 5x5
     * @param range the range
     * @return a string of the radius
     */
    public static String getRadiusFromRange(int range) {
        return calculateSide(range) + "x" + calculateSide(range);
    }

    /**
     * Calculate the amount of chunks based on the range
     * @param range the range
     * @return the amount of chunks based on the range
     */
    public static int calculateChunksFromRange(int range){
        return calculateSide(range)*calculateSide(range);
    }

    /**
     * Remove the chunkloader from the datastore and send the owner and player a message (if not the same)
     * @param chunkLoader the chunkloader we have deleted
     * @param player The player removing the chunkloader
     */
    public static void removeChunkLoader(CChunkLoader chunkLoader, Player player){
        // remove the chunk loader
        DataStoreManager.getDataStore().removeChunkLoader(chunkLoader);
        String executor = "unknown";

        if(player != null){
            executor = player.getName();
        }

        String logMessage = String.format(
                Messages.DELETE_CHUNKLOADER_LOG_SELF,
                executor,
                chunkLoader.getOwnerName()
        );


        String userMessage = String.format(
                Messages.DELETE_CHUNKLOADER_USER_SELF,
                chunkLoader.getLocationString()
        );

        if (!chunkLoader.getOwnerName().equals(executor)) {
            logMessage = String.format(
                    Messages.DELETE_CHUNKLOADER_LOG_OTHER,
                    executor,
                    chunkLoader.getOwnerName(),
                    chunkLoader.getLocationString()
            );
            userMessage = String.format(
                    Messages.DELETE_CHUNKLOADER_USER_OTHER,
                    chunkLoader.getOwnerName(),
                    chunkLoader.getLocationString()
            );


            //send message to the owner
            if(chunkLoader.getPlayer() != null) {
                chunkLoader.getPlayer().sendMessage(Text.of(TextColors.RED,
                        String.format(
                                Messages.DELETE_CHUNKLOADER_USER_INFORM,
                                executor,
                                chunkLoader.getLocationString()
                        )));
            }
        }

        BetterChunkLoader.instance().getLogger().info(logMessage);

        if(player != null) {
            player.sendMessage(Text.of(TextColors.RED, userMessage));
        }
    }
}
