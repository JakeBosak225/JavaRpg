package com.company;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InventoryItem {
    private Item details;
    private int quantity;

    public InventoryItem(Item details, int quantity) {
        this.details = details;
        this.quantity = quantity;
    }

    public InventoryItem() {

    }

    public static List<InventoryItem> sortInventoryById(List<InventoryItem> listToSort) {
      listToSort.sort(Comparator.comparing(InventoryItem::getInventoryItemId));

      return listToSort;
    }

    public Item getDetails() {
        return details;
    }

    public void setDetails(Item details) {
        this.details = details;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static int getInventoryItemId(InventoryItem ii){
        return ii.getDetails().getId();
    }
}
