package com.bnjrKemal;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.UUID;

public class Stored {
    
    ConfigurationSection configurationSection;
    PlayerData playerData;

    public Stored(UUID uuid){
        this.playerData = new PlayerData(uuid);
        this.configurationSection = playerData.getDatabaseYAML().getConfigurationSection("stored");
    }
    
    public int getSize(Material type) {return configurationSection == null ? 0 : configurationSection.get(type + "") == null ? 0 : configurationSection.getInt(type + "");}
    
    public void setAmount(Material type, int amount){
        int allAmount = configurationSection.getInt(type + "");
        configurationSection.set(type + "", allAmount - amount);
        try {playerData.getDatabaseYAML().save(playerData.getDatabaseFile());} catch (IOException e) {throw new RuntimeException(e);}
    }
    
}
