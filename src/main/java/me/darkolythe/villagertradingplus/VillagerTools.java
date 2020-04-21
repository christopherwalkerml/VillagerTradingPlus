package me.darkolythe.villagertradingplus;

import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VillagerTools {

    private VillagerTradingPlus main;
    public VillagerTools(VillagerTradingPlus plugin) {
        this.main = plugin; // set it equal to an instance of main
    }

    public Boolean isFullShulker(ShulkerBox shulker) {
        Inventory shulkerinv = shulker.getInventory();
        ItemStack item = shulkerinv.getItem(0);
        for (int i = 0; i < 27; i++) {
            if (shulkerinv.getItem(i) != null) {
                if ((shulkerinv.getItem(i).getType() != item.getType()) || (shulkerinv.getItem(i).getAmount() != shulkerinv.getItem(i).getMaxStackSize())) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public Boolean isBlacklist(ItemStack item) {
        if (item != null) {
            for (String str : main.blacklist) {
                if (item.getType() == Material.getMaterial(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int clamp(int val, int min, int max) {
        if (val < min) {
            return min;
        } else if (val > max) {
            return max;
        } else {
            return val;
        }
    }

}
