package com.garden.GardenSimulator.ApiDriver;

import com.garden.GardenSimulator.Controllers.GertenSimulationAPI;

import java.util.Map;

public class APITestDriver {
    public static void main(String[] args) {
        GertenSimulationAPI.initializeGarden();

        Map<String, Object> info = GertenSimulationAPI.getPlants();

        System.out.println("API plants: " + info.get("plants"));

        GertenSimulationAPI.rain(5);
        GertenSimulationAPI.temperature(70);
        GertenSimulationAPI.parasite("Aphid");

        GertenSimulationAPI.getState();
    }
}
