package net.kaikk.mc.bcl;

import java.util.*;
import net.kaikk.mc.bcl.config.Config;
import net.kaikk.mc.bcl.datastore.DataStoreManager;
import net.kaikk.mc.bcl.forgelib.BCLForgeLib;
import net.kaikk.mc.bcl.utils.Messenger;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.InventoryProperty;
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
				CChunkLoader chunkLoader = DataStoreManager.getDataStore().getChunkLoaderAt(clickedBlock.getLocation().get());
				if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent() && player.getItemInHand(HandTypes.MAIN_HAND).get().getItem().getType().equals(ItemTypes.BLAZE_ROD)) {
					if (chunkLoader!=null && chunkLoader.getServerName().equalsIgnoreCase(Config.getConfig().get().getNode("ServerName").getString())) {
						if (player.getUniqueId().equals(chunkLoader.getOwner()) || player.hasPermission("betterchunkloader.edit") || (chunkLoader.isAdminChunkLoader() && player.hasPermission("betterchunkloader.adminloader"))) {
							chunkLoader.showUI(player);
						} else {
							player.sendMessage(Text.of(TextColors.RED, "You can't edit others' chunk loaders."));
						}
					} else {
							UUID uid=player.getUniqueId();

							chunkLoader = new CChunkLoader((int) (Math.floor(clickedBlock.getLocation().get().getBlockX()/16.00)), (int) (Math.floor(clickedBlock.getLocation().get().getBlockZ()/16.00)), clickedBlock.getLocation().get().getExtent().getName(), (byte) -1, uid, clickedBlock.getLocation().get(), null, clickedBlock.getState().getType().equals(BlockTypes.DIAMOND_BLOCK), Config.getConfig().get().getNode("ServerName").getString());
							chunkLoader.showUI(player);
					}
				} else {
					if (chunkLoader!=null && chunkLoader.getServerName().equalsIgnoreCase(Config.getConfig().get().getNode("ServerName").getString())) {
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

		CChunkLoader chunkLoader = DataStoreManager.getDataStore().getChunkLoaderAt(block.getLocation().get());
		if (chunkLoader==null) {
			return;
		} else if (!chunkLoader.getServerName().equalsIgnoreCase(Config.getConfig().get().getNode("ServerName").getString())) {
		    return;
        }
		
		DataStoreManager.getDataStore().removeChunkLoader(chunkLoader);
		
		Player player = event.getCause().last(Player.class).get();
		player.sendMessage(Text.of(TextColors.RED, "Chunk loader removed."));
		
		Player owner = chunkLoader.getPlayer();
		if (owner!=null && player!=owner) {
			owner.sendMessage(Text.of(TextColors.RED, "Your chunk loader at "+chunkLoader.getLoc().toString()+" has been removed by "+player.getName()+"."));
		}
		
		BetterChunkLoader.instance().getLogger().info(player.getName()+" broke "+chunkLoader.getOwnerName()+"'s chunk loader at "+chunkLoader.getLocationString());
	}
	
	@Listener
	public void onPlayerLogin(ClientConnectionEvent.Join event) {
//		if (event.getResult()!=Result.ALLOWED) {
//			return;
//		}

		DataStoreManager.getDataStore().refreshPlayer(event.getTargetEntity().getUniqueId());

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
		BetterChunkLoader.instance().getLogger().info("Quit event!");
		List<CChunkLoader> clList = DataStoreManager.getDataStore().getChunkLoaders(event.getTargetEntity().getUniqueId());

		for (CChunkLoader chunkLoader : clList) {
			if(chunkLoader.getServerName().equalsIgnoreCase(Config.getConfig().get().getNode("ServerName").getString())) {
				if (!chunkLoader.isAlwaysOn()) {
					BCLForgeLib.instance().removeChunkLoader(chunkLoader);
				}
			}
		}
	}

    @Listener
    public void onInventoryClick(ClickInventoryEvent.Primary event) {

		Optional<InventoryProperty<String, ?>> optionalCChunkLoaderInvProp = event.getTargetInventory().getArchetype().getProperty("cchunkloaderinvprop");
		if(!optionalCChunkLoaderInvProp.isPresent()) {
			return;
		}

		CChunkLoaderInvProp cChunkLoaderInvProp = (CChunkLoaderInvProp)optionalCChunkLoaderInvProp.get();
		CChunkLoader chunkLoader = cChunkLoaderInvProp.getValue();
    	if (event.getCause().last(Player.class).isPresent()) {
    		Player player = event.getCause().last(Player.class).get();

    		event.setCancelled(true);

    		if (chunkLoader==null) {
    			return;
    		}
    		
    		if (chunkLoader.isAdminChunkLoader()) {
    			if (!player.hasPermission("betterchunkloader.adminloader")) {
					Messenger.sendNoPermission(player);
					return;
    			}
    		} else {
	    		if (!player.getUniqueId().equals(chunkLoader.getOwner()) && !player.hasPermission("betterchunkloader.edit")) {
	    			player.sendMessage(Text.of(TextColors.RED, "You can't edit others' chunk loaders."));
	    			return;
	    		}
    		}
    		String firstChar = null;
    		if(event.getTransactions().get(0).getOriginal().get(Keys.DISPLAY_NAME).isPresent()) {
    		    firstChar = String.valueOf(event.getTransactions().get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain().charAt(5));
            } else {
    		    return;
            }
            BetterChunkLoader.instance().getLogger().info("FISTCHAR:" +firstChar);
    		Integer pos;
    		try {
                pos = Integer.parseInt(firstChar);
            } catch(NumberFormatException e){
    		    pos = 0;
            }
            BetterChunkLoader.instance().getLogger().info("POS IS:" +pos);

    		if(chunkLoader.getRange()!=-1) {
    			if (pos==0) {
        			// remove the chunk loader
        			DataStoreManager.getDataStore().removeChunkLoader(chunkLoader);
        			closeInventory(player);
        		} else {
        			
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
	        					player.sendMessage(Text.of(TextColors.RED, "Not enough free chunks! Needed: "+needed+". Available: "+available+"."));
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
    		} else {
    			
    			if (!chunkLoader.isAdminChunkLoader() && !player.hasPermission("betterchunkloader.unlimitedchunks")) {
	    			int needed = (1+(pos*2))*(1+(pos*2));
					int available;
					if (chunkLoader.isAlwaysOn()) {
						available=DataStoreManager.getDataStore().getAlwaysOnFreeChunksAmount(chunkLoader.getOwner());
					} else {
						available=DataStoreManager.getDataStore().getOnlineOnlyFreeChunksAmount(chunkLoader.getOwner());
					}
					
					if (needed>available) {
						player.sendMessage(Text.of(TextColors.RED, "Not enough free chunks! Needed: "+needed+". Available: "+available+"."));
						closeInventory(player);
						return;
					}
    			}
    			
    			chunkLoader.setRange(Byte.valueOf(""+pos));
    			chunkLoader.setCreationDate(new Date());
    			BetterChunkLoader.instance().getLogger().info(player.getName()+" made a new "+(chunkLoader.isAdminChunkLoader()?"admin ":"")+"chunk loader at "+chunkLoader.getLocationString()+" with range "+pos);
    			DataStoreManager.getDataStore().addChunkLoader(chunkLoader);
    			closeInventory(player);
    			player.sendMessage(Text.of(TextColors.GOLD, "Chunk Loader created."));
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
    
    private static void closeInventory(final Player p) {
		p.closeInventory(Cause.of(NamedCause.simulated(p)));
    }
}
