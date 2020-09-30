package net.kaikk.mc.bcl;

import guru.franz.mc.bcl.config.Config;
import guru.franz.mc.bcl.utils.ChunkLoaderHelper;
import guru.franz.mc.bcl.utils.Messenger;
import guru.franz.mc.bcl.utils.Permission;
import net.kaikk.mc.bcl.datastore.DataStoreManager;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
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

            if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent() && player.getItemInHand(HandTypes.MAIN_HAND).get().getType()
                    .equals(Config.getInstance().getItemType())) {
                boolean adminLoader =
                        chunkLoader != null && chunkLoader.isAdminChunkLoader() && player.hasPermission(Permission.ABILITY_ADMINLOADER);
                // if the chunkloader is not on this server or the player can edit chunkloader or if it is an admin chunkloader then we should show
                // the UI
                if (!ChunkLoaderOnThisServer || (player.getUniqueId().equals(chunkLoader.getOwner()) || player
                        .hasPermission(Permission.ABILITY_EDIT_OTHERS) || adminLoader)) {
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
                            "Iron and Diamond blocks can be converted into chunk loaders. Right click it with a ",Config.getInstance().getItemName(),"."));
                }
            }
        }
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        BlockSnapshot block = event.getTransactions().get(0).getOriginal();
        if (
            block == null //TODO: in what cases could this block be empty?
            || (!block.getState().getType().equals(BlockTypes.DIAMOND_BLOCK) && !block.getState().getType().equals(BlockTypes.IRON_BLOCK))
            || !block.getLocation().isPresent()
        ) {
            return;
        }

        CChunkLoader chunkLoader = DataStoreManager.getDataStore().getChunkLoaderAt(block.getLocation().get());
        if (chunkLoader == null) {
            return;
        }

        ChunkLoaderHelper.removeChunkLoader(chunkLoader,event.getCause().last(Player.class).orElse(null));
    }

    @Listener
    public void onPlayerLogin(ClientConnectionEvent.Join event) {
        DataStoreManager.getDataStore().refreshPlayer(event.getTargetEntity().getUniqueId());

        //todo: This should really be with the world too
        List<CChunkLoader> clList = DataStoreManager.getDataStore().getChunkLoaders(event.getTargetEntity().getUniqueId());

        for (CChunkLoader chunkLoader : clList) {
            if (chunkLoader.getServerName().equalsIgnoreCase(Config.getInstance().getServerName())
                && !chunkLoader.isAlwaysOn() && chunkLoader.blockCheck()) {
                BetterChunkLoader.instance().loadChunks(chunkLoader);
            }

        }
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        //todo: This should really be with the world too
        List<CChunkLoader> clList = DataStoreManager.getDataStore().getChunkLoaders(event.getTargetEntity().getUniqueId());

        for (CChunkLoader chunkLoader : clList) {
            if (chunkLoader.getServerName().equalsIgnoreCase(Config.getInstance().getServerName()) && !chunkLoader.isAlwaysOn()) {
                BetterChunkLoader.instance().unloadChunks(chunkLoader);
            }
        }
    }


    @Listener
    public void onWorldLoad(LoadWorldEvent event) {
        try {
            BetterChunkLoader.instance().getLogger().info("Loading world " + event.getTargetWorld().getName());
            DataStoreManager.getDataStore().loadWorld(event.getTargetWorld().getName());
            for (CChunkLoader cl : DataStoreManager.getDataStore().getChunkLoaders(event.getTargetWorld().getName())) {

                if (cl.isLoadable()) {
                    BetterChunkLoader.instance().loadChunks(cl);
                }
            }
        }catch(RuntimeException e){
            Messenger.logException(e);
        }
    }

    @Listener
    public void onReload(GameReloadEvent event){

        try {
            BetterChunkLoader.instance().setupPlugin();
        } catch (Exception e) {
            Messenger.logException(e);
        }
    }
}
