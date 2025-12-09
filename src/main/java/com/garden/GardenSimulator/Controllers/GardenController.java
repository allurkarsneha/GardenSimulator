package com.garden.GardenSimulator.Controllers;
import com.garden.GardenSimulator.Model.Insect;
import com.garden.GardenSimulator.Systems.IrrigationSystem;
import javafx.scene.layout.GridPane;
import com.garden.GardenSimulator.View.Logger;
import com.garden.GardenSimulator.Model.Plant;
import com.garden.GardenSimulator.Systems.Sensor;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GardenController {
    private final List<Plant> plants = new ArrayList<>();
    private final List<Insect> insects = new ArrayList<>();
    private final PestController pestController;
    private final SensorController sensorController;
    private final Logger logger;

    private int dayCount;
    private static final String[] WEATHER_TYPES = {"Sunny", "Rainy", "Cold", "Snowy", "Cloudy", "Windy"};

    private final GridPane gardenGrid;

    public void simulateDay() {
        dayCount++;
        logger.addDayLogEntry("Day " + dayCount + ": simulation started.");

        // Determine the current weather
        String weather = getCurrentWeather();
        logger.addDayLogEntry("Weather: " + weather);

        // Clear dead plants
        plants.removeIf(Plant::isDead);

        // Update plant conditions
        for (Plant plant : plants) {
            if (!plant.isDead()) {
                plant.setCurrentWater(plant.getCurrentWater() - 1);
            }
        }

        // Manage pests
        insects.clear();
        pestController.managePests(plants, insects, logger, dayCount);
        IrrigationSystem irrigationSystem = new IrrigationSystem();
        irrigationSystem.waterPlants();
        logger.addWateringLogEntry("Day " + dayCount + ": Irrigation system has watered all the zones.");

        // Log watering activity
        logger.addWateringLogEntry("Day " + dayCount + ": Plants watered!");

        for (Plant plant : plants) {
            if (!plant.isDead()) {
                // Meet water requirement so plants gain a bit of life
                plant.water(plant.getWaterRequirement());
            }
        }


        // Create a sensor with the current weather
        Sensor currentSensor = new Sensor(weather, 10); // Example: fixed temperature 10°C

        // Trigger animations and weather-specific effects
        sensorController.generateAnimation(currentSensor, gardenGrid, plants);

        // Additional actions based on weather
        switch (weather) {
            case "Cold", "Snowy" -> logger.addHeatingLogEntry("Day " + dayCount + ": Heating system is activated.");
            case "Cloudy" -> logger.addDayLogEntry("Day " + dayCount + ": Weather is Cloudy. No special actions taken.");
            case "Windy" -> logger.addDayLogEntry("Day " + dayCount + ": Weather ris Windy - Wind protection shield activated.");
            case "Sunny" -> logger.addDayLogEntry("Day " + dayCount + ": Weather is Sunny - Sprinklers activated for dry sunny weather.");
            case "Rainy" -> logger.addDayLogEntry("Day " + dayCount + ": weather is Rainy - Plants received natural watering from rain.");
        }

        logger.addDayLogEntry("Day " + dayCount + ": simulation completed.");
    }

    public GardenController(PestController pestController, SensorController sensorController, GridPane gardenGrid) {
        this.pestController = pestController;
        this.sensorController = sensorController;
        this.gardenGrid = gardenGrid; // Add GridPane reference

        logger = new Logger();
        dayCount = 0;
    }

    public Logger getLogger() {
        return logger;
    }

    public int getDay() {
        return dayCount;
    }

    public void addPlant(Plant plant) {
        plants.add(plant);
        logger.addDayLogEntry("Added plant: " + plant.getName() + " at grid (" + plant.getRow() + "," + plant.getCol() + ")");
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public List<Insect> getInsects() {
        return insects;
    }

    public String getCurrentWeather() {
        if (WEATHER_TYPES == null) {
            System.err.println("Error: Since WEATHER_TYPES is not configured. Defaulting to 'Sunny'.");
            return "Sunny";
        }
        String weather = WEATHER_TYPES[new Random().nextInt(WEATHER_TYPES.length)];
        if (weather == null) {
            System.err.println("Error: Defaulting the weather to 'Sunny'.");
            return "Sunny";
        }
        return weather;
    }

}
