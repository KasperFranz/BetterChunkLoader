package net.kaikk.mc.bcl;

import org.spongepowered.api.data.Property;
import org.spongepowered.api.item.inventory.property.AbstractInventoryProperty;

/**
 * Created by Rob5Underscores on 10/12/2016.
 */
public class CChunkLoaderInvProp extends AbstractInventoryProperty<String, CChunkLoader>  {

    public static final String PROPERTY_NAME = "chunkloaderdata";

    public CChunkLoaderInvProp (CChunkLoader cChunkLoader) {
        super(cChunkLoader);
    }

    public static CChunkLoaderInvProp of(CChunkLoader cChunkLoader) {
        return new CChunkLoaderInvProp(cChunkLoader);
    }

    @Override
    public int compareTo(Property<?, ?> o) {
        return 0;
    }
}
