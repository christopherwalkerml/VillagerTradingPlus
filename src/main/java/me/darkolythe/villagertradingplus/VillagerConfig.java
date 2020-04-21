package me.darkolythe.villagertradingplus;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.MerchantRecipe;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class VillagerConfig {

    public VillagerTradingPlus main; //initialize the main variable as Type main class
    public VillagerConfig(VillagerTradingPlus plugin) {
        this.main = plugin; //set it equal to an instance of main
    }

    private VillagerTradingPlus plugin = VillagerTradingPlus.getPlugin(VillagerTradingPlus.class);

    public FileConfiguration tradelistcfg;
    public File tradelist;

    public void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        tradelist = new File(plugin.getDataFolder(), "VillagerTrades.yml");

        if (!tradelist.exists()) {
            try {
                tradelist.createNewFile();
                System.out.println(VillagerTradingPlus.prefix + ChatColor.GREEN + "VillagerTrades.yml has been created");
            } catch (IOException e) {
                System.out.println(VillagerTradingPlus.prefix + ChatColor.RED + "Could not create VillagerTrades.yml");
            }
        }
        tradelistcfg = YamlConfiguration.loadConfiguration(tradelist);
    }


    public void saveVillagerTrades(UUID uuid) {
        try {
            if (!tradelistcfg.contains("villager." + uuid)) {
                tradelistcfg.createSection("villager." + uuid); //if the request doesnt exist in the config, create it
            }
            String section = "villager." + uuid;

            tradelistcfg.set(section + ".ingredient", main.villagertrades.get(uuid).getIngredients().get(0));
            tradelistcfg.set(section + ".result", main.villagertrades.get(uuid).getResult());
            tradelistcfg.set(section + ".maxUses", main.villagertrades.get(uuid).getMaxUses());
            tradelistcfg.set(section + ".uses", main.villagertrades.get(uuid).getUses());

            tradelistcfg.save(tradelist);
        } catch (IOException e) {
            System.out.println(VillagerTradingPlus.prefix + ChatColor.RED + "Could not save VillagerTrades.yml");
        }
    }


    public void loadVillagerTrades() {
        if (tradelistcfg.contains("villager")) {
            for (String key: tradelistcfg.getConfigurationSection("villager.").getKeys(false)) {
                MerchantRecipe recipe = new MerchantRecipe(tradelistcfg.getItemStack("villager." + key + ".result"), tradelistcfg.getInt("villager." + key + ".maxUses"));
                recipe.addIngredient(tradelistcfg.getItemStack("villager." + key + ".ingredient"));
                recipe.setMaxUses(tradelistcfg.getInt("villager." + key + ".maxUses"));
                recipe.setUses(tradelistcfg.getInt("villager." + key + ".uses"));
                main.villagertrades.put(UUID.fromString(key), recipe);
            }
        }
    }
}
