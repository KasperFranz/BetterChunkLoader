package guru.franz.mc.bcl.inventory;

import net.kaikk.mc.bcl.CChunkLoader;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.item.inventory.property.AbstractInventoryProperty;

public class ChunkLoaderInvProp extends AbstractInventoryProperty<String, CChunkLoader> {

    public static final String PROPERTY_NAME = "chunkloaderdata";

    public ChunkLoaderInvProp(CChunkLoader cChunkLoader) {
        super(cChunkLoader);
    }

    public static ChunkLoaderInvProp of(CChunkLoader cChunkLoader) {
        return new ChunkLoaderInvProp(cChunkLoader);
    }

    @Override
    public int compareTo(Property<?, ?> o) {
        return 0;
    }
}
