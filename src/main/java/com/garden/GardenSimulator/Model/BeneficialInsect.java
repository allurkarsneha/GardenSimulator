package com.garden.GardenSimulator.Model;

public class BeneficialInsect extends Insect {

    // Constructors
    public BeneficialInsect(String name, int row, int col) {
        super(name, row, col);
    }

    @Override
    public boolean isPest() {
        return false;
    }
}