package com.garden.GardenSimulator.Systems;

import com.garden.GardenSimulator.Model.Plant;

import java.util.ArrayList;

public class IrrigationSystem {
    // ------ Instance Variables ------
    private static final ArrayList<Zone> zones = new ArrayList<>();

    static {
        ArrayList<int[]> tuples = new ArrayList<>();
        int[] start = new int[]{0, 3, 6};
        int k = 1;
        for (int x : start) {
            for (int y : start) {
                for (int i = x; i < x + 3; i++) {
                    for (int j = y; j < y + 3; j++) {
                        tuples.add(new int[]{i, j});
                    }
                }
                // Default Zone configuration
                zones.add(new Zone(k, tuples, "Sprinkler", 1));
                tuples = new ArrayList<>();
                k++;
            }
        }
    }

    // ------ Accessor Methods ------
    public static ArrayList<Zone> getZones() {
        return zones;
    }

    public static Zone getZone(int id) {
        for (Zone zone: zones) {
            if (zone.getId() == id) {
                return zone;
            }
        }
        return zones.getFirst();
    }

    // ------ Mutator Methods ------
    public void addPlant(Plant plant) {
        int plantRow = plant.getRow();
        int plantCol = plant.getCol();

        for (Zone zone : zones) {
            for (int[] tile : zone.getTiles()) {
                if (tile[0] == plantRow && tile[1] == plantCol) {
                    zone.addPlant(plant);
                    // assuming a plant belongs to at most one zone;
                    return;
                }
            }
        }
    }


    // ------ Other Methods ------
    public void waterPlants() {
        for (Zone zone : zones) {
            zone.water();
        }
    }
}
