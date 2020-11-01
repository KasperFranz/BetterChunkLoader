package guru.franz.mc.bcl.model;


import guru.franz.mc.bcl.BetterChunkLoader;
import guru.franz.mc.bcl.config.Config;
import guru.franz.mc.bcl.exception.NoWorldException;
import guru.franz.mc.bcl.exception.WrongServerException;
import guru.franz.mc.bcl.utils.ChunkLoaderHelper;
import guru.franz.mc.bcl.utils.Messenger;
import net.kaikk.mc.bcl.forgelib.ChunkLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

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

    public void setCreationDate(Date date) {
        this.creationDate = date;
    }

    public boolean isAlwaysOn() {
        return isAlwaysOn;
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

    public void setRange(byte range) {
        super.range = range;
    }

}
