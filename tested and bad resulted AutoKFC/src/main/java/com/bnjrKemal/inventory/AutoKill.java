package com.bnjrKemal.inventory;

import com.bnjrKemal.AutoKFC;
import com.bnjrKemal.Configurationx;
import com.bnjrKemal.LoreSystem;
import com.bnjrKemal.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.bnjrKemal.Configurationx.getCurrentBukkitVersion;

public class AutoKill implements InventoryHolder, Listener {

    Inventory inventory;
    Player player;

    public AutoKill() {}

    public AutoKill createInventory(Player player) {
        this.player = player;
        inventory = Bukkit.createInventory(this, 54, "");
        Configurationx.listingItems(inventory, 1, player.getUniqueId());
        return this;
    }

    @Override
    public Inventory getInventory() {return inventory;}

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getClickedInventory().getHolder() instanceof AutoKill)) return;
        e.setCancelled(true);
        if(e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null || e.getCursor() == null) return;

        UUID uuid = player.getUniqueId();
        if (Configurationx.getPreviousPage(e.getCurrentItem().getAmount()).equals(e.getCurrentItem()) || Configurationx.getNextPage(e.getCurrentItem().getAmount()).equals(e.getCurrentItem())) {
            int page = e.getCurrentItem().getAmount();
            Configurationx.listingItems(e.getClickedInventory(), page, uuid);
            return;
        }
        if(!Configurationx.slots.contains(e.getSlot())) return;
        PlayerData playerData = new PlayerData(player.getUniqueId());
        if(e.getClick().equals(ClickType.DROP)) playerData.changeStatus(e.getCurrentItem());
        e.getInventory().setItem(e.getSlot(), new LoreSystem(uuid).changeLoreOneItemStack(playerData, e.getCurrentItem()));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if (!(e.getInventory().getHolder() instanceof AutoKill)) return;
        Bukkit.getScheduler().runTaskLater(AutoKFC.plugin, () -> {
            e.getPlayer().openInventory(new MainInventory().getInventory());
        }, 1);
    }

    public static List<ItemStack> getMonsterEggs(){
        List<ItemStack> list = new ArrayList<>();
        if(getCurrentBukkitVersion() >= 1.8 || getCurrentBukkitVersion() <= 1.12) {
            Set<String> keys = Configurationx.getSettingsYAML().getConfigurationSection("autoKill").getKeys(false);
            for (EntityType entity : EntityType.values()) {
                if (keys.contains(entity + "")) {
                    ItemStack itemStack = new ItemStack(Material.MONSTER_EGG, 1, entity.getTypeId());
                    list.add(itemStack);
                }
            }
        }
        return list;
    }

}
