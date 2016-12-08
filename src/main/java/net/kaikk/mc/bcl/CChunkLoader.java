package net.kaikk.mc.bcl;

import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import net.kaikk.mc.bcl.forgelib.ChunkLoader;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.NONE)
public class CChunkLoader extends ChunkLoader implements InventoryHolder {
	final public static UUID adminUUID = new UUID(0,1);
	private UUID owner;
	private BlockLocation loc;
	private Date creationDate;
	private boolean isAlwaysOn;
	private String serverName;
	
	public CChunkLoader() { }
	
	public CChunkLoader(int chunkX, int chunkZ, String worldName, byte range, UUID owner, BlockLocation loc, Date creationDate, boolean isAlwaysOn, String serverName) {
		super(chunkX, chunkZ, worldName, range);
		this.owner = owner;
		this.loc = loc;
		this.creationDate = creationDate;
		this.isAlwaysOn = isAlwaysOn;
		this.serverName = serverName;
	}
	
	public CChunkLoader(String location, byte range, UUID owner, Date creationDate, boolean isAlwaysOn, String serverName) {
		super(0, 0, "", range);
		this.setLocationString(location);
		this.owner = owner;
		this.creationDate = creationDate;
		this.isAlwaysOn = isAlwaysOn;
		this.serverName = serverName;
	}

	public boolean isExpired() {
		return System.currentTimeMillis()-this.getOwnerLastPlayed()>BetterChunkLoader.instance().config().maxHoursOffline*3600000L;
	}

	public OfflinePlayer getOfflinePlayer() {
		return BetterChunkLoader.instance().getServer().getOfflinePlayer(this.owner);
	}
	
	public Player getPlayer() {
		return BetterChunkLoader.instance().getServer().getPlayer(this.owner);
	}
	
	public long getOwnerLastPlayed() {
		if (this.isAdminChunkLoader()) {
			return System.currentTimeMillis();
		}
		return BetterChunkLoader.getPlayerLastPlayed(owner);
	}
	
	public String getOwnerName() {
		if (this.isAdminChunkLoader()) {
			return "Admin";
		}
		return this.getOfflinePlayer().getName();
	}
	
	public int side() {
		return 1+(super.getRange()*2);
	}
	
	public int size() {
		return this.side()*this.side();
	}
	
	public String sizeX() {
		return this.side()+"x"+this.side();
	}
	
	public String info() {
		return ChatColor.GOLD + "== Chunk loader info ==\n"
				+ ChatColor.WHITE + "Owner: "+this.getOwnerName()+"\n"
						+ "Position: "+this.loc.toString()+"\n"
						+ "Chunk: "+this.worldName+":"+this.chunkX+","+this.chunkZ+"\n"
						+ "Size: "+this.sizeX();
	}
	
	public boolean isLoadable() {
		return (this.isOwnerOnline() || (this.isAlwaysOn && !this.isExpired())) && this.blockCheck();
	}
	
	public boolean blockCheck() {
		if (this.loc.getBlock()==null) {
			return false;
		}
		if (isAlwaysOn) {
			return this.loc.getBlock().getType()==Material.DIAMOND_BLOCK;
		} else {
			return this.loc.getBlock().getType()==Material.IRON_BLOCK;
		}
	}
	
	public boolean isOwnerOnline() {
		return this.getPlayer()!=null;
	}
	
	@Override
	public String toString() {
		return (this.isAlwaysOn?"y":"n")+" - "+this.sizeX()+" - "+this.loc.toString()+" - "+this.serverName;
	}
	
	public UUID getOwner() {
		return owner;
	}
	
	public BlockLocation getLoc() {
		return loc;
	}
	
	public String getLocationString() {
		return loc.toString();
	}

	public String getServerName() {
		return serverName;
	}

	
	@XmlAttribute(name="loc")
	public void setLocationString(String location) {
		try {
			String[] s = location.split(":");
			String[] coords = s[1].split(",");
			Integer x=Integer.valueOf(coords[0]);
			Integer y=Integer.valueOf(coords[1]);
			Integer z=Integer.valueOf(coords[2]);
			
			this.loc=new BlockLocation(s[0], x, y, z);
			
			super.worldName=s[0];
			super.chunkX=this.loc.getChunkX();
			super.chunkZ=this.loc.getChunkZ();
		} catch(Exception e) {
			throw new RuntimeException("Wrong chunk loader location: "+location);
		}
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public boolean isAlwaysOn() {
		return isAlwaysOn;
	}
	
	@XmlAttribute(name="date")
	public void setCreationDate(Date date) {
		this.creationDate=date;
	}
	
	/** Ignore this, it'll always return null */
	@Override
	public Inventory getInventory() {
		return null;
	}
	
	/** Shows the chunk loader's user interface to the specified player */
	void showUI(Player player) {
		String title = (this.range!=-1 ? "BCL:"+this.getOwnerName()+"@"+this.getLoc() : "New "+(this.isAdminChunkLoader()?"Admin ":"")+"BetterChunkLoader");
		if (title.length()>32) {
			title=title.substring(0, 32);
		}
		Inventory inventory = Bukkit.createInventory(this, 9, title);

		addInventoryOption(inventory, 0, Material.REDSTONE_TORCH_ON, "Remove");
		
		for (byte i=0; i<5; i++) {
			addInventoryOption(inventory, i+2, Material.MAP, "Size "+this.sizeX(i)+(this.getRange()==i?" [selected]":""));
		}
		
		player.openInventory(inventory);
	}
	
	private String sizeX(byte i) {
		return this.side(i)+"x"+this.side(i);
	}
	
	private int side(byte i) {
		return 1+(i*2);
	}

	private static void addInventoryOption(Inventory inventory, int position, Material icon, String name) {
		ItemStack is = new ItemStack(icon);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		is.setItemMeta(im);
		inventory.setItem(position, is);
	}
	
	@XmlAttribute(name="own")
	void setOwner(UUID owner) {
		this.owner = owner;
	}

	@XmlAttribute(name="aon")
	void setAlwaysOn(boolean isAlwaysOn) {
		this.isAlwaysOn = isAlwaysOn;
	}
	
	@Override
	public byte getRange() {
		return super.range;
	}
	
	@XmlAttribute(name="r")
	public void setRange(byte range) {
		super.range=range;
	}
	
	public boolean isAdminChunkLoader() {
		return adminUUID.equals(this.owner);
	}
}
