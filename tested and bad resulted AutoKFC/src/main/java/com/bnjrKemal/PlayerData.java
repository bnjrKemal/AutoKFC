package com.bnjrKemal;

import com.bnjrKemal.inventory.MainInventory;
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
    private String holder;

    public PlayerData(UUID uuid){
        databaseFile = new File(AutoKFC.plugin.getDataFolder() + "/database", uuid + ".yml");
        databaseYAML = YamlConfiguration.loadConfiguration(databaseFile);
        this.holder = MainInventory.getHolder(Bukkit.getPlayer(uuid).getInventory().getHolder());
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

    public int getUpgrade() {
        return getDatabaseYAML().get("upgrade") == null ? Configurationx.getGuiYAML().getInt("upgrade.1.capacity") : Configurationx.getGuiYAML().getInt("upgrade." + getDatabaseYAML().getInt("upgrade") + ".capacity");
    }
}
