package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enemy extends LivingCreature {
    private Random rng = new Random();

    private int id;
    private int minRewardGold;
    private int maxRewardGold;
    private int minRewardExp;
    private int maxRewardExp;
    private int minAttack;
    private int maxAttack;

    public List<LootItem> lootTable;


    public Enemy() {
        super();
    }

    public Enemy(int id, String name, int currentHp, int maxHp, int minRewardGold, int maxRewardGold, int minRewardExp, int maxRewardExp, int minAttack, int maxAttack, List<LootItem> lootTable) {
        super(name, currentHp, maxHp);
        this.id = id;
        this.minRewardGold = minRewardGold;
        this.maxRewardGold = maxRewardGold;
        this.minRewardExp = minRewardExp;
        this.maxRewardExp = maxRewardExp;
        this.minAttack = minAttack;
        this.maxAttack = maxAttack;
        this.lootTable = lootTable;
    }

    public void getLootItems(Player player) {
        List<InventoryItem> itemsDropped = new ArrayList<>();

        for (LootItem lootItem : getLootTable()) {
            //Check if key item
            if (lootItem.isKeyItem()) {
                //check is player has it
                boolean hasKeyItem = false;

                for(Item i : player.getKeyItems()){
                    if(i.getId() == lootItem.getDetails().getId()){
                        hasKeyItem = true;
                    }
                }

                if (hasKeyItem) {
                    continue;
                } else {
                    //Item is Key and player doesn't have it, add to drop list
                    System.out.println(getName() + " dropped a key item: " + lootItem.getDetails().getName() + "!");
                    itemsDropped.add(new InventoryItem(WorldData.getItemById(lootItem.getDetails().getId()), 1));
                    player.getKeyItems().add(WorldData.getItemById(lootItem.getDetails().getId()));
                    continue;
                }
            }

            //get drop rate and compare to drop chance
            int dropChance = rng.nextInt(101);

            if (dropChance < lootItem.getDropRate()) {
                int numberDropped = rng.nextInt((lootItem.getMaxQuantity() - lootItem.getMinQuantity()) + 1) + lootItem.getMinQuantity();

                System.out.println("The " + getName() + " dropped " + numberDropped + " " +
                        lootItem.getDetails().getName());

                itemsDropped.add(new InventoryItem(WorldData.getItemById(lootItem.getDetails().getId()), numberDropped));
            }
        }

        for (InventoryItem ii : itemsDropped) {
            boolean hasItem = false;

            for(InventoryItem playerInventory : player.getInventory()){
                if(playerInventory.getDetails().getId() == ii.getDetails().getId()){
                    hasItem = true;
                    playerInventory.setQuantity(playerInventory.getQuantity() + ii.getQuantity());
                }
            }

            if(!hasItem){
                player.getInventory().add(new InventoryItem(WorldData.getItemById(ii.getDetails().getId()), ii.getQuantity()));
            }
        }
    }


    public int getAttackPower() {
        return rng.nextInt((maxAttack - minAttack) + 1) + minAttack;
    }

    public int getRewardGold() {
        return rng.nextInt((maxRewardGold - minRewardGold) + 1) + minRewardGold;
    }

    public int getRewardExp() {
        return rng.nextInt((maxRewardExp - minRewardExp) + 1) + minRewardExp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMinRewardGold() {
        return minRewardGold;
    }

    public void setMinRewardGold(int minRewardGold) {
        this.minRewardGold = minRewardGold;
    }

    public int getMaxRewardGold() {
        return maxRewardGold;
    }

    public void setMaxRewardGold(int maxRewardGold) {
        this.maxRewardGold = maxRewardGold;
    }

    public int getMinRewardExp() {
        return minRewardExp;
    }

    public void setMinRewardExp(int minRewardExp) {
        this.minRewardExp = minRewardExp;
    }

    public int getMaxRewardExp() {
        return maxRewardExp;
    }

    public void setMaxRewardExp(int maxRewardExp) {
        this.maxRewardExp = maxRewardExp;
    }

    public int getMinAttack() {
        return minAttack;
    }

    public void setMinAttack(int minAttack) {
        this.minAttack = minAttack;
    }

    public int getMaxAttack() {
        return maxAttack;
    }

    public void setMaxAttack(int maxAttack) {
        this.maxAttack = maxAttack;
    }

    public List<LootItem> getLootTable() {
        return lootTable;
    }

    public void setLootTable(List<LootItem> lootTable) {
        this.lootTable = lootTable;
    }
}
