package com.company;

import java.io.File;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        WorldData worldData = new WorldData();

        System.out.println("-----Super Java RPG-----");

        String playerXmlFilePath = "XmlFiles/Player.xml";
        File playerXmlFile = new File(playerXmlFilePath);

        while (true) {
            if (playerXmlFile.exists()) {
                System.out.println("Would you like to load your current save?: Y/N");
                String input = scanner.nextLine();

                if (!input.toUpperCase().equals("Y") && !input.toUpperCase().equals("N")) {
                    System.out.println("Please enter a valid input...");
                    scanner.nextLine();
                    continue;
                }

                switch (input.toUpperCase()) {
                    case "Y":
                        System.out.println("Loading Saved Data...");
                        Player loadedPlayer = WorldData.loadPlayerData();
                        WorldData.townVendor = WorldData.populateVendor();
                        Player.mainHub(loadedPlayer);
                        break;

                    case "N":
                        System.out.println("Are you sure? This will delete your current save data...: Y/N");
                        String deleteInput = scanner.nextLine();
                        if (!deleteInput.toUpperCase().equals("Y")) {
                            continue;
                        } else {
                            playerXmlFile.delete();
                            Player newPlayer = WorldData.createNewPlayer();
                            Location.newLocationCheck(newPlayer, newPlayer.getCurrentLocation());
                            WorldData.townVendor = WorldData.createDefaultVendor();
                            Player.mainHub(newPlayer);
                            break;
                        }
                }
            }else{
                Player newPlayer = WorldData.createNewPlayer();
                Location.newLocationCheck(newPlayer, newPlayer.getCurrentLocation());
                WorldData.townVendor = WorldData.createDefaultVendor();
                Player.mainHub(newPlayer);
            }

            break;
        }
    }
}
