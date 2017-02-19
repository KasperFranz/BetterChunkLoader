package net.kaikk.mc.bcl;

import java.util.*;
import java.util.concurrent.TimeUnit;

import net.kaikk.mc.bcl.config.Config;
import net.kaikk.mc.bcl.datastore.DataStoreManager;
import net.kaikk.mc.bcl.forgelib.BCLForgeLib;
import net.kaikk.mc.bcl.utils.InventoryCloseAfterADelayTask;
import net.kaikk.mc.bcl.utils.Messenger;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class Events {

	@Listener
	public void onPlayerInteract(InteractBlockEvent.Secondary event) {
		if(!event.getCause().containsType(Player.class)){
			return;
		}
	    Player player = event.getCause().last(Player.class).get();
		BlockSnapshot clickedBlock = event.getTargetBlock();
		
		if (clickedBlock==null || player==null) {
			return;
		}
		
		if (clickedBlock.getState().getType().equals(BlockTypes.DIAMOND_BLOCK) || clickedBlock.getState().getType().equals(BlockTypes.IRON_BLOCK)) {
				//todo: This should really be with the world too
				CChunkLoader chunkLoader = DataStoreManager.getDataStore().getChunkLoaderAt(clickedBlock.getLocation().get());
				String serverName = Config.getConfig().get().getNode("ServerName").getString();
				boolean ChunkLoaderOnThisServer = chunkLoader!=null && chunkLoader.getServerName().equalsIgnoreCase(serverName);

				if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent() && player.getItemInHand(HandTypes.MAIN_HAND).get().getItem().getType().equals(ItemTypes.BLAZE_ROD)) {
					boolean adminLoader = chunkLoader.isAdminChunkLoader() && player.hasPermission("betterchunkloader.adminloader");
					// if the chunkloader is not on this server or the player can edit chunkloader or if it is an admin chunkloader then we should show the UI
					if (!ChunkLoaderOnThisServer || (player.getUniqueId().equals(chunkLoader.getOwner()) || player.hasPermission("betterchunkloader.edit.others") || adminLoader)) {
						// if the chunkloader is not present lets make one!
						if(chunkLoader == null) {
							UUID uid = player.getUniqueId();
							int x = (int) Math.floor(clickedBlock.getLocation().get().getBlockX() / 16.00);
							int y = (int) Math.floor(clickedBlock.getLocation().get().getBlockZ() / 16.00);
							String worldName = clickedBlock.getLocation().get().getExtent().getName();
							boolean alwaysOn = clickedBlock.getState().getType().equals(BlockTypes.DIAMOND_BLOCK);
							chunkLoader = new CChunkLoader(x, y, worldName, (byte) -1, uid, clickedBlock.getLocation().get(), null, alwaysOn, serverName);
						}

						chunkLoader.showUI(player);
					} else {
						player.sendMessage(Text.of(TextColors.RED, "You can't edit others' chunk loaders."));
					}
				} else {
					if (ChunkLoaderOnThisServer) {
						player.sendMessage(chunkLoader.info());
					} else {
						player.sendMessage(Text.of(TextColors.GOLD, "Iron and Diamond blocks can be converted into chunk loaders. Right click it with a blaze rod."));
					}
				}
		}
	}

	@Listener
	public void onBlockBreak(ChangeBlockEvent.Break event) {
		BlockSnapshot block = event.getTransactions().get(0).getOriginal();
		if (block==null || (!block.getState().getType().equals(BlockTypes.DIAMOND_BLOCK) && !block.getState().getType().equals(BlockTypes.IRON_BLOCK))) {
			return;
		}

		//todo: This should really be with the world too
		CChunkLoader chunkLoader = DataStoreManager.getDataStore().getChunkLoaderAt(block.getLocation().get());
		if (chunkLoader==null) {
			return;
		} else if (!chunkLoader.getServerName().equalsIgnoreCase(Config.getConfig().get().getNode("ServerName").getString())) {
		    return;
        }
		
		DataStoreManager.getDataStore().removeChunkLoader(chunkLoader);
		
		Optional<Player> player = event.getCause().last(Player.class);
		String breaker = player.isPresent() ?player.get().getName() : "unknown";
		Player owner = chunkLoader.getPlayer();
		if (owner!=null && !owner.getName().equals(breaker)) {
			owner.sendMessage(Text.of(TextColors.RED, "Your chunk loader at "+chunkLoader.getLoc().toString()+" has been removed by "+breaker+"."));
		}
		
		BetterChunkLoader.instance().getLogger().info(breaker+" broke "+chunkLoader.getOwnerName()+"'s chunk loader at "+chunkLoader.getLocationString());
	}
	
	@Listener
	public void onPlayerLogin(ClientConnectionEvent.Join event) {
		DataStoreManager.getDataStore().refreshPlayer(event.getTargetEntity().getUniqueId());

		//todo: This should really be with the world too
		List<CChunkLoader> clList = DataStoreManager.getDataStore().getChunkLoaders(event.getTargetEntity().getUniqueId());

		for (CChunkLoader chunkLoader : clList) {
			if(chunkLoader.getServerName().equalsIgnoreCase(Config.getConfig().get().getNode("ServerName").getString())) {
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
			if(chunkLoader.getServerName().equalsIgnoreCase(Config.getConfig().get().getNode("ServerName").getString())) {
				if (!chunkLoader.isAlwaysOn()) {
					BCLForgeLib.instance().removeChunkLoader(chunkLoader);
				}
			}
		}
	}

	//This is only here to make sure that you ca't do weird shit with the inventory.
	//Todo: I saw on the forum somehow to merge the 2 events, But i forgot where i found it, if this is found again it might be usefull to merge these 2
	@Listener
	public void onInventoryInteract(ClickInventoryEvent event) {
		Optional<InventoryProperty<String, ?>> optionalCChunkLoaderInvProp = event.getTargetInventory().getArchetype().getProperty("cchunkloaderinvprop");
		if(!optionalCChunkLoaderInvProp.isPresent()) {
			return;
		}
		if(!(event instanceof ClickInventoryEvent.Primary)){
			event.setCancelled(true);
		}
	}

	//This is only here to make sure that you ca't do weird shit with the inventory.
	//Todo: I saw on the forum somehow to merge the 2 events, But i forgot where i found it, if this is found again it might be usefull to merge these 2
	@Listener
	public void onInventoryInteractTest(ChangeInventoryEvent event) {
		Optional<InventoryProperty<String, ?>> optionalCChunkLoaderInvProp = event.getTargetInventory().getArchetype().getProperty("cchunkloaderinvprop");
		if(!optionalCChunkLoaderInvProp.isPresent()) {
			return;
		}
		if(!(event instanceof ClickInventoryEvent.Primary)){
			event.setCancelled(true);
		}
	}

    @Listener
    public void onInventoryClick(ClickInventoryEvent.Primary event) {

		Optional<InventoryProperty<String, ?>> optionalCChunkLoaderInvProp = event.getTargetInventory().getArchetype().getProperty("cchunkloaderinvprop");
		if(!optionalCChunkLoaderInvProp.isPresent()) {
			return;
		}

		event.setCancelled(true);

		CChunkLoaderInvProp cChunkLoaderInvProp = (CChunkLoaderInvProp)optionalCChunkLoaderInvProp.get();
		CChunkLoader chunkLoader = cChunkLoaderInvProp.getValue();
    	if (event.getCause().last(Player.class).isPresent()) {
    		Player player = event.getCause().last(Player.class).get();

    		if (chunkLoader==null) {
    			return;
    		}
    		
    		if (chunkLoader.isAdminChunkLoader() && !player.hasPermission("betterchunkloader.adminloader")) {
				Messenger.sendNoPermission(player);
				return;
    		}

			if (!player.getUniqueId().equals(chunkLoader.getOwner()) && !player.hasPermission("betterchunkloader.edit.others")) {
				player.sendMessage(Text.of(TextColors.RED, "You can't edit others' chunk loaders."));
				return;
			}

    		if(!event.getTransactions().get(0).getOriginal().get(Keys.DISPLAY_NAME).isPresent()) {
    			return;
            }
			String firstChar = String.valueOf(event.getTransactions().get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain().charAt(5));
    		Integer pos;

    		try {
                pos = Integer.parseInt(firstChar);
            } catch(NumberFormatException e){
    		    pos = 0;
            }


			//todo these 2 might be more useful combined as most of the logic is the same!.
            // -1 == create new chunkloader (as the old chunkLoaders range was 0)
			if(chunkLoader.getRange() == -1){
				pos = chunkLoader.radiusFromSide(pos);
				if (!chunkLoader.isAdminChunkLoader() && !player.hasPermission("betterchunkloader.unlimitedchunks")) {

					int needed = (1+(pos*2))*(1+(pos*2));
					int available;
					if (chunkLoader.isAlwaysOn()) {
						available=DataStoreManager.getDataStore().getAlwaysOnFreeChunksAmount(chunkLoader.getOwner());
					} else {
						available=DataStoreManager.getDataStore().getOnlineOnlyFreeChunksAmount(chunkLoader.getOwner());
					}

					if (needed>available) {
						player.sendMessage(Text.of(TextColors.RED, "You don't have enough free chunks! Needed: "+needed+". Available: "+available+"."));
						closeInventory(player);
						return;
					}
				}

				chunkLoader.setRange(Byte.valueOf(""+pos));
				chunkLoader.setCreationDate(new Date());
				String type = chunkLoader.isAdminChunkLoader()?"adminloader ":"chunkloader";
				BetterChunkLoader.instance().getLogger().info(player.getName()+" made a new "+type +" at "+chunkLoader.getLocationString()+" with range "+pos);
				DataStoreManager.getDataStore().addChunkLoader(chunkLoader);
				closeInventory(player);
				player.sendMessage(Text.of(TextColors.GOLD, "Chunk Loader created."));
				return;
			}
    		if(chunkLoader.getRange()!=-1) {
    			if (pos==0) {
        			// remove the chunk loader
        			DataStoreManager.getDataStore().removeChunkLoader(chunkLoader);
        			closeInventory(player);
        		} else {
					pos = chunkLoader.radiusFromSide(pos);
        			// if higher range, check if the player has enough free chunks
        			if (!chunkLoader.isAdminChunkLoader() && !player.hasPermission("betterchunkloader.unlimitedchunks")) {

	        			if (pos>chunkLoader.getRange()) {
							int needed = ((1+(pos*2))*(1+(pos*2)))-chunkLoader.size();
	        				int available;
	        				if (chunkLoader.isAlwaysOn()) {
	        					available=DataStoreManager.getDataStore().getAlwaysOnFreeChunksAmount(chunkLoader.getOwner());
	        				} else {
	        					available=DataStoreManager.getDataStore().getOnlineOnlyFreeChunksAmount(chunkLoader.getOwner());
	        				}
	        				
	        				if (needed>available) {
								player.sendMessage(Text.of(TextColors.RED, "You don't have enough free chunks! Needed: "+needed+". Available: "+available+"."));
	        					closeInventory(player);
	        					return;
	        				}
	        			}
        			}
        			
    				BetterChunkLoader.instance().getLogger().info(player.getName()+" edited "+chunkLoader.getOwnerName()+"'s chunk loader at "+chunkLoader.getLocationString()+" range from "+chunkLoader.getRange()+" to "+pos);
    				DataStoreManager.getDataStore().changeChunkLoaderRange(chunkLoader, Byte.valueOf(""+pos));
    				player.sendMessage(Text.of(TextColors.GOLD,"Chunk Loader updated."));
    				closeInventory(player);
        		}
    		}
    	}
    }
    
    @Listener
    public void onWorldLoad(LoadWorldEvent event) {
		//todo: shouldn't this also include the world?
		for (CChunkLoader cl : DataStoreManager.getDataStore().getChunkLoaders(event.getTargetWorld().getName())) {
			if (cl.isLoadable()) {
				BCLForgeLib.instance().addChunkLoader(cl);
			}
		}
    }
    
    private static void closeInventory(final Player p) {
		Task.builder().execute(new InventoryCloseAfterADelayTask(p))
				.delay(100, TimeUnit.MILLISECONDS)
				.name("Closing players inventory.").submit(BetterChunkLoader.instance());
    }

}

