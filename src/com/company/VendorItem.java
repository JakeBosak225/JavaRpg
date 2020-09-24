package com.company;

public class VendorItem {
    private Item details;
    private int quantityOnHand;
    private int sellPrice;

    public VendorItem(){}

    public VendorItem(Item details, int quantityOnHand, int sellPrice) {
        this.details = details;
        this.quantityOnHand = quantityOnHand;
        this.sellPrice = sellPrice;
    }

    public Item getDetails() {
        return details;
    }

    public void setDetails(Item details) {
        this.details = details;
    }

    public int getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(int quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }
}
