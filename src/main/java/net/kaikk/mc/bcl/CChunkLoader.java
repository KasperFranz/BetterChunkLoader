package net.kaikk.mc.bcl;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import net.kaikk.mc.bcl.config.Config;
import net.kaikk.mc.bcl.forgelib.ChunkLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;


@XmlRootElement
@XmlAccessorType(value=XmlAccessType.NONE)
public class CChunkLoader extends ChunkLoader {
	final public static UUID adminUUID = new UUID(0,1);
	private UUID owner;
	private Location<World> loc;
	private Date creationDate;
	private boolean isAlwaysOn;
	private String serverName;
	
	public CChunkLoader() { }
	
	public CChunkLoader(int chunkX, int chunkZ, String worldName, byte range, UUID owner, Location<World> loc, Date creationDate, boolean isAlwaysOn, String serverName) {
		super(chunkX, chunkZ, worldName, range);
		this.owner = owner;
		this.loc = loc;
		this.creationDate = creationDate;
		this.isAlwaysOn = isAlwaysOn;
		this.serverName = serverName;
	}
	
	public CChunkLoader(String location, byte range, UUID owner, Date creationDate, boolean isAlwaysOn, String serverName) {
		super(0, 0, "", range);
        this.serverName = serverName;
		this.setLocationString(location);
		this.owner = owner;
		this.creationDate = creationDate;
		this.isAlwaysOn = isAlwaysOn;
	}

	public boolean isExpired() {
		return System.currentTimeMillis()-this.getOwnerLastPlayed()>Config.getConfig().get().getNode("MaxHoursOffline").getInt()*3600000L;
	}

	public String getServer() {
	    return this.serverName;
    }

	public User getOfflinePlayer() {
		Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
		Optional<User> optUser = userStorage.get().get(this.owner);
		if (optUser.isPresent()) {
			User user = optUser.get();
			return user;
		}
		return null;
	}
	
	public Player getPlayer() {
		Optional<Player> onlinePlayer = Sponge.getServer().getPlayer(this.owner);
		if(onlinePlayer.isPresent()) {
			return onlinePlayer.get();
		}
		return null;
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
	
	public Text info() {
		return Text.builder("== Chunk Loader Info ==").color(TextColors.GOLD).build().concat(Text.NEW_LINE)
				.concat(Text.builder("Owner: "+this.getOwnerName()).build()).concat(Text.NEW_LINE)
				.concat(Text.builder("Position: "+this.loc.toString()).build()).concat(Text.NEW_LINE)
				.concat(Text.builder("Chunk: "+this.worldName+":"+this.chunkX+","+this.chunkZ).build()).concat(Text.NEW_LINE)
				.concat(Text.builder("Size: "+this.sizeX()).build());
	}
	
	public boolean isLoadable() {
		return (this.isOwnerOnline() || (this.isAlwaysOn && !this.isExpired())) && this.blockCheck();
	}
	
	public boolean blockCheck() {
		if (this.loc.getBlock()==null) {
			return false;
		}
		if (isAlwaysOn) {

			return this.loc.getBlock().getType().equals(BlockTypes.DIAMOND_BLOCK);
		} else {
			return this.loc.getBlock().getType().equals(BlockTypes.IRON_BLOCK);
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
	
	public Location<World> getLoc() {
		return loc;
	}

	public String getLocationString() {
		String locString = "";
		locString+=loc.getExtent().getName()+":"+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ();
		return locString;
	}

	public String getServerName() {
		return serverName;
	}

	
	@XmlAttribute(name="loc")
	public void setLocationString(String location) {
		try {
		    if(this.serverName.equalsIgnoreCase(Config.getConfig().get().getNode("ServerName").getString())) {
                String[] s = location.split(":");
                String[] coords = s[1].split(",");
                Integer x = Integer.valueOf(coords[0]);
                Integer y = Integer.valueOf(coords[1]);
                Integer z = Integer.valueOf(coords[2]);
                super.worldName = s[0];
                this.loc = new Location<World>(Sponge.getServer().getWorld(s[0]).get(), x, y, z);
                super.chunkX = this.loc.getChunkPosition().getX();
                super.chunkZ = this.loc.getChunkPosition().getZ();
            } else {
		        super.worldName = "Another server...";
            }
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

	public Inventory getInventory() {
		return null;
	}
	
	/** Shows the chunk loader's user interface to the specified player */
	void showUI(Player player) {
		String title = (this.range!=-1 ? "BCL:"+this.getOwnerName()+"@"+getLocationString() : "New "+(this.isAdminChunkLoader()?"Admin ":"")+"BetterChunkLoader");
		if (title.length()>32) {
			title=title.substring(0, 32);
		}
		InventoryArchetype inventoryArchetype = InventoryArchetype.builder().from(InventoryArchetypes.MENU_ROW).property(CChunkLoaderInvProp.of(this)).build("archid","archname");
		Inventory inventory = Inventory.builder().of(inventoryArchetype).property(InventoryTitle.PROPERTY_NAME , InventoryTitle.of(Text.of(title))).build(BetterChunkLoader.instance());
		if(this.range!=-1) {
            addInventoryOption(inventory, 0, ItemTypes.REDSTONE_TORCH, "Remove");
        }
        int pos = 2;
		for (byte i=0; i<5;) {
			addInventoryOption(inventory, pos, ItemTypes.MAP, "Size "+this.sizeX(i)+(this.getRange()==i?" [selected]":""));
			pos++;
            i++;
		}
		
		player.openInventory(inventory, Cause.of(NamedCause.simulated(player)));
	}

	public int radiusFromSide(int side){
	    return (side-1)/2;
    }

	public int side(byte i) {
		return 1+(i*2);
	}
	
	private String sizeX(byte i) {
		return this.side(i)+"x"+this.side(i);
	}


	private static void addInventoryOption(Inventory inventory, int position, ItemType icon, String name) {
		Iterable<Slot> slotIterable = inventory.slots();
		ItemStack itemStack = ItemStack.builder().itemType(icon).quantity(1).build();
		itemStack.offer(Keys.DISPLAY_NAME, Text.of(name));
		Integer iter = 0;
		for(Slot slot : slotIterable) {
			if(iter == position) {
				slot.set(itemStack);
			}
			iter++;
		}

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
