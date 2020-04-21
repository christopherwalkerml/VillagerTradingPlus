package me.darkolythe.villagertradingplus;

import org.bukkit.Particle;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.entity.VillagerReplenishTradeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VillagerInteract implements Listener {

    private VillagerTradingPlus main;
    public VillagerInteract(VillagerTradingPlus plugin) {
        this.main = plugin; // set it equal to an instance of main
    }

    @EventHandler
    public void onPlayerEntityInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("villagertradingplus.use") && event.getHand().equals(EquipmentSlot.HAND)) {
            Entity entity = event.getRightClicked();
            UUID puuid = player.getUniqueId();
            UUID vuuid = entity.getUniqueId();
            if (entity instanceof Villager) {
                Villager villager = (Villager) entity;
                if (player.isSneaking()) {
                    main.setVillagerClicked(puuid, vuuid); //save what villagers the player has clicked until server restart
                    if (main.getVillagerClicked(puuid, vuuid)) { //this toggles on and off clicking
                        player.spawnParticle(Particle.VILLAGER_HAPPY, entity.getLocation(), 50, 0.25, 1, 0.25);
                    } else {
                        player.spawnParticle(Particle.SPELL_WITCH, entity.getLocation(), 50, 0.25, 0.5, 0.25);
                    }
                } else if (main.getVillagerClicked(puuid, vuuid)) { //if villager clicking is on, it will try to detect if the item in hand is able to be a trade
                    ItemStack handitem = player.getInventory().getItemInMainHand();
                    boolean success = false;
                    if (main.stacktype.equals("SHULKER") && handitem.getType().toString().contains("SHULKER_BOX")) {
                        ShulkerBox shulkerbox = (ShulkerBox) ((BlockStateMeta) handitem.getItemMeta()).getBlockState();
                        boolean isFull = main.villagertools.isFullShulker(shulkerbox);
                        boolean isBlackList = main.villagertools.isBlacklist(shulkerbox.getInventory().getItem(0));
                        if (isFull && !isBlackList) {
                            success = getVillagerTrades(shulkerbox.getInventory().getItem(0), villager);
                        }
                    } else if (main.stacktype.equals("STACK")) {
                        boolean isFull = (handitem.getAmount() == handitem.getType().getMaxStackSize() && handitem.getAmount() != 1);
                        boolean isBlackList = main.villagertools.isBlacklist(handitem);
                        if (isFull && !isBlackList) {
                            success = getVillagerTrades(handitem, villager);
                        }
                    } else if (main.stacktype.equals("ITEM")) {
                        boolean isOne = (handitem.getAmount() == 1);
                        boolean isBlackList = main.villagertools.isBlacklist(handitem);
                        boolean isShulker = false;
                        if (main.noitemshulker) {
                            isShulker = handitem.getType().toString().contains("SHULKER_BOX");
                        }
                        if (isOne && !isBlackList && !isShulker) {
                            success = getVillagerTrades(handitem, villager);
                        }
                    }
                    if (success) {
                        player.getInventory().setItemInMainHand(null);
                        player.spawnParticle(Particle.VILLAGER_HAPPY, entity.getLocation(), 50, 0.25, 1, 0.25);
                        main.saveVillagerTrades();
                    }
                }
                if (main.villagertrades.containsKey(vuuid)) {
                    if (villager.getRecipe(0).getUses() == villager.getRecipe(0).getMaxUses()) {
                        int randval = main.rand.nextInt(main.upgraderange.get(1) - main.upgraderange.get(0)) + main.upgraderange.get(0);
                        int payamount = main.villagertools.clamp((main.villagertrades.get(villager.getUniqueId()).getIngredients().get(0).getAmount() +
                                randval), main.traderange.get(0), main.traderange.get(1));
                        setVillagerTrades(villager.getRecipe(0).getResult(), payamount, villager);
                    }
                }
            }
        }
    }

    /*
    This function sets the villager trades based on the players in-hand item, and if the villager already has preset trades
     */
    public boolean getVillagerTrades(ItemStack item, Villager villager) {
        int payamount;
        if (!main.villagertrades.containsKey(villager.getUniqueId())) { //If the villager is not a VillagerPlus villager, then give it a default value
            payamount = main.rand.nextInt(main.traderange.get(1) - main.traderange.get(0)) + main.traderange.get(0);
        } else {
            if (main.villagertrades.get(villager.getUniqueId()).getResult().getType() == item.getType()) { //if it is already a VP villager, then check if the player item and trade item are the same
                if (main.villagertrades.get(villager.getUniqueId()).getIngredients().get(0).getAmount() > main.traderange.get(0)) { //then check if the villager's trade is not already maxed out
                    int randval = main.rand.nextInt(main.upgraderange.get(1) - main.upgraderange.get(0)) + main.upgraderange.get(0);
                    payamount = main.villagertools.clamp((main.villagertrades.get(villager.getUniqueId()).getIngredients().get(0).getAmount() -
                            randval), main.traderange.get(0), main.traderange.get(1));
                } else { //the above few lines upgrade the villager's trade
                    return false;
                }
            } else {
                return false;
            }
        }
        setVillagerTrades(item, payamount, villager);
        return true;
    }

    public void setVillagerTrades(ItemStack item, int payamount, Villager villager) {
        villager.setProfession(Villager.Profession.NITWIT);
        MerchantRecipe recipe = new MerchantRecipe(item, main.tradedowngrade); //if the set/upgrade is passed through, set the trades and register the villager
        ItemStack payment = new ItemStack(main.tradetype, payamount);
        recipe.addIngredient(payment);
        List<MerchantRecipe> recipes = new ArrayList<>();
        recipes.add(recipe);
        villager.setRecipes(recipes);
        main.villagertrades.put(villager.getUniqueId(), recipe);
    }


    @EventHandler
    public void onVillagerUnlock(VillagerAcquireTradeEvent event) {
        AbstractVillager villager = event.getEntity();
        if (main.villagertrades.containsKey(villager.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVillagerRefresh(VillagerReplenishTradeEvent event) {
        AbstractVillager villager = event.getEntity();
        if (main.villagertrades.containsKey(villager.getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
