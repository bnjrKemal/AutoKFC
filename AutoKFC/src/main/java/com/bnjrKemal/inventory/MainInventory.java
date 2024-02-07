package com.bnjrKemal.inventory;

import com.bnjrKemal.Configurationx;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MainInventory implements InventoryHolder {

    Inventory inventory;

    int row;
    String title;
    public static Map<Integer, ItemStack> slots;
    public static ItemStack otherSlots;

    public MainInventory(){
        this.row = Configurationx.getGuiYAML().getInt("row");
        this.title = ChatColor.translateAlternateColorCodes('&', Configurationx.getGuiYAML().getString("title"));
        slots = new HashMap<>();
        Configurationx.getGuiYAML().getConfigurationSection("slots").getKeys(false).forEach(slot -> slots.put(Integer.parseInt(slot), Configurationx.getGuiYAML().get("slots." + slot + ".item") != null ? Configurationx.getGuiYAML().getItemStack("slots." + slot + ".item") : new ItemStack(Material.STONE)));
        this.otherSlots = Configurationx.getGuiYAML().get("other-slots") == null ? new ItemStack(Material.REDSTONE) : Configurationx.getGuiYAML().getItemStack("other-slots");
        this.inventory = createInventory();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private Inventory createInventory(){

        inventory = Bukkit.createInventory(this, row*9, title);

        for(int i = 0; i<row*9 ;i++) inventory.setItem(i, otherSlots);

        for(Map.Entry<Integer, ItemStack> entry : slots.entrySet()){
            int slot = entry.getKey();
            ItemStack itemStack = entry.getValue();
            inventory.setItem(slot, itemStack);
        }
        return inventory;
    }

}
