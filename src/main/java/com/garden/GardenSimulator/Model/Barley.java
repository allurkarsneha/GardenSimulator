package com.garden.GardenSimulator.Model;

public class Barley extends Plant {

    private static int count;

    public int getCount() {
        return count;
    }


    public void decrementCount() {
        count -= 1;
    }

    public Barley(int row, int col) {
        super("Barley", 9, 20, row, col, 2);
        count += 1;
    }



}
