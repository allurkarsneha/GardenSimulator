package com.garden.GardenSimulator.Model;

public class Spinach extends Plant {

    private static int count;

    public Spinach(int row, int col) {
        // name, maxLifespan, waterRequirement, row, col, fertilizingFrequency
        super("Spinach", 15, 15, row, col, 2);
        count += 1;
    }



}