package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HealingItem extends Item {
    private int amountToHeal;

    private static Scanner scanner = new Scanner(System.in);

    public HealingItem(int id, String name, String namePlural, int amountToHeal) {
        super(id, name, namePlural);
        this.amountToHeal = amountToHeal;
    }

    public HealingItem() {

    }

    public static boolean canUseHealingItems(Player player) {
        if (player.getCurrentHp() == player.getMaxHp()) {
            System.out.println("You are already at full health");
            scanner.nextLine();
            return false;
        }

        for (InventoryItem ii : player.getInventory()) {
            if (ii.getDetails().getClass() == HealingItem.class) {
                return true;
            }
        }
        System.out.println("You do not have any Potions to use.");
        return false;
    }

    public static void useHealingItem(Player player) {
        int healingItemToUseId;
        List<HealingItem> healingItemsOnHand = new ArrayList<>();

        System.out.println("---Healing Items On Hand---");
        for (InventoryItem ii : player.getInventory()) {
            if (ii.getDetails().getClass() == HealingItem.class) {

                if (ii.getQuantity() > 1) {
                    System.out.println(ii.getDetails().getId() + ": " +
                            ii.getDetails().getNamePlural() + " " + ii.getQuantity() + " on hand. " +
                            "Heals " + ((HealingItem) ii.getDetails()).getAmountToHeal() + " Hp.");
                } else {
                    System.out.println(ii.getDetails().getId() + ": " +
                            ii.getDetails().getName() + " " + ii.getQuantity() + " on hand. " +
                            "Heals " + ((HealingItem) ii.getDetails()).getAmountToHeal() + " Hp.");
                }
                healingItemsOnHand.add((HealingItem) WorldData.getItemById(ii.getDetails().getId()));
            }
        }

        while (true) {
            boolean healingItemUsed = false;
            System.out.println("Current Hp: " + player.getCurrentHp() + "/" + player.getMaxHp());
            System.out.println("Which would you like to use?: [ID]");
            String userInput = scanner.nextLine();

            try {
                healingItemToUseId = Integer.parseInt(userInput);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number as your input.");
                continue;
            }

            for (HealingItem healingItem : healingItemsOnHand) {
                if (healingItem.getId() == healingItemToUseId) {
                    System.out.println(player.getName() + " used a " + healingItem.getName());

                    int amountHealed = healingItem.getAmountToHeal();
                    player.setCurrentHp(player.getCurrentHp() + healingItem.getAmountToHeal());

                    if (player.getCurrentHp() > player.getMaxHp()) {
                        amountHealed = (player.getMaxHp() - player.getCurrentHp()) + healingItem.getAmountToHeal();
                        player.setCurrentHp(player.getMaxHp());
                    }

                    System.out.println(player.getName() + " healed for " + amountHealed + " hp!");
                    healingItemUsed = true;
                }
            }

            if (healingItemUsed) {
                HealingItem.removeHealingItemFromInventory(player, WorldData.getItemById(healingItemToUseId));
                break;
            } else {
                System.out.println("Please enter a valid input");
                continue;
            }
        }
    }

    public static List<InventoryItem> removeHealingItemFromInventory(Player player, Item itemToRemove) {
        List<InventoryItem> itemsToRemove = new ArrayList<>();

        for (InventoryItem ii : player.getInventory()) {
            if (ii.getDetails().getId() == itemToRemove.getId()) {
                if (ii.getQuantity() >= 2) {
                    ii.setQuantity(ii.getQuantity() - 1);
                } else {
                    itemsToRemove.add(ii);
                }
            }
        }

        player.getInventory().removeAll(itemsToRemove);

        return player.getInventory();
    }


    public int getAmountToHeal() {
        return amountToHeal;
    }

    public void setAmountToHeal(int amountToHeal) {
        this.amountToHeal = amountToHeal;
    }
}
