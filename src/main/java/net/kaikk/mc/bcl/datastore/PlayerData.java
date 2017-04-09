package net.kaikk.mc.bcl.datastore;

import net.kaikk.mc.bcl.config.Config;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PlayerData {

    private UUID playerId;
    private int alwaysOnChunksAmount, onlineOnlyChunksAmount;

    public PlayerData(UUID playerId) {
        this.playerId = playerId;
        this.alwaysOnChunksAmount = Config.getConfig().get().getNode("DefaultChunksAmount").getNode("World").getInt();
        this.onlineOnlyChunksAmount = Config.getConfig().get().getNode("DefaultChunksAmount").getNode("Personal").getInt();
    }

    public PlayerData(UUID playerId, int alwaysOnChunksAmount, int onlineOnlyChunksAmount) {
        this.playerId = playerId;
        this.alwaysOnChunksAmount = alwaysOnChunksAmount;
        this.onlineOnlyChunksAmount = onlineOnlyChunksAmount;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    @XmlAttribute(name = "id")
    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    /** Total amount of always on chunks that this player can load */
    public int getAlwaysOnChunksAmount() {
        return alwaysOnChunksAmount;
    }

    @XmlAttribute(name = "aon")
    public void setAlwaysOnChunksAmount(int alwaysOnChunksAmount) {
        this.alwaysOnChunksAmount = alwaysOnChunksAmount;
    }

    /** Total amount of online only chunks that this player can load */
    public int getOnlineOnlyChunksAmount() {
        return onlineOnlyChunksAmount;
    }

    @XmlAttribute(name = "oon")
    public void setOnlineOnlyChunksAmount(int onlineOnlyChunksAmount) {
        this.onlineOnlyChunksAmount = onlineOnlyChunksAmount;
    }
}
