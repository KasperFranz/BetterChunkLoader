package guru.franz.mc.bcl.model;

import guru.franz.mc.bcl.config.Config;
import guru.franz.mc.bcl.exception.UserNotFound;
import guru.franz.mc.bcl.utils.Utilities;
import org.spongepowered.api.entity.living.player.User;

import java.util.UUID;

public class PlayerData {

    private final UUID playerId;
    private int alwaysOnChunksAmount, onlineOnlyChunksAmount;

    public PlayerData(UUID playerId) throws UserNotFound {
        this.playerId = playerId;
        User player = Utilities.getUserFromUUID(playerId);
        this.alwaysOnChunksAmount = Utilities.getOptionOrDefault(player, "bcl.world", Config.getInstance().getDefaultChunksAmount().getWorld());
        this.onlineOnlyChunksAmount = Utilities.getOptionOrDefault(player, "bcl.personal", Config.getInstance().getDefaultChunksAmount().getPersonal());
    }

    public PlayerData(UUID playerId, int alwaysOnChunksAmount, int onlineOnlyChunksAmount) {
        this.playerId = playerId;
        this.alwaysOnChunksAmount = alwaysOnChunksAmount;
        this.onlineOnlyChunksAmount = onlineOnlyChunksAmount;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    /**
     * Total amount of always on chunks that this player can load
     */
    public int getAlwaysOnChunksAmount() {
        return alwaysOnChunksAmount;
    }

    public void setAlwaysOnChunksAmount(int alwaysOnChunksAmount) {
        this.alwaysOnChunksAmount = alwaysOnChunksAmount;
    }

    /**
     * Total amount of online only chunks that this player can load
     */
    public int getOnlineOnlyChunksAmount() {
        return onlineOnlyChunksAmount;
    }

    public void setOnlineOnlyChunksAmount(int onlineOnlyChunksAmount) {
        this.onlineOnlyChunksAmount = onlineOnlyChunksAmount;
    }
}
