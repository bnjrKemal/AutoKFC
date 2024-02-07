package com.bnjrKemal;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    private static File databaseFile;
    private static YamlConfiguration databaseYAML;
    private UUID uuid;
    private String holder;

    public PlayerData(UUID uuid){
        databaseFile = new File(AutoKFC.plugin.getDataFolder() + "/database", uuid + ".yml");
        databaseYAML = YamlConfiguration.loadConfiguration(databaseFile);
        this.uuid = uuid;
        this.holder = MainListener.inventories.get(uuid);
    }

    public YamlConfiguration getDatabaseYAML() {return databaseYAML;}

    public static File getDatabaseFile() {return databaseFile;}

    public void changeStatus(ItemStack itemStack){
        String key = itemStack.getType().toString();;
        if(key.equalsIgnoreCase("MONSTER_EGG"))
            key = Configurationx.itemStackToEntityType(itemStack);
        List<String> list = getDatabaseYAML().getStringList(holder);
        if(!list.contains(key)) {
            list.add(key);
        } else if (list.contains(key)) {
            list.remove(key);
        }
        getDatabaseYAML().set(holder, list);
        try {getDatabaseYAML().save(databaseFile);} catch (IOException e) {throw new RuntimeException(e);}
    }

    public void deposit(Material type) {
        Stored stored = new Stored(uuid);
        int allAmount = stored.getSize(type);
        int amount = Math.min(64, allAmount);
        if (amount <= 0) return;
        Map<Integer, ItemStack> rest = Bukkit.getPlayer(uuid).getInventory().addItem(new ItemStack(type, amount));
        int failedToGive = 0;
        for (Map.Entry<Integer, ItemStack> entry : rest.entrySet()) failedToGive += entry.getValue().getAmount();
        stored.setAmount(type, amount - failedToGive);
    }

    public void depositAll(Material type) {
        Stored stored = new Stored(uuid);
        int allAmount = stored.getSize(type);
        if (allAmount <= 0) return;
        Map<Integer, ItemStack> rest = Bukkit.getPlayer(uuid).getInventory().addItem(new ItemStack(type, allAmount));
        int failedToGive = 0;
        for (Map.Entry<Integer, ItemStack> entry : rest.entrySet()) failedToGive += entry.getValue().getAmount();
        stored.setAmount(type, failedToGive);
    }

    public void sellItem(Material type) {
        Stored stored = new Stored(uuid);
        int allAmount = stored.getSize(type);
        int price = Configurationx.getPriceOfItem(holder, type);
        if(price <= 0 || allAmount <= 0) return;
        int willSell = Math.min(64, allAmount);
        int currentAmount = allAmount - willSell;
        stored.setAmount(type, currentAmount);
        int result = willSell * price;
        Bukkit.getPlayer(uuid).sendMessage(result + " oyun parası eklendi ve " + willSell + " adet satıldı. şuanki mevcut sayı : " + currentAmount);
        //add result to player's balance
    }

    public void sellItemAll(Material type) {
        Stored stored = new Stored(uuid);
        int allAmount = stored.getSize(type);
        int price = Configurationx.getPriceOfItem(holder, type);
        if (price <= 0 || allAmount <= 0) return;
        int result = allAmount * price;
        stored.setAmount(type, 0);
        Bukkit.getPlayer(uuid).sendMessage(result + " oyun parası eklendi ve " + allAmount + " adet satıldı.");
    }

    public int getUpgrade() {
        return getDatabaseYAML().get("upgrade") == null ? Configurationx.getGuiYAML().getInt("upgrade.1.capacity") : Configurationx.getGuiYAML().getInt("upgrade." + getDatabaseYAML().getInt("upgrade") + ".capacity");
    }
}
