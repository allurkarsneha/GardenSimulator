package com.garden.GardenSimulator.Model;

public class Mango extends Plant {

    private static int count;

    public Mango(int row, int col) {
        // name, maxLifespan, waterRequirement, row, col, fertilizingFrequency
        super("Mango", 17, 15, row, col,4);
        count += 1;
    }

    public int getCount() {
        return count;
    }

    public void decrementCount() {
        count -= 1;
    }
}
