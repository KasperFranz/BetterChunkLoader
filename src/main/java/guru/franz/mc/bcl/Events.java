package guru.franz.mc.bcl;

import com.flowpowered.math.vector.Vector3i;
import guru.franz.mc.bcl.config.Config;
import guru.franz.mc.bcl.config.node.ItemsNode;
import guru.franz.mc.bcl.datastore.DataStoreManager;
import guru.franz.mc.bcl.datastore.exceptions.MySQLConnectionException;
import guru.franz.mc.bcl.exception.Exception;
import guru.franz.mc.bcl.inventory.ChunkLoaderInventory;
import guru.franz.mc.bcl.model.CChunkLoader;
import guru.franz.mc.bcl.utils.ChunkLoaderHelper;
import guru.franz.mc.bcl.utils.Messenger;
import guru.franz.mc.bcl.utils.Permission;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
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
import java.util.UUID;

public class Events {

    @Listener
    public void onPlayerInteractBlockSecondary(InteractBlockEvent.Secondary.MainHand event, @First Player player,
            @Getter("getTargetBlock") BlockSnapshot clickedBlock) {
        ItemsNode itemsNode = Config.getInstance().getItems();
        if (clickedBlock.getState().getType().equals(BlockTypes.DIAMOND_BLOCK) || clickedBlock.getState().getType().equals(BlockTypes.IRON_BLOCK)) {
            CChunkLoader chunkLoader = DataStoreManager.getDataStore().getChunkLoaderAt(clickedBlock.getLocation().get());
            boolean hasChunkLoader = chunkLoader != null;
            boolean hasSelector = player.getItemInHand(HandTypes.MAIN_HAND)
                    .map(item -> item.getType().equals(itemsNode.getSelector()))
                    .orElse(false);

            if (hasSelector) {
                // if the chunkloader is not on this server or the player can edit chunkloader then we should show
                // the UI
                if (!hasChunkLoader || (player.getUniqueId().equals(chunkLoader.getOwner()) || player
                        .hasPermission(Permission.ABILITY_EDIT_OTHERS))) {
                    // if the chunkloader is not present lets make one!
                    if (chunkLoader == null) {
                        UUID uid = player.getUniqueId();
                        Vector3i chunkVector = clickedBlock.getLocation().get().getChunkPosition();
                        String worldName = clickedBlock.getLocation().get().getExtent().getName();
                        boolean alwaysOn = clickedBlock.getState().getType().equals(BlockTypes.DIAMOND_BLOCK);
                        chunkLoader = new CChunkLoader(
                                chunkVector.getX(),
                                chunkVector.getY(),
                                worldName, (byte) -1,
                                uid,
                                clickedBlock.getLocation().get(),
                                null,
                                alwaysOn
                        );
                    }
                    ChunkLoaderInventory.ShowUI(player, chunkLoader);
                } else {
                    player.sendMessage(Text.of(TextColors.RED, "You can't edit others' chunk loaders."));
                }
            } else {
                if (hasChunkLoader) {
                    player.sendMessage(chunkLoader.info());
                } else {
                    String selectorName = Config.getInstance().getItems().getSelectorName();
                    player.sendMessage(Text.of(TextColors.GOLD,
                            "Iron and Diamond blocks can be converted into chunk loaders. Right click it with a ", selectorName, "."));
                }
            }
        }
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        event.getTransactions().forEach(blockSnapshotTransaction -> {
            BlockSnapshot block = blockSnapshotTransaction.getOriginal();

            //If the block is not diamond or Iron or if the location of the block is not present we should just return early.
            if (
                    (!block.getState().getType().equals(BlockTypes.DIAMOND_BLOCK) && !block.getState().getType().equals(BlockTypes.IRON_BLOCK))
                            || !block.getLocation().isPresent()
            ) {
                return;
            }

            CChunkLoader chunkLoader = DataStoreManager.getDataStore().getChunkLoaderAt(block.getLocation().get());

            //If there is no chunkloader at this location we just return early.
            if (chunkLoader == null) {
                return;
            }

            ChunkLoaderHelper.removeChunkLoader(chunkLoader, event.getCause().last(Player.class).orElse(null));
        });


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
        } catch (RuntimeException | Exception e) {
            Messenger.logException(e);
        }
    }

    @Listener
    public void onReload(GameReloadEvent event) {

        try {
            BetterChunkLoader.instance().setupPlugin();
        } catch (Exception | MySQLConnectionException e) {
            Messenger.logException(e);
        }
    }
}
