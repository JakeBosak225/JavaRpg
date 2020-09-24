package com.company;

public class LivingCreature {
    private String name;
    private int currentHp;
    private int maxHp;

    public LivingCreature() {

    }

    public LivingCreature(String name, int currentHp, int maxHp) {
        this.name = name;
        this.currentHp = currentHp;
        this.maxHp = maxHp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }
}