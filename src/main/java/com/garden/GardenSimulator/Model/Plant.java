package com.garden.GardenSimulator.Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public abstract class Plant {

    private final IntegerProperty daysToLive;
    private final int maxLifespan;
    private final int waterRequirement;
    private final int row;
    private final int col;
    private int pestAttacks = 0;
    private boolean isDead = false;
    private final String name;
    private int daysSinceLastFertilized = 0;
    private int currentWater;
    private int fertilizingFrequency;


    public Plant(String name, int maxLifespan, int waterRequirement, int row, int col, int fertilizingFrequency) {
        if (maxLifespan <= 0) {
            throw new IllegalArgumentException("maxLifespan must be a positive number");
        }
        this.name = name;
        this.maxLifespan = maxLifespan;
        this.daysToLive = new SimpleIntegerProperty(maxLifespan);
        this.waterRequirement = waterRequirement;
        this.row = row;
        this.col = col;
        this.fertilizingFrequency = fertilizingFrequency;
    }




    //  Mutator Methods
    public void incrementDaysSinceLastFertilized() {
        daysSinceLastFertilized++;
    }

    public void resetDaysSinceLastFertilized() {
        daysSinceLastFertilized = 0;
    }

    public void setCurrentWater(int water) {
        this.currentWater = water;
    }

    public void decrementDaysToLive() {
        if (!isDead && daysToLive.get() > 0) {
            daysToLive.set(daysToLive.get() - 1); // Decrement the property value
        }
        if (daysToLive.get() <= 0) {
            isDead = true;
        }
    }

    public int getCol() {
        return col;
    }

    public boolean isDead() {
        return isDead;
    }
    public int getPestAttacks() {
        return pestAttacks;
    }

    public String getName() {
        return name;
    }

    public IntegerProperty getDaysToLiveProperty() {
        return daysToLive;
    }


    public int getCurrentWater() {
        return this.currentWater;
    }

    public int getWaterRequirement() {
        return waterRequirement;
    }

    public int getDaysToLive() {
        return daysToLive.get();
    }

    public int getRow() {
        return row;
    }


    public int getDaysSinceLastFertilized() {
        return daysSinceLastFertilized;
    }

    public void boostGrowth() {
        if (!isDead) {
            daysToLive.set(Math.min(daysToLive.get() + 15, maxLifespan));  // Optionally increase lifespan
        }
    }

    public void incrementPestAttacks() {
        if (isDead) {
            return;
        }
        pestAttacks++;
        if (pestAttacks == 10) { // Reduce lifespan by 1 after 10 pest attacks
            daysToLive.set(daysToLive.get() - 1);
            if (daysToLive.get() <= 0) {
                isDead = true;
            }
        }
        if (pestAttacks >= 20) {  // Plant dies after 20 pest attacks
            isDead = true;
        }
    }

    public void reducePestAttacks(int amount) {
        pestAttacks = Math.max(pestAttacks - amount, 0);  // Reduces pest attacks by the given amount
    }

    public void adjustLifespanForWeather(String weather) {
        if (!isDead) {
            if (weather.equals("Rainy")) {
                // Rainy days are beneficial, increase lifespan by 2
                daysToLive.set(Math.min(daysToLive.get() + 2, maxLifespan));
            }
            if (weather.equals("Cloudy")) {
                // Cloudy days are harmful, decrease lifespan by 1
                daysToLive.set(daysToLive.get() - 1);
                if (daysToLive.get() <= 0) {
                    isDead = true;
                }
            }
            if (weather.equals("Sunny")) {
                // Sunny days are beneficial, increase lifespan by 2
                daysToLive.set(Math.min(daysToLive.get() + 2, maxLifespan));
            }
        }
    }

    public int getFertilizingFrequency() {
        return this.fertilizingFrequency;
    }

    public void water(int amount) {
        if (!isDead) {
            currentWater += amount;
            if (currentWater >= waterRequirement) {
                daysToLive.set(Math.min(daysToLive.get() + 2, maxLifespan));// Increase lifespan by 2 days if properly watered
            } else {
                daysToLive.set(daysToLive.get() - 1);
            }
        }
    }
}
