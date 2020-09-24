package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Weapon extends Item {
    private int minAttack;
    private int maxAttack;

    private static Scanner scanner = new Scanner(System.in);


    public Weapon(int id, String name, String namePlural, int minAttack, int maxAttack) {
        super(id, name, namePlural);
        this.minAttack = minAttack;
        this.maxAttack = maxAttack;
    }

    public Weapon() {

    }

    public static void weaponManagement(Player player) {
        int weaponToEquipId;
        List<Weapon> weaponsOnHand = new ArrayList<>();

        while (true) {
            System.out.println("Currently Equipped: " + player.getEquippedWeapon().getId() + ": " + player.getEquippedWeapon().getName());

            for (InventoryItem ii : player.getInventory()) {
                if (ii.getDetails().getClass() == Weapon.class) {
                    System.out.println(ii.getDetails().getId() + ": " + ii.getDetails().getName() +
                            ". Damage Range: " + ((Weapon) ii.getDetails()).getMinAttack() + "-" + ((Weapon) ii.getDetails()).getMaxAttack());

                    weaponsOnHand.add((Weapon) ii.getDetails());
                }
            }

            System.out.println("Which weapon would you like to equip: [ID] - or - B to go back.");
            String userInput = scanner.nextLine();

            if (userInput.toUpperCase().equals("B")) {
                break;
            }

            try {
                weaponToEquipId = Integer.parseInt(userInput);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number(or B) as your input.");
                continue;
            }

            if (!weaponsOnHand.contains(WorldData.getItemById(weaponToEquipId))) {
                System.out.println("Please enter a valid input.");
                scanner.nextLine();
                continue;
            }

            if (player.getEquippedWeapon().getId() == weaponToEquipId) {
                System.out.println("You already have the " + player.getEquippedWeapon().getName() + " equipped.");
                scanner.nextLine();
                continue;
            }

            for (Weapon weapon : weaponsOnHand) {
                if (weapon.getId() == weaponToEquipId) {
                    System.out.println(player.getName() + " equips the " + WorldData.getItemById(weaponToEquipId).getName());
                    player.setEquippedWeapon((Weapon) WorldData.getItemById(weaponToEquipId));
                }
            }
            break;
        }
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
}
