package com.company;

import java.util.List;
import java.util.Scanner;

public class Vendor {
    private String name;
    private List<VendorItem> vendorsInventory;

    private static Scanner scanner = new Scanner(System.in);

    public Vendor() {
    }

    public Vendor(String name, List<VendorItem> vendorsInventory) {
        this.name = name;
        this.vendorsInventory = vendorsInventory;
    }

    public static void visitVendor(Player player, Vendor vendor) {
        System.out.println("-----Welcome to " + vendor.getName() + "'s Shop!-----");

        while (true) {
            int itemToBuyId;

            System.out.println("---Items For Sale---");
            for (VendorItem vendorItem : vendor.getVendorsInventory()) {
                if (vendorItem.getQuantityOnHand() > 0) {
                    System.out.println(vendorItem.getDetails().getId() + ": " + vendorItem.getDetails().getName() +
                            ". Price: " + vendorItem.getSellPrice() + " G. " + vendorItem.getQuantityOnHand() + " on hand.");
                } else {
                    System.out.println(vendorItem.getDetails().getId() + ": " + vendorItem.getDetails().getName() +
                            ". SOLD OUT");
                }
            }

            System.out.println("Your Gold: " + player.getGold());
            System.out.println("What would you like to buy?: [ID] - or - B to go back.");
            String userInput = scanner.nextLine();

            if (userInput.toUpperCase().equals("B")) {
                break;
            }

            try {
                itemToBuyId = Integer.parseInt(userInput);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number(or B) as your input.");
                scanner.nextLine();
                continue;
            }

            boolean vendorHasItem = false;
            for (VendorItem vendorItem : vendor.getVendorsInventory()) {
                if (vendorItem.getDetails().getId() == itemToBuyId) {
                    vendorHasItem = true;
                }
            }

            if (!vendorHasItem) {
                System.out.println("Please enter a valid input.");
                scanner.nextLine();
                continue;
            }

            VendorItem vendorItemToBuy = new VendorItem();
            for (VendorItem vendorItem : vendor.getVendorsInventory()) {
                if (vendorItem.getDetails().getId() == itemToBuyId) {
                    vendorItemToBuy = vendorItem;
                    break;
                }
            }

            if (vendorItemToBuy.getQuantityOnHand() <= 0) {
                System.out.println("This item is out of stock...");
                scanner.nextLine();
                continue;
            }

            if (player.getGold() < vendorItemToBuy.getSellPrice()) {
                System.out.println("You do not have enough Gold to buy this...");
                scanner.nextLine();
                continue;
            }

            InventoryItem inventoryItemToAdd = new InventoryItem(WorldData.getItemById(vendorItemToBuy.getDetails().getId()), 1);

            //Remove the item from the Vendor
            for (VendorItem vendorItem : vendor.getVendorsInventory()) {
                if (vendorItem.getDetails().getId() == inventoryItemToAdd.getDetails().getId()) {
                    vendorItem.setQuantityOnHand(vendorItem.getQuantityOnHand() - 1);
                }
            }

            boolean playerHasItem = false;
            for (InventoryItem playersItems : player.getInventory()) {
                if (playersItems.getDetails().getId() == inventoryItemToAdd.getDetails().getId()) {
                    playersItems.setQuantity(playersItems.getQuantity() + 1);
                    playerHasItem = true;
                }
            }
            if (!playerHasItem) {
                player.getInventory().add(inventoryItemToAdd);
            }

            System.out.println("You bought a " + inventoryItemToAdd.getDetails().getName() + "!");
            player.setGold(player.getGold() - vendorItemToBuy.getSellPrice());

            System.out.println("Would you like to keep shopping?: Y/N");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("Y")) {
                continue;
            }
            break;
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<VendorItem> getVendorsInventory() {
        return vendorsInventory;
    }

    public void setVendorsInventory(List<VendorItem> vendorsInventory) {
        this.vendorsInventory = vendorsInventory;
    }
}
