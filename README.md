# Automated Gardening Simulator 🌱

A JavaFX-based automated gardening simulator built for the CSEN 275 Object Oriented Analysis, Design, and Programming course.  
The system models a smart garden with plants, pests, sensors, irrigation, heating, fertilizers, and rich visual/logging support.

---

## Features

- **Interactive JavaFX UI**
  - Large grid-based garden view with animated plants, insects, and a walking farmer.
  - Weather forecast panel and live garden metrics (water and temperature indicators).
  - Start/Pause controls and plant selection panel for adding crops to the garden.

- **Automated Garden Modules**
  - **Pest control**: detects pest attacks, spawns cleaners, and visually shows them visiting affected plants.
  - **Fertilizer system**: tracks fertilizer stock, applies fertilizer on schedule, and animates the farmer moving to plants.
  - **Irrigation system**: waters plants automatically and logs watering actions.
  - **Heating / weather response**: reacts to cold / hot weather and adjusts plant lifespan.

- **Detailed Logging**
  - Separate logs for:
    - Day events
    - Watering / irrigation
    - Heating actions
    - Insect attacks and cleaner actions
    - Fertilizer usage
  - Logs are visible in the UI (accordion of log panels) and are also written to a log file for offline inspection.

- **Testing API for Automation**
  - `GertenSimulationAPI` exposes:
    - `initializeGarden()`
    - `getPlants()`
    - `rain(int)`
    - `temperature(int)`
    - `parasite(String)`
    - `getState()`
  - Used by an external script or the small `APITestDriver` to simulate 24 days of random weather, rain, and parasite events without using the GUI.

---

## Project Structure

Key packages and classes:

- `com.garden.GardenSimulator.View`
  - `UserInterface` – JavaFX UI, garden grid, animations, log panels.
  - `Main` – entry point for the GUI; clears previous log file and launches the UI.
- `com.garden.GardenSimulator.Controllers`
  - `GardenController` – core garden logic, day simulation, weather, and coordination.
  - `PestController` – manages pests, beneficial insects, and cleaners.
  - `FertilizerController` – manages fertilizer inventory and farmer/fertilizer animations.
  - `SensorController` – handles sensor-based animations and weather effects.
  - `GertenSimulationAPI` – headless testing API used for automated grading.
- `com.garden.GardenSimulator.Model`
  - `Plant` base class and concrete plants (e.g.,Barley,Mango,Spinach,Orchid,Watermelon).
  - `Insect`, `Pest`, `BeneficialInsect`.
- `com.garden.GardenSimulator.Systems`
  - `IrrigationSystem`, `HeatingSystem`, `Sensor`, `Zone`, `Cleaner`, `Fertilizer`.
- `log file`
  - `GardenSimulatorLogs.txt` – human‑readable event log generated on each run.

---

## How to Run

### Prerequisites

- Java 22 (or the version configured in `pom.xml`)
- Maven
- JavaFX runtime (handled via Maven dependencies in `pom.xml`)

### Run the GUI (main simulator)

From the project root:
mvn clean install
mvn javafx:run

or run the `Main` class:

- Main class: `com.garden.GardenSimulator.View.Main`

When the app starts:

1. Read the console instructions.
2. Use the plant selector at the top to add plants to the grid.
3. Click **Start Simulation** to begin day-by-day simulation.
4. Watch weather, metrics, and animations on the right side.
5. Click **Show Logs** to open the detailed log panels.

### Run the Headless API (for automated tests)

There is a simple driver that exercises `GertenSimulationAPI` without opening the UI.

- Main class: `com.garden.GardenSimulator.ApiDriver.APITestDriver`

From the command line:
mvn -Dexec.mainClass="com.garden.GardenSimulator.ApiDriver.APITestDriver" exec:java


This will:

1. Initialize the garden with predefined plants.
2. Retrieve plant definitions via `getPlants()`.
3. Invoke `rain(...)`, `temperature(...)`, and `parasite(...)`.
4. Print log output and a final `getState()` summary to the console and log file.

---

## Controls and Behavior

- **Select plants**: Use the plant buttons/images at the top to place crops onto empty cells in the grid.
- **Start Simulation**: Begins automatic day progression, weather changes, pest attacks, irrigation, and fertilizing cycles.
- **Pause Simulation**: Temporarily stops the day progression.
- **Garden metrics** (right side):
  - Water level indicator.
  - Temperature indicator.
  - Garden metrics list (counts of plants, insects, and pests).
- **Logs**:
  - Use the **Show Logs** / **Hide Logs** button to toggle the bottom log panel.
  - Each log pane (Day, Watering, Heating, Insect, Cleaner, Fertilizer) scrolls independently.

---

## Implementation Notes

- Built using **JavaFX** with a single primary `BorderPane` layout:
  - Center: garden grid + controls.
  - Right: weather + metrics.
  - Bottom: collapsible logs.
- Animations:
  - Background particles, moving birds.
  - Farmer walking (`farmer_walk.gif`) to fertilized plants.
  - Cleaners moving to pest‑attacked plants.
- Robust logging via a custom `Logger` class that:
  - Appends to `GardenSimulatorLogs.txt`.
  - Feeds JavaFX `ListView<TextFlow>` components with nicely styled entries.

---

## Future Improvements

- Smarter pest and fertilizer scheduling based on plant health and weather trends.
- More plant types, pest species, and weather patterns (droughts, storms, heatwaves).
- Configurable garden size, simulation speed, and difficulty levels.
- Sound effects and richer animations for weather, irrigation, and pest control.
- Tutorial or guided walkthrough mode for first-time users.
- Accessibility improvements (keyboard navigation, high-contrast mode, screen‑reader‑friendly logs).
- Network or API mode where an external monitoring service can adjust parameters in real time.

---

## License

This project was built for an academic course; check with the course policy before reusing for other submissions.  
For personal or learning use, you are free to fork and extend the project.


