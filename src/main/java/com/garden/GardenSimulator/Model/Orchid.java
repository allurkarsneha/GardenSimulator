package com.garden.GardenSimulator.Model;

public class Orchid extends Plant {

    private static int count;

    public Orchid(int row, int col) {
        // name, maxLifespan, waterRequirement, row, col, fertilizingFrequency
        super("Orchid", 12, 12, row, col, 3);
        count += 1;
    }

    public int getCount() { return count;}

    public void decrementCount() { count -= 1;}
}
