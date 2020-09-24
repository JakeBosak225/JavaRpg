package com.company;

import java.util.*;

public class Player extends LivingCreature {
    private int gold;
    private int exp;
    private int level;

    private List<InventoryItem> inventory;
    private List<Item> keyItems;
    private List<PlayerQuest> questLog;
    private Weapon equippedWeapon;
    private Location currentLocation;

    private static Scanner scanner = new Scanner(System.in);
    private static Random rng = new Random();

    public Player(String name, int currentHp, int maxHp, int gold, int exp, int level,
                  List<InventoryItem> inventory, List<Item> keyItems, List<PlayerQuest> questLogs, Weapon equippedWeapon, Location currentLocation) {
        super(name, currentHp, maxHp);
        this.gold = gold;
        this.exp = exp;
        this.level = level;
        this.inventory = inventory;
        this.keyItems = keyItems;
        this.questLog = questLogs;
        this.equippedWeapon = equippedWeapon;
        this.currentLocation = currentLocation;
    }

    public Player() {

    }

    public static void mainHub(Player player) {
        while (true) {
            System.out.println("\f");
            System.out.printf("%n%n%n");
            System.out.println("---Main Hub---");
            System.out.println(player.getName() + ": Level: " + player.getLevel());
            System.out.println("Hp: " + player.getCurrentHp() + "/" + player.getMaxHp());
            System.out.println("Current Location: " + player.getCurrentLocation().getName());
            System.out.println("Gold: " + player.getGold());
            System.out.println("Exp: " + player.getExp());
            System.out.println("Equipped Weapon: " + player.getEquippedWeapon().getName());


            System.out.printf("%nWhat would you like to do?:%n" +
                    "[M] Move %n" +
                    "[I] Inventory %n" +
                    "[Q] Quest Log%n" +
                    "[S] Save Game %n");

            String input = scanner.nextLine();

            switch (input.toUpperCase()) {
                case "M":
                    MovePlayer(player);
                    break;

                case "I":
                    playerInventoryManagement(player);
                    break;

                case "Q":
                    Quest.questManagement(player);
                    break;

                case "S":
                    System.out.println("Saving Data...");
                    WorldData.savePlayerData(player);

                    System.out.println("Would you like to continue?: Y/N");
                    String continueInput = scanner.nextLine();

                    if (continueInput.equalsIgnoreCase("N")) {
                        System.exit(0);
                    }
                    break;

                default:
                    System.out.println("Please enter a valid input...");
                    scanner.nextLine();
                    continue;
            }
        }
    }

    public void checkForLevelUp() {
        int currentLevel = getLevel();
        setLevel(getExp() / 100 + 1);

        if (getLevel() > currentLevel) {
            System.out.println("You leveled up! Max Hp increased by +5!");
            setMaxHp(getMaxHp() + 5);
            setCurrentHp(getMaxHp());
        }
    }

    public static void playerInventoryManagement(Player player) {
        List<String> validOptionKey = new ArrayList<>();
        validOptionKey.add("W");
        validOptionKey.add("H");
        validOptionKey.add("B");

        System.out.println("\f");
        System.out.printf("%n%n%n");
        System.out.println("----" + player.getName() + "'s Inventory----");

        //Order Inventory by Items ID
        InventoryItem.sortInventoryById(player.getInventory());

        //Display ITEMS
        System.out.println("---Items---");
        for (InventoryItem ii : player.getInventory()) {
            if (ii.getDetails().getClass() == Item.class) {
                if (ii.getQuantity() > 1) {
                    System.out.println(ii.getDetails().getId() + ": " +
                            ii.getDetails().getNamePlural() + ". " + ii.getQuantity() +
                            " on hand.");
                } else {
                    System.out.println(ii.getDetails().getId() + ": " +
                            ii.getDetails().getName() + ". " + ii.getQuantity() +
                            " on hand.");
                }
            }
        }

        //Display Weapons
        System.out.println();
        System.out.println("---Weapons---");
        for (InventoryItem ii : player.getInventory()) {
            if (ii.getDetails().getClass() == Weapon.class) {

                System.out.printf(ii.getDetails().getId() + ": " +
                        ii.getDetails().getName() + ". " +
                        " Attack Range: " + ((Weapon) ii.getDetails()).getMinAttack() +
                        "-" + ((Weapon) ii.getDetails()).getMaxAttack());

                if (ii.getDetails().getId() == player.getEquippedWeapon().getId()) {
                    System.out.printf(" : Currently Equipped%n");
                } else {
                    System.out.printf("%n");
                }
            }
        }

        //Display Healing Items
        System.out.println();
        System.out.println("---Healing Items---");

        for (InventoryItem ii : player.getInventory()) {
            if (ii.getDetails().getClass() == HealingItem.class) {
                if (ii.getQuantity() > 1) {
                    System.out.println(ii.getDetails().getId() + ": " +
                            ii.getDetails().getNamePlural() + ". " + ii.getQuantity() +
                            " on hand. Heals " + ((HealingItem) ii.getDetails()).getAmountToHeal() + " Hp.");
                } else {
                    System.out.println(ii.getDetails().getId() + ": " +
                            ii.getDetails().getName() + ". " + ii.getQuantity() +
                            " on hand. Heals " + ((HealingItem) ii.getDetails()).getAmountToHeal() + " Hp.");
                }
            }
        }

        while (true) {
            System.out.printf("%nWhat would you like to do?: %n");
            System.out.printf("[W] Weapon Management%n" +
                    "[H] Heal Player%n" +
                    "[B] Return to Main Hub%n");

            String input = scanner.nextLine().toUpperCase();

            if (!validOptionKey.contains(input)) {
                System.out.println("Please Enter a valid entry.");
                scanner.nextLine();
                continue;
            }

            switch (input) {
                case "B":
                    break;

                case "W":
                    Weapon.weaponManagement(player);
                    break;

                case "H":
                    if (!HealingItem.canUseHealingItems(player)) {
                        break;
                    }
                    HealingItem.useHealingItem(player);
                    break;
            }
            break;
        }
    }


    private static void MovePlayer(Player player) {
        List<String> directionKeys = new ArrayList<>();

        while (true) {
            System.out.println("\f");
            System.out.printf("%n%n%n");
            System.out.println("Which way would you like to travel: [N,E,S,W] ");

            if (player.getCurrentLocation().getLocationToNorth() != null) {
                System.out.println("Location to North: " + player.getCurrentLocation().getLocationToNorth().getName());

                directionKeys.add("N");
            }

            if (player.getCurrentLocation().getLocationToEast() != null) {
                System.out.println("Location to East: " +
                        player.getCurrentLocation().getLocationToEast().getName());

                directionKeys.add("E");
            }

            if (player.getCurrentLocation().getLocationToSouth() != null) {
                System.out.println("Location to South: " +
                        player.getCurrentLocation().getLocationToSouth().getName());

                directionKeys.add("S");
            }

            if (player.getCurrentLocation().getLocationToWest() != null) {
                System.out.println("Location to West: " +
                        player.getCurrentLocation().getLocationToWest().getName());

                directionKeys.add("W");
            }

            System.out.println("Or B to go back.");
            directionKeys.add("B");

            String input = scanner.nextLine();

            if (!directionKeys.contains(input.toUpperCase())) {
                System.out.println("Please Enter a valid entry.");
                scanner.nextLine();
                continue;
            }

            switch (input.toUpperCase()) {
                case "B":
                    break;

                case "N":
                    if (Location.hasItemRequiredCheck(player, player.getCurrentLocation().getLocationToNorth())) {
                        System.out.println("Moving North...");
                        scanner.nextLine();
                        player.setCurrentLocation(player.getCurrentLocation().getLocationToNorth());
                        Location.newLocationCheck(player, player.getCurrentLocation());
                        break;
                    }
                    break;

                case "E":
                    if (Location.hasItemRequiredCheck(player, player.getCurrentLocation().getLocationToEast())) {
                        System.out.println("Moving East...");
                        scanner.nextLine();
                        player.setCurrentLocation(player.getCurrentLocation().getLocationToEast());
                        Location.newLocationCheck(player, player.getCurrentLocation());
                        break;
                    }
                    break;

                case "S":
                    if (Location.hasItemRequiredCheck(player, player.getCurrentLocation().getLocationToSouth())) {
                        System.out.println("Moving South...");
                        scanner.nextLine();
                        player.setCurrentLocation(player.getCurrentLocation().getLocationToSouth());
                        Location.newLocationCheck(player, player.getCurrentLocation());
                        break;
                    }
                    break;

                case "W":
                    if (Location.hasItemRequiredCheck(player, player.getCurrentLocation().getLocationToWest())) {
                        System.out.println("Moving West...");
                        scanner.nextLine();
                        player.setCurrentLocation(player.getCurrentLocation().getLocationToWest());
                        Location.newLocationCheck(player, player.getCurrentLocation());
                        break;
                    }
                    break;
            }
            break;
        }
    }

    public static void Battle(Player player, Enemy enemy) {
        enemy.setCurrentHp(enemy.getMaxHp());

        while (player.getCurrentHp() > 0 && enemy.getCurrentHp() > 0) {

            //Player Move
            while (true) {
                System.out.println("Player Hp: " + player.getCurrentHp());
                System.out.println(enemy.getName() + " Hp: " + enemy.getCurrentHp());


                System.out.printf("What would you like to do: %n" +
                        "[A]ttack%n" +
                        "[H]eal%n");
                String userInput = scanner.nextLine();
                userInput = userInput.toUpperCase();

                if (!userInput.equals("A") && !userInput.equals("H")) {
                    System.out.println("Please enter a valid input...");
                    scanner.nextLine();
                    continue;
                }

                if (userInput.equals("H")) {
                    if (!HealingItem.canUseHealingItems(player)) {
                        continue;
                    }
                }

                switch (userInput) {
                    case "A":
                        int playerAttackPower = rng.nextInt((player.getEquippedWeapon().getMaxAttack() - player.getEquippedWeapon().getMinAttack() + 1) +
                                player.getEquippedWeapon().getMinAttack());

                        if (playerAttackPower <= 0) {
                            System.out.println("Your attack missed!");
                        } else {
                            System.out.println("You hit the " + enemy.getName() + " for " + playerAttackPower + " hit points!");
                            enemy.setCurrentHp(enemy.getCurrentHp() - playerAttackPower);
                        }
                        break;

                    case "H":
                        HealingItem.useHealingItem(player);
                        break;
                }
                break;
            }

            //Check if enemy is dead
            if (enemy.getCurrentHp() <= 0) {
                break;
            }

            //Enemy Move
            int enemyAttackPower = enemy.getAttackPower();

            if (enemyAttackPower <= 0) {
                System.out.println("The enemy missed it attack!");
            } else {
                System.out.println(enemy.getName() + " hits you for " + enemyAttackPower + " hit points!");
                player.setCurrentHp(player.getCurrentHp() - enemyAttackPower);
            }
        }

        if (enemy.getCurrentHp() <= 0) {
            int rewardGold = enemy.getRewardGold();
            int rewardExp = enemy.getRewardExp();

            System.out.println("You defeated the " + enemy.getName() + "!");
            System.out.println("You receive " + rewardGold + " reward Gold and " + rewardExp + " reward Exp!");

            enemy.getLootItems(player);

            player.setGold(player.getGold() + rewardGold);
            player.setExp(player.getExp() + rewardExp);
            player.checkForLevelUp();
            scanner.nextLine();
        } else {
            System.out.println("You are Dead...");
            System.exit(0);
        }
    }


    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<InventoryItem> getInventory() {
        return inventory;
    }

    public void setInventory(List<InventoryItem> inventory) {
        this.inventory = inventory;
    }

    public List<Item> getKeyItems() {
        return keyItems;
    }

    public void setKeyItems(List<Item> keyItems) {
        this.keyItems = keyItems;
    }

    public List<PlayerQuest> getQuestLog() {
        return questLog;
    }

    public void setQuestLog(List<PlayerQuest> questLogs) {
        this.questLog = questLogs;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public void setEquippedWeapon(Weapon equippedWeapon) {
        this.equippedWeapon = equippedWeapon;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
}