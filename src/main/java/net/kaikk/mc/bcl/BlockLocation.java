package net.kaikk.mc.bcl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.NONE)
public class BlockLocation {
	@XmlAttribute
	private String world;
	@XmlAttribute
	private int x,y,z;
	
	BlockLocation() {}
	
	public BlockLocation(Block block) {
		this(block.getLocation());
	}
	
	public BlockLocation(Location location) {
		this(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
	
	public BlockLocation(String world, int x, int y, int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public World getWorld() {
		return Bukkit.getServer().getWorld(world);
	}
	
	public String getWorldName() {
		return this.world;
	}
	
	public Chunk getChunk() {
		return this.getWorld().getChunkAt(this.getChunkX(), this.getChunkZ());
	}
	
	public Block getBlock() {
		World world = this.getWorld();
		if (world==null) {
			return null;
		}
		return world.getBlockAt(x, y, z);
	}
	
	public Location getLocation() {
		Block block = this.getBlock();
		if (block==null) {
			return null;
		}
		return block.getLocation();
	}
	
	public int getChunkX() {
		return (int) (Math.floor(this.x/16.00));
	}
	
	public int getChunkZ() {
		return (int) (Math.floor(this.z/16.00));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BlockLocation) {
			BlockLocation loc = (BlockLocation) obj;
			return this.x == loc.x && this.y == loc.y && this.z == loc.z && this.world.equals(loc.world);
		} else if (obj instanceof Location) {
			Location loc = (Location) obj;
			return this.x == loc.getBlockX() && this.y == loc.getBlockY() && this.z == loc.getBlockZ() && this.world.equals(loc.getWorld().getName());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return 37*(37*(37*this.world.hashCode()+this.x)+this.y)+this.z;
	}
	
	@Override
	public String toString() {
		return this.world+":"+this.x+","+this.y+","+this.z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}
	
	
}