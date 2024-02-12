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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.bnjrKemal.inventory.AutoKill.getMonsterEggs;
import static com.bnjrKemal.inventory.MainInventory.getHolder;

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

    public static ItemStack getPreviousPage(int page){
        ItemStack is = (ItemStack) getGuiYAML().get("previouspage");
        is.setAmount(page);
        return is;
    }

    public static ItemStack getNextPage(int page){
        ItemStack is = (ItemStack) getGuiYAML().get("nextpage");
        is.setAmount(page);
        return is;
    }

    public static Integer getMaxPage(Inventory inventory) {
        ConfigurationSection section = getSettingsYAML().getConfigurationSection(getHolder(inventory.getHolder()));
        if (section == null) return 0;
        int totalKeys = section.getKeys(false).size();
        int keysPerPage = 28;
        if (totalKeys == 0) return 0;
        int maxPage = (int) Math.ceil(totalKeys / keysPerPage);
        return maxPage;
    }

    public static List<Integer> getSlots() {return slots;}

    public static void listingItems(Inventory inventory, int page, UUID uuid) {
        List<ItemStack> itemStackList = getKeys(inventory);
        inventory.clear();
        if(page > 1) inventory.setItem(45, getPreviousPage(page - 1));
        if(page <= getMaxPage(inventory)) inventory.setItem(53, getNextPage(page + 1));
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

    public static List<ItemStack> getKeysForPage(Inventory inventory, int page, List<ItemStack> itemStackList) {
        ConfigurationSection itemsSection = getSettingsYAML().getConfigurationSection(getHolder(inventory.getHolder()));
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

    private static List<ItemStack> getKeys(Inventory inventory) {
        if(Configurationx.getSettingsYAML().getConfigurationSection(getHolder(inventory.getHolder())) == null) return null;
        if(inventory.getHolder() instanceof AutoCollect || inventory.getHolder() instanceof AutoHarvest) {
            List<ItemStack> itemStacks = new ArrayList<>();
            for(String str : getSettingsYAML().getConfigurationSection(getHolder(inventory.getHolder())).getKeys(false)){
                ItemStack is = new ItemStack(Material.getMaterial(str));
                if(is != null) itemStacks.add(is);
            }
            return itemStacks;
        }
        if(inventory.getHolder() instanceof AutoKill) return getMonsterEggs();
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
