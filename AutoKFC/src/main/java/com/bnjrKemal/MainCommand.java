package com.bnjrKemal;

import com.bnjrKemal.inventory.MainInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //for console
        if (!(sender instanceof Player)) {
            Configurationx.reload(sender);
            return false;
        }
        //reload command
        if(sender.isOp() && args.length >= 1 && (args[0].equalsIgnoreCase("reload"))){
            if(args[0].equalsIgnoreCase("reload")){
                Configurationx.reload(sender);
                return false;
            }
        }
        //open or buy for autoKFC
        if(args.length == 0 || !sender.isOp()){

            /*
            PlayerData playerData = new PlayerData(((Player) sender).getUniqueId());
            if(playerData.getDatabaseFile() == null){
                //satın alımı
                return false;
            }
            */

            ((Player) sender).openInventory(new MainInventory().getInventory());
            return true;
        }
        //previouspage arguments
        if(args.length == 2 && args[1].equalsIgnoreCase("previouspage")){
            ItemStack hold = ((Player) sender).getItemInHand();
            if(hold.getType().equals(Material.AIR)){
                sender.sendMessage(ChatColor.RED + "[AutoKFC] Your hold cannot the air. You need to held your item in your hand.");
                return false;
            }
            Configurationx.getGuiYAML().set("previouspage", hold);
            try {Configurationx.getGuiYAML().save(Configurationx.getGuiFile());} catch (IOException e) {throw new RuntimeException(e);}
            return false;
        }
        //nextpage arguments
        if(args.length == 2 && args[1].equalsIgnoreCase("nextpage")){
            ItemStack hold = ((Player) sender).getItemInHand();
            if(hold.getType().equals(Material.AIR)){
                sender.sendMessage(ChatColor.RED + "[AutoKFC] Your hold cannot the air. You need to held your item in your hand.");
                return false;
            }
            Configurationx.getGuiYAML().set("nextpage", hold);
            try {Configurationx.getGuiYAML().save(Configurationx.getGuiFile());} catch (IOException e) {throw new RuntimeException(e);}
            return false;
        }
        //otherslot arguments
        if(args.length == 2 && args[1].equalsIgnoreCase("otherslot")){
            ItemStack hold = ((Player) sender).getItemInHand();
            if(hold.getType().equals(Material.AIR)){
                sender.sendMessage(ChatColor.RED + "[AutoKFC] Your hold cannot the air. You need to held your item in your hand.");
                return false;
            }
            Configurationx.getGuiYAML().set("other-slots", hold);
            try {Configurationx.getGuiYAML().save(Configurationx.getGuiFile());} catch (IOException e) {throw new RuntimeException(e);}
            return false;
        }
        //+3 arguments
        if(args.length >= 3){
            //row arguments
            if(args[1].equalsIgnoreCase("row")){
                try{
                    int number = Integer.parseInt(args[2]);
                    Configurationx.getGuiYAML().set("row", number);
                }catch(NumberFormatException e){sender.sendMessage(ChatColor.RED + "[AutoKFC] 3th argument is not number. It must be number.");}
                try {Configurationx.getGuiYAML().save(Configurationx.getGuiFile());} catch (IOException e) {throw new RuntimeException(e);}
                return false;
            }
            //slot arguments
            if(args[1].equalsIgnoreCase("slot")){
                try{
                    int number = Integer.parseInt(args[2]);
                    if(number>54 || number <0){
                        sender.sendMessage(ChatColor.RED + "[AutoKFC] 3th argument is can be between 0 and 53. Please get correct number.");
                        return false;
                    }
                    if(args.length >= 4 && !(args[3].equalsIgnoreCase("autoKill") || args[3].equalsIgnoreCase("autoHarvest") || args[3].equalsIgnoreCase("autoCollect"))){
                        sender.sendMessage(ChatColor.RED + "[AutoKFC] 4th argument is that can be autoKill or autoHarvest or autoCollect. Please select one.");
                        return false;
                    }
                    ItemStack hold = ((Player) sender).getItemInHand();
                    if(hold.getType().equals(Material.AIR)){
                        sender.sendMessage(ChatColor.RED + "[AutoKFC] Your hold cannot the air. You need to held your item in your hand.");
                        return false;
                    }
                    ItemMeta itemMeta = hold.getItemMeta();
                    List<String> lore = hold.getItemMeta().getLore();
                    if(lore == null) lore = new ArrayList<>();
                    lore.add(ChatColor.BLACK + args[3].toUpperCase());
                    itemMeta.setLore(lore);
                    hold.setItemMeta(itemMeta);
                    Configurationx.getGuiYAML().set("slots." + number + ".item", hold);
                }catch(NumberFormatException e){sender.sendMessage(ChatColor.RED + "[AutoKFC] 3th argument is not number. It must be number.");}
                try {Configurationx.getGuiYAML().save(Configurationx.getGuiFile());} catch (IOException e) {throw new RuntimeException(e);}
                return false;
            }
            //title arguments
            if(args[1].equalsIgnoreCase("title")){
                StringBuilder concatenated = new StringBuilder();
                for (int i = 2; i < args.length; i++)
                    concatenated.append(args[i]).append(" ");
                String title = concatenated.toString();
                Configurationx.getGuiYAML().set("title", ChatColor.translateAlternateColorCodes('&', title));
                try {Configurationx.getGuiYAML().save(Configurationx.getGuiFile());} catch (IOException e) {throw new RuntimeException(e);}
                return false;
            }
        }
        //help lines
        sender.sendMessage(ChatColor.BLUE + " [AutoKFC] - Help me" + ChatColor.DARK_RED + " - by bnjrKemal");
        sender.sendMessage(ChatColor.GRAY +
                "          /autoKFC - Open the Main inventory of AutoKFC.\n" +
                "          /autoKFC reload - Reload the all files by who is op or console.\n" +
                "          /autoKFC help - About of all commands.\n" +
                "\n" +
                "          /autoKFC set row <number>\n" +
                "          /autoKFC set title <text...>\n" +
                "          /autoKFC set slot <number> auto(kill|harvest|collect)//(by item's hold)\n" +
                "          /autoKFC set previouspage //(by item's hold for previous page icon)" +
                "          /autoKFC set nextpage //(by item's hold for next page icon)" +
                "          /autoKFC set otherslot //(by item's hold)");

        return true;
    }
}
