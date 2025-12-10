package com.garden.GardenSimulator.Model;

public class Barley extends Plant {

    private static int count;

    public Barley(int row, int col) {
        // name, maxLifespan, waterRequirement, row, col, fertilizingFrequency
        super("Barley", 16, 20, row, col, 2);
        count += 1;
    }

}
