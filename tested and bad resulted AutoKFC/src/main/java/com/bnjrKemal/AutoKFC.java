package com.bnjrKemal;

import com.bnjrKemal.inventory.AutoKill;
import com.bnjrKemal.inventory.MainInventory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class AutoKFC extends JavaPlugin {

    public static AutoKFC plugin;

    @Override
    public void onEnable() {
        plugin = this;
        new Configurationx(this);

        //The Commands
        getCommand("autoKFC").setExecutor(new MainCommand());

        //The listeners
        Bukkit.getPluginManager().registerEvents(new MainInventory(), this);
        Bukkit.getPluginManager().registerEvents(new AutoKill(), this);

    }

    @Override
    public void onDisable() {
        Configurationx.save();
    }

}
