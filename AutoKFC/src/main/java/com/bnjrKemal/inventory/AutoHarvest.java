package com.bnjrKemal.inventory;

import com.bnjrKemal.Configurationx;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class AutoHarvest implements InventoryHolder {

    Inventory inventory;
    Player player;

    public AutoHarvest(Player player) {
        this.player = player;
        this.inventory = createInventory();
    }

    private Inventory createInventory() {
        inventory = Bukkit.createInventory(this, 54, "");
        Configurationx.listingItems(inventory, 1, player.getUniqueId());
        return inventory;
    }

    @Override
    public Inventory getInventory() {return inventory;}

}
