package net.kaikk.mc.bcl;

import net.kaikk.mc.bcl.config.Config;
import net.kaikk.mc.bcl.datastore.DataStoreManager;
import net.kaikk.mc.bcl.forgelib.BCLForgeLib;
import net.kaikk.mc.bcl.utils.BCLPermission;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Events {

    @Listener
    public void onPlayerInteractBlockSecondary(InteractBlockEvent.Secondary.MainHand event, @First Player player,
            @Getter("getTargetBlock") BlockSnapshot clickedBlock) {
        if (clickedBlock.getState().getType().equals(BlockTypes.DIAMOND_BLOCK) || clickedBlock.getState().getType().equals(BlockTypes.IRON_BLOCK)) {
            CChunkLoader chunkLoader = DataStoreManager.getDataStore().getChunkLoaderAt(clickedBlock.getLocation().get());
            boolean ChunkLoaderOnThisServer = chunkLoader != null;

            if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent() && player.getItemInHand(HandTypes.MAIN_HAND).get().getItem().getType()
                    .equals(ItemTypes.BLAZE_ROD)) {
                boolean adminLoader =
                        chunkLoader != null && chunkLoader.isAdminChunkLoader() && player.hasPermission(BCLPermission.ABILITY_ADMINLOADER);
                // if the chunkloader is not on this server or the player can edit chunkloader or if it is an admin chunkloader then we should show
                // the UI
                if (!ChunkLoaderOnThisServer || (player.getUniqueId().equals(chunkLoader.getOwner()) || player
                        .hasPermission(BCLPermission.ABILITY_EDIT_OTHERS) || adminLoader)) {
                    // if the chunkloader is not present lets make one!
                    if (chunkLoader == null) {
                        UUID uid = player.getUniqueId();
                        int x = (int) Math.floor(clickedBlock.getLocation().get().getBlockX() / 16.00);
                        int y = (int) Math.floor(clickedBlock.getLocation().get().getBlockZ() / 16.00);
                        String worldName = clickedBlock.getLocation().get().getExtent().getName();
                        boolean alwaysOn = clickedBlock.getState().getType().equals(BlockTypes.DIAMOND_BLOCK);
                        chunkLoader = new CChunkLoader(x, y, worldName, (byte) -1, uid, clickedBlock.getLocation().get(), null, alwaysOn);
                    }

                    chunkLoader.showUI(player);
                } else {
                    player.sendMessage(Text.of(TextColors.RED, "You can't edit others' chunk loaders."));
                }
            } else {
                if (ChunkLoaderOnThisServer) {
                    player.sendMessage(chunkLoader.info());
                } else {
                    player.sendMessage(Text.of(TextColors.GOLD,
                            "Iron and Diamond blocks can be converted into chunk loaders. Right click it with a blaze rod."));
                }
            }
        }
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        BlockSnapshot block = event.getTransactions().get(0).getOriginal();
        if (block == null || (!block.getState().getType().equals(BlockTypes.DIAMOND_BLOCK) && !block.getState().getType()
                .equals(BlockTypes.IRON_BLOCK))) {
            return;
        }

        CChunkLoader chunkLoader = DataStoreManager.getDataStore().getChunkLoaderAt(block.getLocation().get());
        if (chunkLoader == null) {
            return;
        }

        DataStoreManager.getDataStore().removeChunkLoader(chunkLoader);

        Optional<Player> player = event.getCause().last(Player.class);
        String breaker = player.isPresent() ? player.get().getName() : "unknown";
        Player owner = chunkLoader.getPlayer();
        if (owner != null && !owner.getName().equals(breaker)) {
            owner.sendMessage(
                    Text.of(TextColors.RED, "Your chunk loader at " + chunkLoader.getLocationString() + " has been removed by " + breaker + "."));
            player.get().sendMessage(Text.of(TextColors.RED, "You removed " + (owner != null ? owner.getName() : "unknown") + "'s chunk loader."));
        } else {
            player.get().sendMessage(Text.of(TextColors.RED, "You removed your chunk loader at " + chunkLoader.getLocationString() + "."));
        }

        BetterChunkLoader.instance().getLogger()
                .info(breaker + " broke " + chunkLoader.getOwnerName() + "'s chunk loader at " + chunkLoader.getLocationString());
    }

    @Listener
    public void onPlayerLogin(ClientConnectionEvent.Join event) {
        DataStoreManager.getDataStore().refreshPlayer(event.getTargetEntity().getUniqueId());

        //todo: This should really be with the world too
        List<CChunkLoader> clList = DataStoreManager.getDataStore().getChunkLoaders(event.getTargetEntity().getUniqueId());

        for (CChunkLoader chunkLoader : clList) {
            if (chunkLoader.getServerName().equalsIgnoreCase(Config.getConfig().get().getNode("ServerName").getString())) {
                if (!chunkLoader.isAlwaysOn() && chunkLoader.blockCheck()) {
                    BCLForgeLib.instance().addChunkLoader(chunkLoader);
                }
            }
        }
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        //todo: This should really be with the world too
        List<CChunkLoader> clList = DataStoreManager.getDataStore().getChunkLoaders(event.getTargetEntity().getUniqueId());

        for (CChunkLoader chunkLoader : clList) {
            if (chunkLoader.getServerName().equalsIgnoreCase(Config.getConfig().get().getNode("ServerName").getString())) {
                if (!chunkLoader.isAlwaysOn()) {
                    BCLForgeLib.instance().removeChunkLoader(chunkLoader);
                }
            }
        }
    }


    @Listener
    public void onWorldLoad(LoadWorldEvent event) {
        for (CChunkLoader cl : DataStoreManager.getDataStore().getChunkLoaders(event.getTargetWorld().getName())) {
            if (cl.isLoadable()) {
                BCLForgeLib.instance().addChunkLoader(cl);
            }
        }
    }
}

