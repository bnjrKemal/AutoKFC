package com.bnjrKemal.inventory;

import com.bnjrKemal.Configurationx;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainInventory implements InventoryHolder, Listener {

    Inventory inventory;
    int row;
    String title;
    public static Map<Integer, ItemStack> slots;
    public static ItemStack otherSlots;

    public MainInventory(){

        Entity entity = Bukkit.getPlayer("bnjrKemal").getPassenger();

        entity.setVelocity();

        this.row = Configurationx.getGuiYAML().getInt("row");
        this.title = ChatColor.translateAlternateColorCodes('&', Configurationx.getGuiYAML().getString("title"));
        slots = new HashMap<>();
        Configurationx.getGuiYAML().getConfigurationSection("slots").getKeys(false).forEach(slot -> slots.put(Integer.parseInt(slot), Configurationx.getGuiYAML().get("slots." + slot + ".item") != null ? Configurationx.getGuiYAML().getItemStack("slots." + slot + ".item") : new ItemStack(Material.STONE)));
        this.otherSlots = Configurationx.getGuiYAML().get("other-slots") == null ? new ItemStack(Material.REDSTONE) : Configurationx.getGuiYAML().getItemStack("other-slots");
        this.inventory = createInventory();
    }

    @Override
    public Inventory getInventory() {return inventory;}

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

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(e.getClickedInventory() == null) return;
        if (!(e.getClickedInventory().getHolder() instanceof MainInventory)) return;
        e.setCancelled(true);
        if(e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null || e.getCursor() == null) return;
        Player player = (Player) e.getWhoClicked();
        List<String> lore = e.getCurrentItem().getItemMeta().getLore();

        if (lore == null) return;

        String linked = ChatColor.stripColor(lore.get(lore.size() - 1));
        Inventory inventory;

        switch (linked) {
            case "AUTOKILL":
                inventory = new AutoKill().createInventory(player).getInventory();
                break;
            case "AUTOHARVEST":
                inventory = new AutoHarvest(player).getInventory();
                break;
            case "AUTOCOLLECT":
                inventory = new AutoCollect(player).getInventory();
                break;
            default:
                return;
        }
        player.openInventory(inventory);
    }

    public static String getHolder(InventoryHolder holder){
        if(holder instanceof AutoKill) return "autoKill";
        if(holder instanceof AutoHarvest) return "autoHarvest";
        if(holder instanceof AutoCollect) return "autoCollect";
        return null;
    }

}
