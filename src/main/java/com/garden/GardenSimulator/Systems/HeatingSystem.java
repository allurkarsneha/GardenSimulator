package com.garden.GardenSimulator.Systems;

import com.garden.GardenSimulator.Model.Plant;

import java.util.List;

public class HeatingSystem {
    private int currentTemperature;

    public HeatingSystem() {
        this.currentTemperature = 20;
    }

    public void increaseTemperature(List<Plant> plants) {
        currentTemperature += 5;
        for (Plant plant : plants) {
            if (!plant.isDead()) {
                plant.boostGrowth();
            }
        }
    }

    public void decreaseTemperature(List<Plant> plants) {
        currentTemperature -= 5;
        for (Plant plant : plants) {
            if (!plant.isDead()) {
                plant.getDaysToLiveProperty().set(plant.getDaysToLive() - 1);
            }
        }
    }
}


