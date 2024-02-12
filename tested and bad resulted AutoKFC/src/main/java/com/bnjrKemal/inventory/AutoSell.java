package com.bnjrKemal.inventory;

import com.bnjrKemal.Configurationx;
import com.bnjrKemal.Stored;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class AutoSell {


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

}
