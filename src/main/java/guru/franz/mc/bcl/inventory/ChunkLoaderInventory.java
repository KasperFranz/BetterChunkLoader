package guru.franz.mc.bcl.inventory;

import guru.franz.mc.bcl.BetterChunkLoader;
import guru.franz.mc.bcl.datastore.DataStoreManager;
import guru.franz.mc.bcl.model.CChunkLoader;
import guru.franz.mc.bcl.utils.ChunkLoaderHelper;
import guru.franz.mc.bcl.utils.Messages;
import guru.franz.mc.bcl.utils.Permission;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ChunkLoaderInventory {

    public static void ShowUI(Player player, CChunkLoader chunkLoader) {
        String title = (chunkLoader.getRange() != -1 ? "BCL:" + chunkLoader.getOwnerName() + "@" + chunkLoader.getLocationString() :
                "New Chunk Loader");
        if (title.length() > 32) {
            title = title.substring(0, 32);
        }
        InventoryArchetype inventoryArchetype = InventoryArchetype.builder()
                .from(InventoryArchetypes.MENU_ROW)
                .property(ChunkLoaderInvProp.of(chunkLoader)).build("archid", "archname");

        Inventory inventory = Inventory.builder()
                .of(inventoryArchetype)
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(title)))
                .listener(ClickInventoryEvent.class, createClickEventConsumer(chunkLoader))
                .build(BetterChunkLoader.instance());
        if (chunkLoader.getRange() != -1) {
            addInventoryOption(inventory, 0, ItemTypes.REDSTONE_TORCH, "Remove");
        }

        int pos = 2;
        for (byte i = 0; i < 5; ) {
            addInventoryOption(inventory, pos, ItemTypes.MAP, "Size " + ChunkLoaderHelper.getRadiusFromRange(i) + (chunkLoader.getRange() == i ? " [selected]" : ""));
            pos++;
            i++;
        }

        player.openInventory(inventory);

    }

    private static void addInventoryOption(Inventory inventory, int position, ItemType icon, String name) {
        Iterable<Slot> slotIterable = inventory.slots();
        ItemStack itemStack = ItemStack.builder().itemType(icon).quantity(1).build();
        itemStack.offer(Keys.DISPLAY_NAME, Text.of(name));
        int iteration = 0;
        for (Slot slot : slotIterable) {
            if (iteration == position) {
                slot.set(itemStack);
            }
            iteration++;
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
                    if (!player.hasPermission(Permission.ABILITY_UNLIMITED)) {

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
                    String type = "chunk loader";


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
                        if (!player.hasPermission(Permission.ABILITY_UNLIMITED) &&  pos > chunkLoader.getRange()) {
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

}
