package com.bnjrKemal;

import com.bnjrKemal.inventory.AutoCollect;
import com.bnjrKemal.inventory.AutoHarvest;
import com.bnjrKemal.inventory.AutoKill;
import com.bnjrKemal.inventory.MainInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Configurationx {

    private static AutoKFC main;

    private static File guiFile;
    private static YamlConfiguration guiYAML;
    private static File settingsFile;
    private static YamlConfiguration settingsYAML;

    public static List<Integer> slots = Arrays.asList(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43);

    public Configurationx(AutoKFC instance){
        main = instance;

        guiFile = new File(instance.getDataFolder(), "gui.yml");
        settingsFile = new File(instance.getDataFolder(), "settings.yml");

        if(!guiFile.exists()) try {guiFile.getParentFile().mkdirs();guiFile.createNewFile();instance.saveResource("gui.yml", true);} catch (IOException e) {throw new RuntimeException(e);}
        if(!settingsFile.exists()) try {settingsFile.getParentFile().mkdirs();settingsFile.createNewFile();instance.saveResource("settings.yml", true);} catch (IOException e) {throw new RuntimeException(e);}

        guiYAML = YamlConfiguration.loadConfiguration(guiFile);
        settingsYAML = YamlConfiguration.loadConfiguration(settingsFile);

        if(!new File(AutoKFC.plugin.getDataFolder(), "database").exists()) new File(AutoKFC.plugin.getDataFolder(), "database").mkdir();
    }

    public static void reload(CommandSender sender) {
        guiFile = new File(main.getDataFolder(), "gui.yml");
        guiYAML = YamlConfiguration.loadConfiguration(guiFile);
        settingsFile = new File(main.getDataFolder(), "settings.yml");
        settingsYAML = YamlConfiguration.loadConfiguration(settingsFile);
        sender.sendMessage(ChatColor.AQUA + "[AutoKFC] You have reloaded all the files");
    }

    public static void save(){
        try {
            guiYAML.save(guiFile);
            settingsYAML.save(settingsFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static YamlConfiguration getGuiYAML() {return guiYAML;}

    public static File getGuiFile() {return guiFile;}

    public static YamlConfiguration getSettingsYAML() {return settingsYAML;}

    public static ItemStack getPreviousPage(){return (ItemStack) getGuiYAML().get("previouspage");}

    public static ItemStack getNextPage(){return (ItemStack) getGuiYAML().get("nextpage");}

    public static List<ItemStack> getKeysForPage(Inventory inventory, int page, List<ItemStack> itemStackList) {
        ConfigurationSection itemsSection = getSettingsYAML().getConfigurationSection(getHolder(inventory));
        if (itemsSection == null) return Collections.emptyList();
        int pageSize = 28;
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, itemStackList.size());
        if (startIndex < 0 || startIndex >= endIndex) return Collections.emptyList();
        List<ItemStack> itemStacks = new ArrayList<>();
        for (int i = startIndex; i < endIndex;i++) {
            ItemStack itemStack = itemStackList.get(i);
            if (itemStack != null && itemStack.getItemMeta() != null) itemStacks.add(itemStack);
        }
        return itemStacks;
    }

    public static List<Integer> getSlots() {return slots;}

    public static Integer getMaxPage(Inventory inventory) {
        ConfigurationSection section = getSettingsYAML().getConfigurationSection(getHolder(inventory));
        if (section == null) {
            return 0; // veya istediğiniz başka bir değer
        }
        int totalKeys = getSettingsYAML().getConfigurationSection(getHolder(inventory)).getKeys(false).size();
        int keysPerPage = 28;
        if (totalKeys == 0) return 0;
        int maxPage = (int) Math.ceil(totalKeys / keysPerPage);
        return maxPage;
    }

    public static void listingItems(Inventory inventory, int page, UUID uuid) {
        List<ItemStack> itemStackList = getKeys(inventory);
        inventory.clear();
        if(page > 1) inventory.setItem(45, getPreviousPage());
        if(page <= getMaxPage(inventory)) inventory.setItem(53, getNextPage());
        List<ItemStack> keysForPage = getKeysForPage(inventory, page, itemStackList);
        List<ItemStack> gotLores = new LoreSystem(uuid).makeLore(keysForPage);
        int slotIndex = 0;
        for (Integer slot : getSlots()) {
            if (slotIndex >= gotLores.size()) break;
            ItemStack key = gotLores.get(slotIndex);
            inventory.setItem(slot, key);
            slotIndex++;
        }
        for (int i = 0; i<=53; i++) if(inventory.getItem(i) == null) inventory.setItem(i, MainInventory.otherSlots);
    }

    private static List<ItemStack> getKeys(Inventory inventory) {
        if(Configurationx.getSettingsYAML().getConfigurationSection(getHolder(inventory)) == null) return null;
        if(inventory.getHolder() instanceof AutoCollect || inventory.getHolder() instanceof AutoHarvest) {
            List<ItemStack> itemStacks = new ArrayList<>();
            for(String str : getSettingsYAML().getConfigurationSection(getHolder(inventory)).getKeys(false)){
                ItemStack is = new ItemStack(Material.getMaterial(str));
                if(is != null) itemStacks.add(is);
            }
            return itemStacks;
        }
        if(inventory.getHolder() instanceof AutoKill) return getMonsterEggs();
        return null;
    }

    private static List<ItemStack> getMonsterEggs(){
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

    public static String getHolder(Inventory inventory){
        if(inventory.getHolder() instanceof AutoKill) return "autoKill";
        if(inventory.getHolder() instanceof AutoHarvest) return "autoHarvest";
        if(inventory.getHolder() instanceof AutoCollect) return "autoCollect";
        return null;
    }

    public static int getPriceOfItem(String holder, Material type){
        if(getSettingsYAML().get(holder) == null) return 0;
        return getSettingsYAML().getInt(holder + "." + type);
    }

    public static double getCurrentBukkitVersion() {
        String currentVersion = Bukkit.getServer().getBukkitVersion();
        String[] versionParts = currentVersion.split("\\.");

        int major = 0;
        int minor = 0;

        if (versionParts.length >= 2) {
            major = Integer.parseInt(versionParts[0]);
            minor = Integer.parseInt(versionParts[1]);
        }

        return Double.parseDouble(major + "." + minor);
    }

    public static String itemStackToEntityType(ItemStack itemStack){
        if(getCurrentBukkitVersion() >= 1.8 || getCurrentBukkitVersion() <= 1.12){
            return (itemStack.getData() + "").replace("SPAWN EGG{", "").replace("}", "");
        }
        return null;
    }

}
