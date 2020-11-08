package guru.franz.mc.bcl.config.nodes;

import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public final class MaxChunksAmountNode extends ChunksAmountNode {

    public MaxChunksAmountNode() {
        this.personal = 250;
        this.world = 250;
    }
}
