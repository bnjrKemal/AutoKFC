package com.bnjrKemal;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutoKFC extends JavaPlugin {

    public static AutoKFC plugin;

    @Override
    public void onEnable() {
        plugin = this;
        new Configurationx(this);

        //The Commands
        getCommand("autoKFC").setExecutor(new MainCommand());

        //The listeners
        Bukkit.getPluginManager().registerEvents(new MainListener(), this);

    }

    @Override
    public void onDisable() {
        Configurationx.save();
    }
}
