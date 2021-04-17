package guru.franz.mc.bcl.chunkload;

import guru.franz.mc.bcl.model.CChunkLoader;
import net.kaikk.mc.bcl.forgelib.BCLForgeLib;

public class BCLForgeMod implements ChunkLoaderInterface {

    @Override public void removeChunkloader(CChunkLoader chunkloader) {

        BCLForgeLib.instance().removeChunkLoader(chunkloader);
    }

    @Override public void addChunkloader(CChunkLoader chunkloader) {

        BCLForgeLib.instance().addChunkLoader(chunkloader);
    }
}
