package guru.franz.mc.bcl.config.nodes;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ConfigSerializable
public final class ItemsNode {

    @Setting("Selector")
    private ItemType selector = ItemTypes.BLAZE_ROD;
    // TODO: Add support for multiple blocks as chunkloaders?
    // @Setting(value = "AllowedBlocks", comment = "Blocks allowed to be used with the selector to make chunkloaders.")
    // private List<BlockType> allowedBlocks = Stream.of(BlockTypes.IRON_BLOCK, BlockTypes.DIAMOND_BLOCK)
    //         .collect(Collectors.toList());

    public ItemType getSelector() {
        return selector;
    }

    public void setSelector(ItemType selector) {
        this.selector = selector;
    }

    public String getSelectorName() {
        return getSelector().getTranslation().get();
    }
}
