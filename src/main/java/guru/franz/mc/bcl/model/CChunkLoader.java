package guru.franz.mc.bcl.model;


import guru.franz.mc.bcl.config.Config;
import guru.franz.mc.bcl.exception.NoWorldException;
import guru.franz.mc.bcl.exception.WrongServerException;
import guru.franz.mc.bcl.inventory.ChunkLoaderInvProp;
import guru.franz.mc.bcl.inventory.InventoryCloseAfterADelayTask;
import guru.franz.mc.bcl.utils.ChunkLoaderHelper;
import guru.franz.mc.bcl.utils.Messages;
import guru.franz.mc.bcl.utils.Messenger;
import guru.franz.mc.bcl.utils.Permission;
import guru.franz.mc.bcl.BetterChunkLoader;
import guru.franz.mc.bcl.datastore.DataStoreManager;
import net.kaikk.mc.bcl.forgelib.ChunkLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


@XmlRootElement
@XmlAccessorType(value = XmlAccessType.NONE)
public class CChunkLoader extends ChunkLoader {

    private final UUID owner;
    private Location<World> loc;
    private Date creationDate;
    private final boolean isAlwaysOn;
    private final String serverName;

    public CChunkLoader(int chunkX, int chunkZ, String worldName, byte range, UUID owner, Location<World> loc, Date creationDate, boolean isAlwaysOn,
            String serverName) {
        super(chunkX, chunkZ, worldName, range);
        this.owner = owner;
        this.loc = loc;
        this.creationDate = creationDate;
        this.isAlwaysOn = isAlwaysOn;
        this.serverName = serverName;
    }

    public CChunkLoader(int chunkX, int chunkZ, String worldName, byte range, UUID owner, Location<World> loc, Date creationDate,
            boolean isAlwaysOn) {
        this(chunkX, chunkZ, worldName, range, owner, loc, creationDate, isAlwaysOn, Config.getInstance().getServerName());

    }

    public CChunkLoader(String location, byte range, UUID owner, Date creationDate, boolean isAlwaysOn, String serverName) throws NoWorldException, WrongServerException {
        super(0, 0, "", range);
        this.serverName = serverName;
        this.setLocationString(location);
        this.owner = owner;
        this.creationDate = creationDate;
        this.isAlwaysOn = isAlwaysOn;
    }

    private static void addInventoryOption(Inventory inventory, int position, ItemType icon, String name) {
        Iterable<Slot> slotIterable = inventory.slots();
        ItemStack itemStack = ItemStack.builder().itemType(icon).quantity(1).build();
        itemStack.offer(Keys.DISPLAY_NAME, Text.of(name));
        int iter = 0;
        for (Slot slot : slotIterable) {
            if (iter == position) {
                slot.set(itemStack);
            }
            iter++;
        }

    }

    public static Consumer<ClickInventoryEvent> createClickEventConsumer(CChunkLoader chunkLoader) {
        return event -> {
            event.setCancelled(true);
            if (event.getCause().last(Player.class).isPresent()) {
                Player player = event.getCause().last(Player.class).get();

                if (chunkLoader == null) {
                    return;
                }

                if (chunkLoader.isAdminChunkLoader() && !player.hasPermission(Permission.ABILITY_ADMINLOADER)) {
                    Messenger.sendNoPermission(player);
                    return;
                }

                if (!player.getUniqueId().equals(chunkLoader.getOwner()) && !player.hasPermission(Permission.ABILITY_EDIT_OTHERS)) {
                    player.sendMessage(Text.of(TextColors.RED, "You can't edit others' chunk loaders."));
                    return;
                }

                if (!event.getTransactions().get(0).getOriginal().get(Keys.DISPLAY_NAME).isPresent()) {
                    return;
                }
                String firstChar = String.valueOf(event.getTransactions().get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain().charAt(5));
                int pos;

                try {
                    pos = Integer.parseInt(firstChar);
                } catch (NumberFormatException e) {
                    pos = 0;
                }


                //todo these 2 might be more useful combined as most of the logic is the same!.
                // -1 == create new chunkloader (as the old chunkLoaders range was 0)
                if (chunkLoader.getRange() == -1) {
                    pos = chunkLoader.radiusFromSide(pos);
                    if (!chunkLoader.isAdminChunkLoader() && !player.hasPermission(Permission.ABILITY_UNLIMITED)) {

                        int needed = (1 + (pos * 2)) * (1 + (pos * 2));
                        int available;
                        if (chunkLoader.isAlwaysOn()) {
                            available = DataStoreManager.getDataStore().getAlwaysOnFreeChunksAmount(chunkLoader.getOwner());
                        } else {
                            available = DataStoreManager.getDataStore().getOnlineOnlyFreeChunksAmount(chunkLoader.getOwner());
                        }

                        if (needed > available) {
                            player.sendMessage(Text.of(TextColors.RED,
                                    "You don't have enough free chunks! Needed: " + needed + ". Available: " + available + "."));
                            closeInventory(player);
                            return;
                        }
                    }

                    chunkLoader.setRange(Byte.parseByte("" + pos));
                    chunkLoader.setCreationDate(new Date());
                    String type = chunkLoader.isAdminChunkLoader() ? "admin loader" : "chunk loader";


                    String logMessage = String.format(
                            Messages.CREATE_CHUNKLOADER_LOG,
                            player.getName(),
                            type,
                            chunkLoader.getLocationString(),
                            chunkLoader.getRadius()
                    );
                    String userMessage = String.format(
                            Messages.CREATE_CHUNKLOADER_USER,
                            ChunkLoaderHelper.getRadiusFromRange(pos)
                    );

                    BetterChunkLoader.instance().getLogger().info(logMessage);
                    DataStoreManager.getDataStore().addChunkLoader(chunkLoader);
                    player.sendMessage(Text.of(TextColors.GOLD, userMessage));
                    closeInventory(player);
                    return;
                }
                if (chunkLoader.getRange() != -1) {
                    if (pos == 0) {
                        ChunkLoaderHelper.removeChunkLoader(chunkLoader, player);
                    } else {
                        pos = chunkLoader.radiusFromSide(pos);
                        // if higher range, check if the player has enough free chunks
                        if (!chunkLoader.isAdminChunkLoader() && !player.hasPermission(Permission.ABILITY_UNLIMITED) &&  pos > chunkLoader.getRange()) {
                            int needed = ((1 + (pos * 2)) * (1 + (pos * 2))) - chunkLoader.getSize();
                            int available;
                            if (chunkLoader.isAlwaysOn()) {
                                available = DataStoreManager.getDataStore().getAlwaysOnFreeChunksAmount(chunkLoader.getOwner());
                            } else {
                                available = DataStoreManager.getDataStore().getOnlineOnlyFreeChunksAmount(chunkLoader.getOwner());
                            }

                            if (needed > available) {
                                if (chunkLoader.getOwner().equals(player.getUniqueId())) {
                                    player.sendMessage(Text.of(TextColors.RED,
                                            "You don't have enough free chunks! Needed: " + needed + ". Available: " + available + "."));
                                } else {
                                    player.sendMessage(Text.of(TextColors.RED,
                                            chunkLoader.getOwnerName() + " don't have enough free chunks! Needed: " + needed + ". Available: "
                                                    + available + "."));
                                }
                                closeInventory(player);
                                return;
                            }
                        }

                        String logMessage = String.format(
                                Messages.EDIT_CHUNKLOADER_LOG_SELF,
                                player.getName(),
                                chunkLoader.getLocationString(),
                                chunkLoader.getRadius(),
                                ChunkLoaderHelper.getRadiusFromRange(pos)
                        );

                        if(!player.getUniqueId().equals(chunkLoader.getOwner())){
                            logMessage = String.format(
                                    Messages.EDIT_CHUNKLOADER_LOG_OTHER,
                                    player.getName(),
                                    chunkLoader.getOwnerName(),
                                    chunkLoader.getLocationString(),
                                    chunkLoader.getRadius(),
                                    ChunkLoaderHelper.getRadiusFromRange(pos)
                            );
                        }

                        String userMessage = String.format(
                                Messages.EDIT_CHUNKLOADER_USER,
                                ChunkLoaderHelper.getRadiusFromRange(pos)
                        );

                        BetterChunkLoader.instance().getLogger().info(logMessage);
                        DataStoreManager.getDataStore().changeChunkLoaderRange(chunkLoader, Byte.parseByte("" + pos));
                        player.sendMessage(Text.of(TextColors.GOLD, userMessage));
                    }
                    closeInventory(player);
                }
            }
        };
    }

    private static void closeInventory(final Player p) {
        Task.builder().execute(new InventoryCloseAfterADelayTask(p))
                .delay(10, TimeUnit.MILLISECONDS)
                .name("Closing players inventory.").submit(BetterChunkLoader.instance());
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - this.getOwnerLastPlayed() > Config.getInstance().getMaxHoursOffline() * 3600000L;
    }

    public User getOfflinePlayer() {
        Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
        return userStorage.get().get(this.owner).orElse(null);
    }

    public Player getPlayer() {
        Optional<Player> onlinePlayer = Sponge.getServer().getPlayer(this.owner);
        return onlinePlayer.orElse(null);
    }

    public long getOwnerLastPlayed() {
        return BetterChunkLoader.getPlayerLastPlayed(owner);
    }

    public String getOwnerName() {
        return this.getOfflinePlayer().getName();
    }

    /**
     * Get the size of the chunkloader
     * @return the number of chunks loaded by this chunkloader.
     */
    public int getSize() {
        return ChunkLoaderHelper.calculateChunksFromRange(range);
    }


    public Text info() {
        TextColor baseColor = TextColors.YELLOW;
        TextColor highlightColor = TextColors.GREEN;
        return Text.builder("== Chunk Loader Info ==").color(TextColors.GOLD).build().concat(Text.NEW_LINE)
                .concat(Text.of(highlightColor, "Owner: ")).concat(Text.of(baseColor, this.getOwnerName())).concat(Text.NEW_LINE)
                .concat(Text.of(highlightColor, "Position: ")).concat(Text.of(baseColor, this.getLocationString())).concat(Text.NEW_LINE)
                // .concat(Text.builder("Chunk: "+this.worldName+":"+this.chunkX+","+this.chunkZ).build()).concat(Text.NEW_LINE)
                .concat(Text.of(highlightColor, "Size: ")).concat(Text.of(baseColor, ChunkLoaderHelper.getRadiusFromRange(range)));
    }

    public boolean isLoadable() {
        return (this.isOwnerOnline() || (this.isAlwaysOn && !this.isExpired())) && this.blockCheck();
    }

    public boolean blockCheck() {
        if (this.loc.getBlock() == null) {
            return false;
        }
        if (isAlwaysOn) {

            return this.loc.getBlock().getType().equals(BlockTypes.DIAMOND_BLOCK);
        } else {
            return this.loc.getBlock().getType().equals(BlockTypes.IRON_BLOCK);
        }
    }

    public boolean isOwnerOnline() {
        return this.getPlayer() != null;
    }

    @Override
    public String toString() {
        return (this.isAlwaysOn ? "y" : "n") + " - " + ChunkLoaderHelper.getRadiusFromRange(range) + " - " + this.loc.toString() + " - " + this.serverName;
    }

    public Text toText(boolean showUser, CommandSource source) {
        return Messenger.getChunkText(source,this,showUser);
    }

    public UUID getOwner() {
        return owner;
    }

    public Location<World> getLoc() {
        return loc;
    }

    public String getLocationString() {
        return loc.getExtent().getName() + ":" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    public void setLocationString(String location) throws NoWorldException, WrongServerException {
            if (this.serverName.equalsIgnoreCase(Config.getInstance().getServerName())) {
                String[] s = location.split(":");
                String[] coordinates = s[1].split(",");
                int x = Integer.parseInt(coordinates[0]);
                int y = Integer.parseInt(coordinates[1]);
                int z = Integer.parseInt(coordinates[2]);
                super.worldName = s[0];

                World world = Sponge.getServer().getWorld(s[0]).orElseThrow(NoWorldException::new);

                this.loc = new Location<>(world, x, y, z);
                super.chunkX = this.loc.getChunkPosition().getX();
                super.chunkZ = this.loc.getChunkPosition().getZ();
            } else {
                throw new WrongServerException();
            }
    }

    public String getPrettyLocationString() {
        return loc.getExtent().getName() + " (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";

    }

    public String getServerName() {
        return serverName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    @XmlAttribute(name = "date")
    public void setCreationDate(Date date) {
        this.creationDate = date;
    }

    public boolean isAlwaysOn() {
        return isAlwaysOn;
    }

    /** Shows the chunk loader's user interface to the specified player */
    public void showUI(Player player) {
        String title = (this.range != -1 ? "BCL:" + this.getOwnerName() + "@" + getLocationString() :
                "New " + (this.isAdminChunkLoader() ? "Admin " : "") + "BetterChunkLoader");
        if (title.length() > 32) {
            title = title.substring(0, 32);
        }
        InventoryArchetype inventoryArchetype = InventoryArchetype.builder()
                .from(InventoryArchetypes.MENU_ROW)
                .property(ChunkLoaderInvProp.of(this)).build("archid", "archname");

        Inventory inventory = Inventory.builder()
                .of(inventoryArchetype)
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(title)))
                .listener(ClickInventoryEvent.class, createClickEventConsumer(this))
                .build(BetterChunkLoader.instance());
        if (this.range != -1) {
            addInventoryOption(inventory, 0, ItemTypes.REDSTONE_TORCH, "Remove");
        }

        int pos = 2;
        for (byte i = 0; i < 5; ) {
            addInventoryOption(inventory, pos, ItemTypes.MAP, "Size " + ChunkLoaderHelper.getRadiusFromRange(i) + (this.getRange() == i ? " [selected]" : ""));
            pos++;
            i++;
        }

        player.openInventory(inventory);
    }

    public int radiusFromSide(int side) {
        return (side - 1) / 2;
    }


    @Override
    public byte getRange() {
        return super.range;
    }

    public String getRadius(){
        return ChunkLoaderHelper.getRadiusFromRange(this.range);
    }

    @XmlAttribute(name = "r")
    public void setRange(byte range) {
        super.range = range;
    }

}
