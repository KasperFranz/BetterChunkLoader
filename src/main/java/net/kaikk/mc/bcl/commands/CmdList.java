package net.kaikk.mc.bcl.commands;

import net.kaikk.mc.bcl.CChunkLoader;
import net.kaikk.mc.bcl.datastore.DataStoreManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ROB on 08/12/2016.
 */
public class CmdList implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {

        int page = 1;
        if(commandContext.getOne("page").isPresent()){
            try {
                page = (Integer)commandContext.getOne("page").get();
            } catch (Exception e) {
                commandSource.sendMessage(Text.builder("Invalid page").color(TextColors.RED).build());
                return null;
            }
        }

        if (args[1].equalsIgnoreCase("all")) {
            if (!sender.hasPermission("betterchunkloader.list.others")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to run this command.");
                return false;
            }

            List<CChunkLoader> clList = DataStoreManager.getDataStore().getChunkLoaders();

            printChunkLoadersList(clList, sender, page);
        } else if (args[1].equalsIgnoreCase("world")) {
            if (!sender.hasPermission("betterchunkloader.list.others")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to run this command.");
                return false;
            }

            List<CChunkLoader> clList = new ArrayList<CChunkLoader>();
            for (CChunkLoader cl : DataStoreManager.getDataStore().getChunkLoaders()) {
                if (cl.isAlwaysOn()) {
                    clList.add(cl);
                }
            }

            printChunkLoadersList(clList, sender, page);
        } else {
            String playerName = args[1];
            if (playerName.equalsIgnoreCase("own")) {
                playerName = sender.getName();
            }

            if (sender.getName().equalsIgnoreCase(playerName)) {
                if (!sender.hasPermission("betterchunkloader.list.own")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to run this command.");
                    return false;
                }
            } else {
                if (!sender.hasPermission("betterchunkloader.list.others")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to run this command.");
                    return false;
                }
            }

            OfflinePlayer player = instance.getServer().getOfflinePlayer(playerName);
            if (player == null || !player.hasPlayedBefore()) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return false;
            }
            List<CChunkLoader> clList = DataStoreManager.getDataStore().getChunkLoaders(player.getUniqueId());
            if (clList == null || clList.size() == 0) {
                sender.sendMessage(ChatColor.RED + "This player doesn't have any chunk loader.");
                return false;
            }

            int clSize = clList.size();
            int pages = (int) Math.ceil(clSize / 5.00);

            if (page > pages) {
                sender.sendMessage(ChatColor.RED + "Invalid page");
                return false;
            }

            sender.sendMessage(ChatColor.GOLD + "== " + player.getName() + "'s loaded chunks (" + page + "/" + pages + ") ==");
            sender.sendMessage(ChatColor.GRAY + "(World Loader? - Size - Position)");

            for (int i = (page - 1) * 5; i < page * 5 && i < clSize; i++) {
                CChunkLoader chunkLoader = clList.get(i);
                sender.sendMessage(chunkLoader.toString());
            }

        }
        return true;
    }
    static private boolean printChunkLoadersList(List<CChunkLoader> clList, CommandSender sender, int page) {

        int clSize=clList.size();
        if (clSize==0) {
            sender.sendMessage(ChatColor.RED + "There isn't loaded chunks yet!");
            return false;
        }

        int pages=(int) Math.ceil(clSize/5.00);

        if (page>pages) {
            sender.sendMessage(ChatColor.RED + "Invalid page");
            return false;
        }

        sender.sendMessage(ChatColor.GOLD + "== Loaded Chunks List ("+page+"/"+pages+") ==");
        sender.sendMessage(ChatColor.GRAY + "(Owner - World Loader? - Size - Position)");

        for(int i=(page-1)*5; i<page*5 && i<clSize; i++) {
            CChunkLoader chunkLoader=clList.get(i);
            sender.sendMessage(chunkLoader.getOwnerName()+" - "+chunkLoader.toString());
        }
        return true;
    }

}
