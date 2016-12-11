package net.kaikk.mc.bcl.commands;

import net.kaikk.mc.bcl.CChunkLoader;
import net.kaikk.mc.bcl.datastore.DataStoreManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ROB on 08/12/2016.
 */
public class CmdList {

//    private boolean list(CommandSender sender, String label, String[] args) {
//        if (args.length<2) {
//            sender.sendMessage(ChatColor.GOLD + "Usage: /bcl list (own|PlayerName|all) [page]");
//            return false;
//        }
//
//        int page=1;
//        if (args.length==3) {
//            try {
//                page=Integer.valueOf(args[2]);
//                if (page<1) {
//                    throw new NumberFormatException();
//                }
//            } catch (NumberFormatException e) {
//                sender.sendMessage(ChatColor.RED + "Invalid page");
//                return false;
//            }
//        }
//
//        if (args[1].equalsIgnoreCase("all")) {
//            if (!sender.hasPermission("betterchunkloader.list.others")) {
//                sender.sendMessage(ChatColor.RED + "You don't have permission to run this command.");
//                return false;
//            }
//
//            List<CChunkLoader> clList = DataStoreManager.getDataStore().getChunkLoaders();
//
//            printChunkLoadersList(clList, sender, page);
//        } else if (args[1].equalsIgnoreCase("world")) {
//            if (!sender.hasPermission("betterchunkloader.list.others")) {
//                sender.sendMessage(ChatColor.RED + "You don't have permission to run this command.");
//                return false;
//            }
//
//            List<CChunkLoader> clList = new ArrayList<CChunkLoader>();
//            for (CChunkLoader cl : DataStoreManager.getDataStore().getChunkLoaders()) {
//                if (cl.isAlwaysOn()) {
//                    clList.add(cl);
//                }
//            }
//
//            printChunkLoadersList(clList, sender, page);
//        } else {
//            String playerName = args[1];
//            if (playerName.equalsIgnoreCase("own")) {
//                playerName=sender.getName();
//            }
//
//            if (sender.getName().equalsIgnoreCase(playerName)) {
//                if (!sender.hasPermission("betterchunkloader.list.own")) {
//                    sender.sendMessage(ChatColor.RED + "You don't have permission to run this command.");
//                    return false;
//                }
//            } else {
//                if (!sender.hasPermission("betterchunkloader.list.others")) {
//                    sender.sendMessage(ChatColor.RED + "You don't have permission to run this command.");
//                    return false;
//                }
//            }
//
//            OfflinePlayer player = instance.getServer().getOfflinePlayer(playerName);
//            if (player==null || !player.hasPlayedBefore()) {
//                sender.sendMessage(ChatColor.RED + "Player not found.");
//                return false;
//            }
//            List<CChunkLoader> clList = DataStoreManager.getDataStore().getChunkLoaders(player.getUniqueId());
//            if (clList==null || clList.size()==0) {
//                sender.sendMessage(ChatColor.RED + "This player doesn't have any chunk loader.");
//                return false;
//            }
//
//            int clSize=clList.size();
//            int pages=(int) Math.ceil(clSize/5.00);
//
//            if (page>pages) {
//                sender.sendMessage(ChatColor.RED + "Invalid page");
//                return false;
//            }
//
//            sender.sendMessage(ChatColor.GOLD + "== "+player.getName()+"'s loaded chunks ("+page+"/"+pages+") ==");
//            sender.sendMessage(ChatColor.GRAY + "(World Loader? - Size - Position)");
//
//            for(int i=(page-1)*5; i<page*5 && i<clSize; i++) {
//                CChunkLoader chunkLoader=clList.get(i);
//                sender.sendMessage(chunkLoader.toString());
//            }
//
//        }
//        return true;
//    }
//
//    static private boolean printChunkLoadersList(List<CChunkLoader> clList, CommandSender sender, int page) {
//
//        int clSize=clList.size();
//        if (clSize==0) {
//            sender.sendMessage(ChatColor.RED + "There isn't loaded chunks yet!");
//            return false;
//        }
//
//        int pages=(int) Math.ceil(clSize/5.00);
//
//        if (page>pages) {
//            sender.sendMessage(ChatColor.RED + "Invalid page");
//            return false;
//        }
//
//        sender.sendMessage(ChatColor.GOLD + "== Loaded Chunks List ("+page+"/"+pages+") ==");
//        sender.sendMessage(ChatColor.GRAY + "(Owner - World Loader? - Size - Position)");
//
//        for(int i=(page-1)*5; i<page*5 && i<clSize; i++) {
//            CChunkLoader chunkLoader=clList.get(i);
//            sender.sendMessage(chunkLoader.getOwnerName()+" - "+chunkLoader.toString());
//        }
//        return true;
//    }
}
