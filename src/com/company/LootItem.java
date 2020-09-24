package com.company;

public class LootItem {
    private Item details;
    private int minQuantity;
    private int maxQuantity;
    private int dropRate;
    private boolean isKeyItem;

    public LootItem(Item details, int minQuantity, int maxQuantity, int dropRate, boolean isKeyItem) {
        this.details = details;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.dropRate = dropRate;
        this.isKeyItem = isKeyItem;
    }

    public LootItem() {

    }

    public Item getDetails() {
        return details;
    }

    public void setDetails(Item details) {
        this.details = details;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public int getDropRate() {
        return dropRate;
    }

    public void setDropRate(int dropRate) {
        this.dropRate = dropRate;
    }

    public boolean isKeyItem() {
        return isKeyItem;
    }

    public void setKeyItem(boolean keyItem) {
        isKeyItem = keyItem;
    }
}
