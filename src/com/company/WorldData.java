package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WorldData {
    public static Scanner scanner = new Scanner(System.in);

    public static List<Item> items = new ArrayList<>();
    public static List<Enemy> enemies = new ArrayList<>();
    public static List<Location> locations = new ArrayList<>();
    public static List<Quest> quests = new ArrayList<>();
    public static Vendor townVendor = new Vendor();

    WorldData() {
        populateItems();
        populateEnemies();
        //TODO RE ORDER BELOW
        populateQuest();
        populateLocations();
    }

    public static Player createNewPlayer() {
        String newPlayerName;

        while (true) {
            System.out.println("Welcome, Please enter you name to begin your journey: ");
            newPlayerName = scanner.nextLine();

            if(newPlayerName.isEmpty()){
                System.out.println("Please enter your name...");
                scanner.nextLine();
                continue;
            }
            break;
        }

        Player newPlayer = new Player(
                newPlayerName, 10, 10, 5, 0, 1,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                (Weapon) getItemById(10), getLocationById(1)
        );

        newPlayer.getInventory().add(new InventoryItem(getItemById(10), 1));
        newPlayer.getInventory().add(new InventoryItem(getItemById(20), 2));

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Player");
            doc.appendChild(rootElement);

            Element name = doc.createElement("name");
            name.appendChild(doc.createTextNode(newPlayerName));
            rootElement.appendChild(name);

            Element currentHp = doc.createElement("currentHp");
            currentHp.appendChild(doc.createTextNode(Integer.toString(newPlayer.getCurrentHp())));
            rootElement.appendChild(currentHp);

            Element maxHp = doc.createElement("maxHp");
            maxHp.appendChild(doc.createTextNode(Integer.toString(newPlayer.getMaxHp())));
            rootElement.appendChild(maxHp);

            Element gold = doc.createElement("gold");
            gold.appendChild(doc.createTextNode(Integer.toString(newPlayer.getGold())));
            rootElement.appendChild(gold);

            Element exp = doc.createElement("exp");
            exp.appendChild(doc.createTextNode(Integer.toString(newPlayer.getExp())));
            rootElement.appendChild(exp);

            Element level = doc.createElement("level");
            level.appendChild(doc.createTextNode(Integer.toString(newPlayer.getLevel())));
            rootElement.appendChild(level);

            Element subElement = doc.createElement("Inventory");
            rootElement.appendChild(subElement);

            for (InventoryItem ii : newPlayer.getInventory()) {
                Element itemElement = doc.createElement("Item");
                subElement.appendChild(itemElement);

                Element itemId = doc.createElement("itemId");
                itemId.appendChild(doc.createTextNode(Integer.toString(ii.getDetails().getId())));
                itemElement.appendChild(itemId);

                Element quantity = doc.createElement("quantity");
                quantity.appendChild(doc.createTextNode(Integer.toString(ii.getQuantity())));
                itemElement.appendChild(quantity);
            }

            Element keyItemSubElement = doc.createElement("KeyItems");
            rootElement.appendChild(keyItemSubElement);

            for (Item keyItem : newPlayer.getKeyItems()) {
                Element keyItemElement = doc.createElement("KeyItem");
                keyItemSubElement.appendChild(keyItemElement);

                Element itemId = doc.createElement("itemId");
                itemId.appendChild(doc.createTextNode(Integer.toString(keyItem.getId())));
                keyItemElement.appendChild(itemId);
            }

            Element questsSubElement = doc.createElement("Quests");
            rootElement.appendChild(questsSubElement);

            for (PlayerQuest questLog : newPlayer.getQuestLog()) {
                Element questElement = doc.createElement("Quest");
                questsSubElement.appendChild(questElement);

                Element questId = doc.createElement("questId");
                questId.appendChild(doc.createTextNode(Integer.toString(questLog.getDetails().getId())));
                questElement.appendChild(questId);

                Element questIsComplete = doc.createElement("isComplete");
                questIsComplete.appendChild(doc.createTextNode(Boolean.toString(questLog.isQuestComplete())));
                questElement.appendChild(questIsComplete);
            }

            Element equippedWeapon = doc.createElement("weaponId");
            equippedWeapon.appendChild(doc.createTextNode(Integer.toString(newPlayer.getEquippedWeapon().getId())));
            rootElement.appendChild(equippedWeapon);

            Element currentLocation = doc.createElement("currentLocation");
            currentLocation.appendChild(doc.createTextNode(Integer.toString(newPlayer.getCurrentLocation().getId())));
            rootElement.appendChild(currentLocation);


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("XmlFiles/Player.xml"));

            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

        return newPlayer;

    }

    public static Player loadPlayerData() {
        Player loadedPlayer = new Player();

        String playerFilePath = "XmlFiles/Player.xml";
        File playerXmlFile = new File(playerFilePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;

        try {
            documentBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = (org.w3c.dom.Document) documentBuilder.parse(playerXmlFile);
            ((org.w3c.dom.Document) doc).getDocumentElement().normalize();
            NodeList nodeList = ((org.w3c.dom.Document) doc).getElementsByTagName("Player");

            loadedPlayer = getPlayerSaveData(nodeList.item(0));

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return loadedPlayer;
    }

    private static Player getPlayerSaveData(Node node) {
        Player loadedPlayer = new Player();
        loadedPlayer.setInventory(new ArrayList<>());
        loadedPlayer.setKeyItems(new ArrayList<>());
        loadedPlayer.setQuestLog(new ArrayList<>());

        if (node.getNodeType() == node.ELEMENT_NODE) {
            Element element = (Element) node;

            loadedPlayer.setName(WorldData.getTagValue("name", element));
            loadedPlayer.setCurrentHp(Integer.parseInt(WorldData.getTagValue("currentHp", element)));
            loadedPlayer.setMaxHp(Integer.parseInt(WorldData.getTagValue("maxHp", element)));
            loadedPlayer.setGold(Integer.parseInt(WorldData.getTagValue("gold", element)));
            loadedPlayer.setExp(Integer.parseInt(WorldData.getTagValue("exp", element)));
            loadedPlayer.setLevel(Integer.parseInt(WorldData.getTagValue("level", element)));

            NodeList itemNodeList = element.getElementsByTagName("Item");
            for (int i = 0; i < itemNodeList.getLength(); i++) {
                loadedPlayer.getInventory().add(getPlayerSavedInventory(itemNodeList.item(i)));
            }

            NodeList keyItemNodeList = element.getElementsByTagName("KeyItem");
            for (int i = 0; i < keyItemNodeList.getLength(); i++) {
                loadedPlayer.getKeyItems().add(getPlayerSavedKeyItems(keyItemNodeList.item(i)));
            }

            NodeList questNodeList = element.getElementsByTagName("Quest");
            for (int i = 0; i < questNodeList.getLength(); i++) {
                loadedPlayer.getQuestLog().add(getPlayerSavedQuest(questNodeList.item(i)));
            }

            loadedPlayer.setEquippedWeapon((Weapon) WorldData.getItemById(Integer.parseInt(WorldData.getTagValue("weaponId", element))));
            loadedPlayer.setCurrentLocation(WorldData.getLocationById(Integer.parseInt(WorldData.getTagValue("currentLocation", element))));
        }

        return loadedPlayer;
    }

    private static InventoryItem getPlayerSavedInventory(Node node) {
        InventoryItem inventoryItem = new InventoryItem();

        if (node.getNodeType() == node.ELEMENT_NODE) {
            Element element = (Element) node;

            inventoryItem.setDetails(WorldData.getItemById(Integer.parseInt(WorldData.getTagValue("itemId", element))));
            inventoryItem.setQuantity(Integer.parseInt(WorldData.getTagValue("quantity", element)));
        }

        return inventoryItem;
    }

    private static Item getPlayerSavedKeyItems(Node node) {
        Item item = new Item();

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;

            item = WorldData.getItemById(Integer.parseInt(WorldData.getTagValue("itemId", element)));
        }

        return item;
    }

    private static PlayerQuest getPlayerSavedQuest(Node node) {
        PlayerQuest questLog = new PlayerQuest();

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;

            questLog.setDetails(WorldData.getQuestById(Integer.parseInt(WorldData.getTagValue("questId", element))));
            questLog.setQuestComplete(Boolean.parseBoolean(WorldData.getTagValue("isComplete", element)));
        }

        return questLog;
    }

    public static void savePlayerData(Player player) {
        InventoryItem.sortInventoryById(player.getInventory());

        try {
            String filePath = "XmlFiles/Player.xml";
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(filePath);

            Node playerNode = doc.getFirstChild();

            Node playerName = doc.getElementsByTagName("name").item(0);
            playerName.setTextContent(player.getName());

            Node playerCurrentHp = doc.getElementsByTagName("currentHp").item(0);
            playerCurrentHp.setTextContent(Integer.toString(player.getCurrentHp()));

            Node playerMaxHp = doc.getElementsByTagName("maxHp").item(0);
            playerMaxHp.setTextContent(Integer.toString(player.getMaxHp()));

            Node playerGold = doc.getElementsByTagName("gold").item(0);
            playerGold.setTextContent(Integer.toString(player.getGold()));

            Node playerExp = doc.getElementsByTagName("exp").item(0);
            playerExp.setTextContent(Integer.toString(player.getExp()));

            Node playerLevel = doc.getElementsByTagName("level").item(0);
            playerLevel.setTextContent(Integer.toString(player.getLevel()));

            ClearXmlNodeListForRewrite(doc, "Inventory");

            //Rebuild inventory save data
            Element inventory = doc.createElement("Inventory");
            playerNode.appendChild(inventory);

            for (InventoryItem ii : player.getInventory()) {
                Element item = doc.createElement("Item");
                inventory.appendChild(item);

                Element itemId = doc.createElement("itemId");
                itemId.appendChild(doc.createTextNode(Integer.toString(ii.getDetails().getId())));
                item.appendChild(itemId);

                Element quantity = doc.createElement("quantity");
                quantity.appendChild(doc.createTextNode(Integer.toString(ii.getQuantity())));
                item.appendChild(quantity);
            }

            ClearXmlNodeListForRewrite(doc, "KeyItems");

            Element keyItems = doc.createElement("KeyItems");
            playerNode.appendChild(keyItems);

            for (Item i : player.getKeyItems()) {
                Element keyItem = doc.createElement("KeyItem");
                keyItems.appendChild(keyItem);

                Element itemId = doc.createElement("itemId");
                itemId.appendChild(doc.createTextNode(Integer.toString(i.getId())));
                keyItem.appendChild(itemId);
            }

            //Save Quest Log
            ClearXmlNodeListForRewrite(doc, "Quests");

            Element quests = doc.createElement("Quests");
            playerNode.appendChild(quests);

            for (PlayerQuest questLog : player.getQuestLog()) {
                Element quest = doc.createElement("Quest");
                quests.appendChild(quest);

                Element questId = doc.createElement("questId");
                questId.appendChild(doc.createTextNode(Integer.toString(questLog.getDetails().getId())));
                quest.appendChild(questId);

                Element isComplete = doc.createElement("isComplete");
                isComplete.appendChild(doc.createTextNode(Boolean.toString(questLog.isQuestComplete())));
                quest.appendChild(isComplete);
            }

            Node equippedWeapon = doc.getElementsByTagName("weaponId").item(0);
            equippedWeapon.setTextContent(Integer.toString(player.getEquippedWeapon().getId()));

            Node currentLocation = doc.getElementsByTagName("currentLocation").item(0);
            currentLocation.setTextContent(Integer.toString(player.getCurrentLocation().getId()));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);

            saveVendorData(townVendor);

            System.out.println("Player Data Saved!");
            scanner.nextLine();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private static void ClearXmlNodeListForRewrite(Document doc, String nodeToDeleteName) {
        Node playerNode = doc.getFirstChild();

        NodeList nodeList = playerNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (nodeToDeleteName.equals(node.getNodeName())) {
                playerNode.removeChild(node);
            }
        }
    }

    private void populateItems() {
        String itemsFilePath = "XmlFiles/Item.xml";
        File itemsXmlFile = new File(itemsFilePath);

        String weaponsFilePath = "XmlFiles/Weapon.xml";
        File weaponsXmlFile = new File(weaponsFilePath);

        String healingItemsFilePath = "XmlFiles/HealingItem.xml";
        File healingItemFile = new File(healingItemsFilePath);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;

        try {
            documentBuilder = dbFactory.newDocumentBuilder();

            Document itemDoc = documentBuilder.parse(itemsXmlFile);
            itemDoc.getDocumentElement().normalize();
            NodeList itemNodeList = itemDoc.getElementsByTagName("Item");

            for (int i = 0; i < itemNodeList.getLength(); i++) {
                items.add(getItem(itemNodeList.item(i)));
            }

            Document weaponsDoc = documentBuilder.parse(weaponsXmlFile);
            weaponsDoc.getDocumentElement().normalize();
            NodeList weaponNodeList = weaponsDoc.getElementsByTagName("Weapon");

            for (int i = 0; i < weaponNodeList.getLength(); i++) {
                items.add(getWeapon(weaponNodeList.item(i)));
            }

            Document healingItemDoc = documentBuilder.parse(healingItemFile);
            healingItemDoc.getDocumentElement().normalize();
            NodeList healingItemNodeList = healingItemDoc.getElementsByTagName("HealingItem");

            for (int i = 0; i < healingItemNodeList.getLength(); i++) {
                items.add(getHealingItem(healingItemNodeList.item(i)));
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    private Item getItem(Node node) {
        Item item = new Item();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            item.setId(Integer.parseInt(getTagValue("id", element)));
            item.setName(getTagValue("name", element));
            item.setNamePlural(getTagValue("namePlural", element));
        }

        return item;
    }

    private Weapon getWeapon(Node node) {
        Weapon weapon = new Weapon();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            weapon.setId(Integer.parseInt(getTagValue("id", element)));
            weapon.setName(getTagValue("name", element));
            weapon.setNamePlural(getTagValue("namePlural", element));
            weapon.setMinAttack(Integer.parseInt(getTagValue("minAttack", element)));
            weapon.setMaxAttack(Integer.parseInt(getTagValue("maxAttack", element)));
        }

        return weapon;
    }

    private HealingItem getHealingItem(Node node) {
        HealingItem healingItem = new HealingItem();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;

            healingItem.setId(Integer.parseInt(getTagValue("id", element)));
            healingItem.setName(getTagValue("name", element));
            healingItem.setNamePlural(getTagValue("namePlural", element));
            healingItem.setAmountToHeal(Integer.parseInt(getTagValue("amountToHeal", element)));
        }

        return healingItem;
    }


    private void populateEnemies() {
        String filePath = "XmlFiles/Enemy.xml";
        File enemyXmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;

        try {
            documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(enemyXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("Enemy");

            for (int i = 0; i < nodeList.getLength(); i++) {
                enemies.add(getEnemy(nodeList.item(i)));
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    private Enemy getEnemy(Node node) {
        Enemy enemy = new Enemy();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            enemy.setId(Integer.parseInt(getTagValue("id", element)));
            enemy.setName(getTagValue("name", element));
            enemy.setCurrentHp(Integer.parseInt(getTagValue("currentHp", element)));
            enemy.setMaxHp(Integer.parseInt(getTagValue("maxHp", element)));
            enemy.setMinRewardGold(Integer.parseInt(getTagValue("minRewardGold", element)));
            enemy.setMaxRewardGold(Integer.parseInt(getTagValue("maxRewardGold", element)));
            enemy.setMinRewardExp(Integer.parseInt(getTagValue("minRewardExp", element)));
            enemy.setMaxRewardExp(Integer.parseInt(getTagValue("maxRewardExp", element)));
            enemy.setMinAttack(Integer.parseInt(getTagValue("minAttack", element)));
            enemy.setMaxAttack(Integer.parseInt(getTagValue("maxAttack", element)));

            enemy.lootTable = new ArrayList<>();

            NodeList nodeList = ((Element) node).getElementsByTagName("Item");

            for (int i = 0; i < nodeList.getLength(); i++) {
                enemy.lootTable.add(getLootItem(nodeList.item(i)));
            }
        }
        return enemy;
    }

    private LootItem getLootItem(Node node) {
        LootItem lootItem = new LootItem();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            lootItem.setDetails(getItemById(Integer.parseInt(getTagValue("itemId", element))));
            lootItem.setMinQuantity(Integer.parseInt(getTagValue("minQuantity", element)));
            lootItem.setMaxQuantity(Integer.parseInt(getTagValue("maxQuantity", element)));
            lootItem.setDropRate(Integer.parseInt(getTagValue("dropRate", element)));
            lootItem.setKeyItem(Boolean.parseBoolean(getTagValue("isKeyItem", element)));
        }
        return lootItem;
    }

    private void populateLocations() {
        String filePath = "XmlFiles/Location.xml";
        File locationXmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;

        try {
            documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(locationXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("Location");

            for (int i = 0; i < nodeList.getLength(); i++) {
                locations.add(getLocation(nodeList.item(i)));
            }

            int i = 0;
            for (Location location : locations) {
                getConnectedLocations(location, nodeList.item(i));
                i++;
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    private Location getLocation(Node node) {
        Location location = new Location();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            location.setId(Integer.parseInt(getTagValue("id", element)));
            location.setName(getTagValue("name", element));
            location.setDescription(getTagValue("description", element));
            location.setEnemyLivingHere(getEnemyById(Integer.parseInt(getTagValue("enemyLivingHere", element))));
            location.setQuestHere((getQuestById(Integer.parseInt(getTagValue("questHere", element)))));
            location.setItemNeededToEnter(getItemById(Integer.parseInt(getTagValue("itemNeededToEnter", element))));
            location.setVendorHere((Boolean.parseBoolean(getTagValue("vendorHere", element))));
        }

        return location;
    }

    private void getConnectedLocations(Location location, Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;

            location.setLocationToNorth(getLocationById(Integer.parseInt(getTagValue("locationToNorth", element))));
            location.setLocationToEast(getLocationById(Integer.parseInt(getTagValue("locationToEast", element))));
            location.setLocationToSouth(getLocationById(Integer.parseInt(getTagValue("locationToSouth", element))));
            location.setLocationToWest(getLocationById(Integer.parseInt(getTagValue("locationToWest", element))));
        }
    }

    private void populateQuest() {
        String filePath = "XmlFiles/Quest.xml";
        File questXmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;

        try {
            documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(questXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("Quest");

            for (int i = 0; i < nodeList.getLength(); i++) {
                quests.add(getQuest(nodeList.item(i)));
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    private Quest getQuest(Node node) {
        Quest quest = new Quest();
        if (node.getNodeType() == node.ELEMENT_NODE) {
            Element element = (Element) node;

            quest.setId(Integer.parseInt(getTagValue("id", element)));
            quest.setName(getTagValue("name", element));
            quest.setDescription(getTagValue("description", element));
            quest.setRewardGold(Integer.parseInt(getTagValue("rewardGold", element)));
            quest.setRewardExp(Integer.parseInt(getTagValue("rewardExp", element)));

            quest.setQuestCompleteItems(new ArrayList<>());

            NodeList completeItemsNodeList = ((Element) node).getElementsByTagName("questCompleteItem");
            for (int i = 0; i < completeItemsNodeList.getLength(); i++) {
                quest.getQuestCompleteItems().add(getQuestItems(completeItemsNodeList.item(i)));
            }

            quest.setRewardItems(new ArrayList<>());

            NodeList rewardItemsNodeList = ((Element) node).getElementsByTagName("questRewardItem");
            for (int i = 0; i < rewardItemsNodeList.getLength(); i++) {
                quest.getRewardItems().add(getQuestItems(rewardItemsNodeList.item(i)));
            }
        }

        return quest;
    }

    private InventoryItem getQuestItems(Node node) {
        InventoryItem questItem = new InventoryItem();

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            questItem.setDetails(getItemById(Integer.parseInt(getTagValue("itemId", element))));
            questItem.setQuantity(Integer.parseInt(getTagValue("quantity", element)));
        }
        return questItem;
    }

    public static Vendor createDefaultVendor() {
        Vendor defaultVendor = new Vendor(
                "Traveling Vendor",
                new ArrayList<>());

        defaultVendor.getVendorsInventory().add(new VendorItem(getItemById(20), 15, 10));
        defaultVendor.getVendorsInventory().add(new VendorItem(getItemById(21), 7, 25));
        defaultVendor.getVendorsInventory().add(new VendorItem(getItemById(13), 1, 150));
        defaultVendor.getVendorsInventory().add(new VendorItem(getItemById(23), 2, 175));

        saveVendorData(defaultVendor);

        return defaultVendor;
    }

    public static Vendor populateVendor() {
        Vendor loadedVendor = new Vendor();

        String filePath = "XmlFiles/Vendor.xml";
        File vendorXmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;

        try {
            documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(vendorXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("Vendor");

            loadedVendor = getVendor(nodeList.item(0));

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return loadedVendor;
    }

    private static Vendor getVendor(Node node) {
        Vendor vendor = new Vendor();

        vendor.setVendorsInventory(new ArrayList<>());
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;

            vendor.setName(getTagValue("name", element));

            NodeList nodeList = ((Element) node).getElementsByTagName("VendorItem");
            for (int i = 0; i < nodeList.getLength(); i++) {
                vendor.getVendorsInventory().add(getVendorItems(nodeList.item(i)));
            }
        }

        return vendor;
    }

    private static VendorItem getVendorItems(Node node) {
        VendorItem vendorItem = new VendorItem();

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            vendorItem.setDetails(getItemById(Integer.parseInt(getTagValue("itemId", element))));
            vendorItem.setQuantityOnHand(Integer.parseInt(getTagValue("quantityOnHand", element)));
            vendorItem.setSellPrice(Integer.parseInt(getTagValue("sellPrice", element)));
        }
        return vendorItem;
    }

    public static void saveVendorData(Vendor vendor) {
        try {
            String filePath = "XmlFiles/Vendor.xml";
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document vendorXmlFile = dBuilder.parse(filePath);

            Node vendorNode = vendorXmlFile.getFirstChild();

            Node vendorName = vendorXmlFile.getElementsByTagName("name").item(0);
            vendorName.setTextContent(vendor.getName());

            ClearXmlNodeListForRewrite(vendorXmlFile, "VendorItems");

            Element vendorInventory = vendorXmlFile.createElement("VendorItems");
            vendorNode.appendChild(vendorInventory);

            for (VendorItem vendorItem : vendor.getVendorsInventory()) {
                Element vendorItemElement = vendorXmlFile.createElement("VendorItem");
                vendorInventory.appendChild(vendorItemElement);

                Element itemId = vendorXmlFile.createElement("itemId");
                itemId.appendChild(vendorXmlFile.createTextNode(Integer.toString(vendorItem.getDetails().getId())));
                vendorItemElement.appendChild(itemId);

                Element quantityOnHand = vendorXmlFile.createElement("quantityOnHand");
                quantityOnHand.appendChild(vendorXmlFile.createTextNode(Integer.toString(vendorItem.getQuantityOnHand())));
                vendorItemElement.appendChild(quantityOnHand);

                Element sellPrice = vendorXmlFile.createElement("sellPrice");
                sellPrice.appendChild(vendorXmlFile.createTextNode(Integer.toString(vendorItem.getSellPrice())));
                vendorItemElement.appendChild(sellPrice);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(vendorXmlFile);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public static Item getItemById(int id) {
        for (Item item : items) {
            if (item.getId() == id) {
                return item;
            }
        }

        return null;
    }

    public static Enemy getEnemyById(int id) {
        for (Enemy enemy : enemies) {
            if (enemy.getId() == id) {
                return enemy;
            }
        }

        return null;
    }

    public static Location getLocationById(int id) {
        for (Location location : locations) {
            if (location.getId() == id) {
                return location;
            }
        }

        return null;
    }

    public static Quest getQuestById(int id) {
        for (Quest quest : quests) {
            if (quest.getId() == id) {
                return quest;
            }
        }

        return null;
    }


    public static String getTagValue(String tag, org.w3c.dom.Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }
}