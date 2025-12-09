package com.garden.GardenSimulator.Model;

public class Watermelon extends Plant {

    private static int count;

    public Watermelon(int row, int col) {
        // name, maxLifespan, waterRequirement, row, col, fertilizingFrequency
        super("Watermelon", 15, 18, row, col, 3);
        count += 1;
    }

    public int getCount() {
        return count;
    }


    public void decrementCount() {
        count -= 1;
    }



}