package com.company;

import java.util.Scanner;

public class Location {
    private int id;
    private String name;
    private String description;

    private Location locationToNorth;
    private Location locationToEast;
    private Location locationToSouth;
    private Location locationToWest;

    private Enemy enemyLivingHere;
    private Quest questHere;
    private Item itemNeededToEnter;
    private boolean vendorHere;

    private static Scanner scanner = new Scanner(System.in);

    public Location(int id, String name, String description, Location locationToNorth,
                    Location locationToEast, Location locationToSouth, Location locationToWest, Enemy enemyLivingHere, Quest questHere, Item itemNeededToEnter,
                    boolean vendorHere) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.locationToNorth = locationToNorth;
        this.locationToEast = locationToEast;
        this.locationToSouth = locationToSouth;
        this.locationToWest = locationToWest;
        this.enemyLivingHere = enemyLivingHere;
        this.questHere = questHere;
        this.itemNeededToEnter = itemNeededToEnter;
        this.vendorHere = vendorHere;
    }

    public Location() {

    }

    public static boolean hasItemRequiredCheck(Player player, Location locationToCheck) {
        //Check if there is no required item
        if (locationToCheck.getItemNeededToEnter() == null) {
            return true;
        }

        System.out.println("You need the " + locationToCheck.getItemNeededToEnter().getName() + " to enter. Turning back");
        return false;
    }

    public static void newLocationCheck(Player player, Location newLocation) {
        System.out.println("You travel to the " + newLocation.getName() + " and take a look around.");
        System.out.println(newLocation.getDescription());
        System.out.println();

        if (newLocation.getEnemyLivingHere() != null) {
            Enemy enemy = newLocation.getEnemyLivingHere();
            System.out.println("A " + enemy.getName() + " jumps out and attacks you! Entering Battle...");
            Player.Battle(player, enemy);
        }

        boolean playerHasQuest = false;
        boolean questIsComplete = false;
        if (newLocation.getQuestHere() != null) {
            for (PlayerQuest questLog : player.getQuestLog()) {
                if (questLog.getDetails().getId() == newLocation.getQuestHere().getId()) {
                    playerHasQuest = true;
                    questIsComplete = questLog.isQuestComplete();
                }
            }

            if (!questIsComplete) {
                if (!playerHasQuest) {
                    //Add Quest to player Quest Log
                    System.out.println("You have been given a quest!");
                    System.out.println(newLocation.getQuestHere().getName() + ": " +
                            newLocation.getQuestHere().getDescription());
                    System.out.println("Check the Quest Log for more info...");
                    scanner.nextLine();

                    player.getQuestLog().add(new PlayerQuest(WorldData.getQuestById(newLocation.getQuestHere().getId()), false));
                }

                Quest.checkQuestCompletion(player, newLocation.getQuestHere());
            }
        }

        if (newLocation.getVendorHere()) {
            while (true) {
                System.out.println("Would you like to visit the Shop?: Y/N");
                String input = scanner.nextLine();
                input = input.toUpperCase();

                if (!input.equals("Y") && !input.equals("N")) {
                    System.out.println("Please enter a valid input");
                    scanner.nextLine();
                    continue;
                }

                switch (input.toUpperCase()) {
                    case "Y":
                        Vendor.visitVendor(player, WorldData.townVendor);
                        break;

                    case "N":
                        break;
                }
                break;
            }
        }
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

    public Location getLocationToNorth() {
        return locationToNorth;
    }

    public void setLocationToNorth(Location locationToNorth) {
        this.locationToNorth = locationToNorth;
    }

    public Location getLocationToEast() {
        return locationToEast;
    }

    public void setLocationToEast(Location locationToEast) {
        this.locationToEast = locationToEast;
    }

    public Location getLocationToSouth() {
        return locationToSouth;
    }

    public void setLocationToSouth(Location locationToSouth) {
        this.locationToSouth = locationToSouth;
    }

    public Location getLocationToWest() {
        return locationToWest;
    }

    public void setLocationToWest(Location locationToWest) {
        this.locationToWest = locationToWest;
    }

    public Enemy getEnemyLivingHere() {
        return enemyLivingHere;
    }

    public void setEnemyLivingHere(Enemy enemyLivingHere) {
        this.enemyLivingHere = enemyLivingHere;
    }

    public Quest getQuestHere() {
        return questHere;
    }

    public void setQuestHere(Quest questHere) {
        this.questHere = questHere;
    }

    public Item getItemNeededToEnter() {
        return itemNeededToEnter;
    }

    public void setItemNeededToEnter(Item itemNeededToEnter) {
        this.itemNeededToEnter = itemNeededToEnter;
    }

    public boolean getVendorHere() {
        return vendorHere;
    }

    public void setVendorHere(boolean vendorHere) {
        this.vendorHere = vendorHere;
    }
}
