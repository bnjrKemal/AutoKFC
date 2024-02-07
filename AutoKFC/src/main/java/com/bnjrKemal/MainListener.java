package com.bnjrKemal;

import com.bnjrKemal.inventory.AutoCollect;
import com.bnjrKemal.inventory.AutoHarvest;
import com.bnjrKemal.inventory.AutoKill;
import com.bnjrKemal.inventory.MainInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainListener implements Listener {

    public static Map<UUID, Integer> pages = new HashMap<>();
    public static Map<UUID, String> inventories = new HashMap<>();

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if (e.getInventory().getHolder() instanceof AutoKill || e.getInventory().getHolder() instanceof AutoHarvest || e.getInventory().getHolder() instanceof AutoCollect) {
            Bukkit.getScheduler().runTaskLater(AutoKFC.plugin, () -> {
                MainInventory mainInventory = new MainInventory();
                e.getPlayer().openInventory(mainInventory.getInventory());
            }, 1);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null) return;
        if (e.getClickedInventory().getHolder() instanceof MainInventory) {
            handleMainInventoryClick(e);
        } else if (Configurationx.getHolder(e.getClickedInventory()) != null) {
            e.setCancelled(true);
            if(e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null || e.getCursor() == null) return;
            Player player = (Player) e.getWhoClicked();
            UUID uuid = player.getUniqueId();
            if (e.getCurrentItem().equals(Configurationx.getPreviousPage()) || e.getCurrentItem().equals(Configurationx.getNextPage())) {
                int page = (e.getCurrentItem().equals(Configurationx.getPreviousPage()) ? -1 : 1) + (pages.get(player.getUniqueId()) == null ? 1 : pages.get(player.getUniqueId()));
                Configurationx.listingItems(e.getClickedInventory(), page, uuid);
                pages.put(uuid, page);
                return;
            }
            if(!Configurationx.slots.contains(e.getSlot())) return;
            PlayerData playerData = new PlayerData(player.getUniqueId());
            switch (e.getClick()){
                case DROP: playerData.changeStatus(e.getCurrentItem());
                case LEFT: playerData.sellItem(e.getCurrentItem().getType());
                case SHIFT_LEFT: playerData.sellItemAll(e.getCurrentItem().getType());
                case RIGHT: playerData.deposit(e.getCurrentItem().getType());
                case SHIFT_RIGHT: playerData.depositAll(e.getCurrentItem().getType());
            }
            e.getInventory().setItem(e.getSlot(), new LoreSystem(uuid).changeLoreOneItemStack(playerData, e.getCurrentItem()));
        }
    }

    private void handleMainInventoryClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if(e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null || e.getCursor() == null) return;
        Player player = (Player) e.getWhoClicked();
        List<String> lore = e.getCurrentItem().getItemMeta().getLore();

        if (lore == null) return;

        String linked = ChatColor.stripColor(lore.get(lore.size() - 1));
        Inventory inventory;

        switch (linked) {
            case "AUTOKILL":
                inventories.put(player.getUniqueId(), "autoKill");
                inventory = new AutoKill(player).getInventory();
                break;
            case "AUTOHARVEST":
                inventories.put(player.getUniqueId(), "autoHarvest");
                inventory = new AutoHarvest(player).getInventory();
                break;
            case "AUTOCOLLECT":
                inventories.put(player.getUniqueId(), "autoCollect");
                inventory = new AutoCollect(player).getInventory();
                break;
            default:
                return;
        }
        pages.put(e.getWhoClicked().getUniqueId(), 1);

        player.openInventory(inventory);
    }
}
