package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Quest {
    private int id;
    private String name;
    private String description;

    private int rewardGold;
    private int rewardExp;
    private List<InventoryItem> questCompleteItems;
    private List<InventoryItem> rewardItems;

    private static Scanner scanner = new Scanner(System.in);

    public Quest() {

    }

    public Quest(int id, String name, String description, int rewardGold, int rewardExp, List<InventoryItem> questCompleteItems, List<InventoryItem> rewardItems) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rewardGold = rewardGold;
        this.rewardExp = rewardExp;
        this.questCompleteItems = questCompleteItems;
        this.rewardItems = rewardItems;
    }

    public static void questManagement(Player player) {
        System.out.println("--- " + player.getName() + "'s Quests---");

        System.out.println("----------------------------------------------------------");
        for (PlayerQuest questLog : player.getQuestLog()) {
            if (questLog.isQuestComplete()) {
                System.out.println("QUEST COMPLETED!");
            }

            System.out.println(questLog.getDetails().getName() + ": ");
            System.out.println(questLog.getDetails().getDescription());

            System.out.println();

            System.out.println("Items Needed To Complete: ");
            for (InventoryItem completionItems : questLog.getDetails().getQuestCompleteItems()) {
                System.out.println(completionItems.getDetails().getName() + ": Needed: " + completionItems.getQuantity() +
                        " On Hand: " + getOnHandItemsCount(player, completionItems));
            }

            if (questLog.getDetails().getId() != 1) {
                System.out.println();
                System.out.println("Rewards: ");
                for (InventoryItem rewardItems : questLog.getDetails().getRewardItems()) {
                    System.out.println(rewardItems.getQuantity() + " " + rewardItems.getDetails().getName());
                }
                System.out.println(questLog.getDetails().getRewardExp() + " reward Exp. -and- " +
                        questLog.getDetails().getRewardGold() + " reward Gold.");
            }
            System.out.println("----------------------------------------------------------");
        }

        System.out.println("Press Enter to Exit to Main Hub...");
        scanner.nextLine();
    }

    public static void checkQuestCompletion(Player player, Quest questToComplete) {
        List<InventoryItem> questItemsOnHand = new ArrayList<>();
        List<InventoryItem> itemsToRemove = new ArrayList<>();


        //Get Quest Items from player inventory
        for (InventoryItem questCompleteItem : questToComplete.getQuestCompleteItems()) {
            for (InventoryItem itemOnHand : player.getInventory()) {
                if (questCompleteItem.getDetails().getId() == itemOnHand.getDetails().getId()) {
                    questItemsOnHand.add(itemOnHand);
                }
            }
        }

        if (questItemsOnHand == null) {
            return;
        }

        for (InventoryItem ii : questItemsOnHand) {
            for (InventoryItem questItem : questToComplete.getQuestCompleteItems()) {
                if (questItem.getDetails().getId() == ii.getDetails().getId()) {
                    if (ii.getQuantity() >= questItem.getQuantity()) {
                        itemsToRemove.add(questItem);
                    }
                }
            }
        }

        if (itemsToRemove.equals(questToComplete.getQuestCompleteItems())) {
            while (true) {
                System.out.println("You can complete the " + questToComplete.getName() + " quest.");
                System.out.println("Would you like to?: Y/N");
                String input = scanner.nextLine();
                input = input.toUpperCase();

                if (!input.equalsIgnoreCase("Y") && !input.equalsIgnoreCase("N")) {
                    System.out.println("Please enter a valid input");
                    scanner.nextLine();
                    continue;
                }

                switch (input) {
                    case "N":
                        break;

                    case "Y":
                        completeQuest(player, questToComplete);
                        break;
                }

                break;
            }
        }
    }

    public static void completeQuest(Player player, Quest questToComplete) {
        System.out.println("You have completed the " + questToComplete.getName() + " quest.");

        System.out.println("The following has been removed from your inventory: ");
        for (InventoryItem questCompleteItem : questToComplete.getQuestCompleteItems()) {
            System.out.println(questCompleteItem.getQuantity() + " " + questCompleteItem.getDetails().getName());
            removeQuestItems(player, questCompleteItem);
        }

        System.out.println("You receive the following rewards!");

        System.out.println("Reward Gold: " + questToComplete.getRewardGold());
        player.setGold(player.getGold() + questToComplete.getRewardGold());

        System.out.println("Reward Exp: " + questToComplete.getRewardExp());
        player.setExp(player.getExp() + questToComplete.getRewardExp());
        player.checkForLevelUp();

        for (InventoryItem rewardItem : questToComplete.getRewardItems()) {
            System.out.println(rewardItem.getQuantity() + " " + rewardItem.getDetails().getName());

            addQuestRewardItem(player, rewardItem);
        }

        //set quest to complete
        for (PlayerQuest questLog : player.getQuestLog()) {
            if (questLog.getDetails().getId() == questToComplete.getId()) {
                questLog.setQuestComplete(true);
            }
        }

        scanner.nextLine();
    }

    public static List<InventoryItem> removeQuestItems(Player player, InventoryItem questItemToRemove) {
        List<InventoryItem> itemsToRemove = new ArrayList<>();

        for (InventoryItem itemOnHand : player.getInventory()) {
            if (itemOnHand.getDetails().getId() == questItemToRemove.getDetails().getId()) {
                if (itemOnHand.getQuantity() > questItemToRemove.getQuantity()) {
                    itemOnHand.setQuantity(itemOnHand.getQuantity() - questItemToRemove.getQuantity());
                } else {
                    itemsToRemove.add(itemOnHand);
                }
            }
        }

        player.getInventory().removeAll(itemsToRemove);


        return player.getInventory();
    }

    public static List<InventoryItem> addQuestRewardItem(Player player, InventoryItem rewardItem) {
        boolean canAddItem = false;

        for (InventoryItem itemOnHand : player.getInventory()) {
            if (itemOnHand.getDetails().getId() == rewardItem.getDetails().getId()) {
                itemOnHand.setQuantity(itemOnHand.getQuantity() + rewardItem.getQuantity());
            } else {
                canAddItem = true;
            }
        }

        if (canAddItem) {
            player.getInventory().add(rewardItem);
        }

        return player.getInventory();
    }

    private static String getOnHandItemsCount(Player player, InventoryItem completionItem) {
        for (InventoryItem inventoryItem : player.getInventory()) {
            if (inventoryItem.getDetails().getId() == completionItem.getDetails().getId()) {
                return Integer.toString(inventoryItem.getQuantity());
            }
        }

        return "0";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRewardGold() {
        return rewardGold;
    }

    public void setRewardGold(int rewardGold) {
        this.rewardGold = rewardGold;
    }

    public int getRewardExp() {
        return rewardExp;
    }

    public void setRewardExp(int rewardExp) {
        this.rewardExp = rewardExp;
    }

    public List<InventoryItem> getQuestCompleteItems() {
        return questCompleteItems;
    }

    public void setQuestCompleteItems(List<InventoryItem> questCompleteItems) {
        this.questCompleteItems = questCompleteItems;
    }

    public List<InventoryItem> getRewardItems() {
        return rewardItems;
    }

    public void setRewardItems(List<InventoryItem> rewardItems) {
        this.rewardItems = rewardItems;
    }
}
