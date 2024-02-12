package com.bnjrKemal;

import com.bnjrKemal.inventory.MainInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoreSystem {

    private UUID uuid;
    private String holder;

    public LoreSystem(UUID uuid){
        this.uuid = uuid;
        this.holder = MainInventory.getHolder(Bukkit.getPlayer(uuid).getOpenInventory().getTopInventory().getHolder());
    }

    public List<ItemStack> makeLore(List<ItemStack> keysForPage) {
        List<ItemStack> resultItemStack = new ArrayList<>();
        PlayerData playerData = new PlayerData(uuid);
        for(ItemStack itemStack : keysForPage)
            resultItemStack.add(changeLoreOneItemStack(playerData, itemStack));
        return resultItemStack;
    }

    public ItemStack changeLoreOneItemStack(PlayerData playerData, ItemStack itemStack){
        //enabled
        boolean enabled = false;
        if(holder.equalsIgnoreCase("autoKill")){
            String key = Configurationx.itemStackToEntityType(itemStack);
            if(playerData.getDatabaseYAML().getStringList(holder).contains(key)) enabled = true;
        } else {
            if(playerData.getDatabaseYAML().getStringList(holder).contains(itemStack.getType() + "")) enabled = true;
        }
        //lore
        List<String> cLore = new ArrayList<>();
        for(String l : Configurationx.getGuiYAML().getStringList("lore." + holder)){
            cLore.add(ChatColor.translateAlternateColorCodes('&', l
                    .replace("{enabled}", enabled + "")
                    .replace("false", Configurationx.getGuiYAML().getString("false"))
                    .replace("true", Configurationx.getGuiYAML().getString("true"))));
        }
        ItemMeta itemMeta = itemStack.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        if(itemMeta != null) {
            itemMeta.setLore(cLore);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
}
