package net.kaikk.mc.bcl.datastore;

/**
 * Created by mcrob on 24/08/2016.
 */
public class ChunkLoader {
    protected int chunkX, chunkZ;
    protected String worldName;
    protected byte range;

    protected ChunkLoader() { }

    public ChunkLoader(int chunkX, int chunkZ, String worldName, byte range) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.worldName = worldName;
        this.range = range;
    }

    public byte getRange() {
        return range;
    }
    public String getWorldName() {
        return worldName;
    }
    public int getChunkX() {
        return chunkX;
    }
    public int getChunkZ() {
        return chunkZ;
    }

    @Override
    public String toString() {
        return this.worldName+":"+this.chunkX+","+this.chunkZ;
    }

    /** is the provided chunk coords pair contained in this chunk loader range? */
    public boolean contains(int chunkX, int chunkZ) {
        return this.chunkX-range<=chunkX && chunkX<=this.chunkX+range && this.chunkZ-range<=chunkZ && chunkZ<=this.chunkZ+range;
    }
}
