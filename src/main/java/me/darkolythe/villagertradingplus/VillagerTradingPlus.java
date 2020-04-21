package me.darkolythe.villagertradingplus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class VillagerTradingPlus extends JavaPlugin {

    public static VillagerTradingPlus plugin;
    public VillagerInteract villagerinteract;
    public VillagerTools villagertools;
    public VillagerConfig villagerconfig;

    public static String prefix = new String(ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "[" + ChatColor.BLUE.toString() + "VillagerTradingPlus" + ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "] ");
    public ArrayList<String> blacklist = new ArrayList<>();
    public String stacktype;
    public Material tradetype;
    public int tradedowngrade;
    public Boolean noitemshulker = true;
    public List<Integer> traderange = new ArrayList<>();
    public List<Integer> upgraderange = new ArrayList<>();

    public HashMap<UUID, MerchantRecipe> villagertrades = new HashMap<>();
    public HashMap<UUID, HashMap<UUID, Boolean>> VillagerClicked = new HashMap<>();

    Random rand = new Random();

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();

        getConfigYML();

        villagerconfig = new VillagerConfig(plugin);
        villagerconfig.setup();
        villagerconfig.loadVillagerTrades();

        getCommand("villagertradingplus").setExecutor(new VillagerCommand());

        villagerinteract = new VillagerInteract(this);
        villagertools = new VillagerTools(this);

        getServer().getPluginManager().registerEvents(villagerinteract, this);

        System.out.println(prefix + ChatColor.GREEN + "VillagerTradingPlus Enabled!");
    }

    @Override
    public void onDisable() {
        saveVillagerTrades();
        System.out.println(prefix + ChatColor.RED + "VillagerTradingPlus Disabled!");
    }



    public static VillagerTradingPlus getInstance() { return plugin; }


    public void setVillagerClicked(UUID puuid, UUID vuuid) {
        if (VillagerClicked.containsKey(puuid)) {
            VillagerClicked.get(puuid).put(vuuid, !getVillagerClicked(puuid, vuuid));
        } else {
            HashMap<UUID, Boolean> vc = new HashMap<>();
            vc.put(vuuid, true);
            VillagerClicked.put(puuid, vc);
        }
    }

    public Boolean getVillagerClicked(UUID puuid, UUID vuuid) {
        if (VillagerClicked.containsKey(puuid)) {
            if (VillagerClicked.get(puuid).containsKey(vuuid)) {
                return VillagerClicked.get(puuid).get(vuuid);
            } else {
                return false;
            }
        } else {
            setVillagerClicked(puuid, vuuid);
            return getVillagerClicked(puuid, vuuid);
        }
    }


    public void saveVillagerTrades() {
        for (UUID uuid: villagertrades.keySet()) {
            villagerconfig.saveVillagerTrades(uuid);
        }
    }


    public void getConfigYML() {
        reloadConfig();

        blacklist = new ArrayList<>();
        traderange = new ArrayList<>();
        upgraderange = new ArrayList<>();

        tradetype = Material.getMaterial(getConfig().getString("tradetype"));
        tradedowngrade = getConfig().getInt("tradedowngrade");
        noitemshulker = getConfig().getBoolean("noitemshulker");
        String stringtraderange = getConfig().getString("traderange");
        for (String str: stringtraderange.split(",")) {
            traderange.add(Integer.parseInt(str));
        }
        String stringupgraderange = getConfig().getString("upgraderange");
        for (String str: stringupgraderange.split(",")) {
            upgraderange.add(Integer.parseInt(str));
        }
        stacktype = getConfig().getString("stacktype");
        String stringblacklist = getConfig().getString("blacklist");
        for (String str: stringblacklist.split(",")) {
            blacklist.add(str);
        }
    }

}
