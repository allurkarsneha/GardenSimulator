package com.garden.GardenSimulator.ApiDriver;
import com.garden.GardenSimulator.Controllers.GertenSimulationAPI;

import java.util.Map;
import java.util.Scanner;

public class APITestDriver {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Initialize default garden
        System.out.println("Initializing garden with default plants: Barley, Mango, Watermelon, Spinach, Orchid...");
        GertenSimulationAPI.initializeGarden();

        // Show base plant info
        Map<String, Object> info = GertenSimulationAPI.getPlants();
        System.out.println("\n=== Initial Garden Plants ===");
        System.out.println("Names           : " + info.get("plants"));
        System.out.println("WaterRequirement: " + info.get("waterRequirement"));
        System.out.println("Parasites       : " + info.get("parasites"));

        // Let user choose environment conditions
        System.out.println("\n=== Configure Simulation Scenario ===");

        System.out.print("Rain amount (int, e.g., 0–10): ");
        int rainAmount = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Temperature (F, e.g., 30–110): ");
        int temp = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Parasite to release (Barley/Mango/etc. vuln. list, e.g., Aphid or Caterpillar, or 'none'): ");
        String parasiteType = scanner.nextLine().trim();

        // Apply selected conditions
        if (rainAmount != 0) {
            GertenSimulationAPI.rain(rainAmount);          // updates water
        }

        GertenSimulationAPI.temperature(temp);             // adjusts lifespan

        if (!parasiteType.equalsIgnoreCase("none") && !parasiteType.isEmpty()) {
            GertenSimulationAPI.parasite(parasiteType);    // pest attacks
        }

        // Get and print result state
        Map<String, Integer> state = GertenSimulationAPI.getStateAsMap();
        System.out.println("\n=== Simulation Result ===");
        System.out.println("Alive plants: " + state.get("alive"));
        System.out.println("Dead plants : " + state.get("dead"));
        System.out.println("Conditions applied -> Rain: " + rainAmount
                + ", Temperature: " + temp
                + ", Parasite: " + (parasiteType.isEmpty() ? "none" : parasiteType));

        System.out.println("\nFor detailed per-plant logs, check the Logger output file/window.");
    }
}
