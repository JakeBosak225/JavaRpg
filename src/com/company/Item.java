package com.company;

public class Item {
    private int id;
    private String name;
    private String namePlural;

    public Item(int id, String name, String namePlural) {
        this.id = id;
        this.name = name;
        this.namePlural = namePlural;
    }

    public Item() {

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

    public String getNamePlural() {
        return namePlural;
    }

    public void setNamePlural(String namePlural) {
        this.namePlural = namePlural;
    }
}

