package com.garden.GardenSimulator.Controllers;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import java.io.File;
import java.util.List;
import com.garden.GardenSimulator.Systems.HeatingSystem;
import javafx.animation.TranslateTransition;
import com.garden.GardenSimulator.Systems.Sensor;
import javafx.animation.PauseTransition;
import com.garden.GardenSimulator.Model.Plant;


public class SensorController {
    private final GridPane gridPane;
    private final Image sensorImage;

    public void generateAnimation(Sensor sensor, GridPane gardenGrid, List<Plant> plants) {
        if (gardenGrid == null || plants == null || sensor == null) {
            throw new IllegalArgumentException("gardenGrid, plants, and sensor cannot be null");
        }

        String weather = sensor.getType();


        gardenGrid.getChildren().removeIf(node -> node instanceof ImageView && node.getId() != null && node.getId().startsWith("anim_"));

        String plantAnimationPath;
        String sensorAnimationPath;

        switch (weather) {
            case "Sunny" -> {
                plantAnimationPath = "src/main/images/sunbeam.png";
                sensorAnimationPath = "src/main/images/sprinkler1.gif";
            }
            case "Rainy" -> {
                plantAnimationPath = "src/main/images/water_droplet.png";
                sensorAnimationPath = "src/main/images/sunny.gif";
            }
            case "Cold" -> {
                plantAnimationPath = "src/main/images/snow.png";
                sensorAnimationPath = "src/main/images/heating.gif";
            }
            case "Snowy" -> {
                plantAnimationPath = "src/main/images/snowball.png";
                sensorAnimationPath = "src/main/images/heating.gif";
            }
            case "Cloudy" -> {
                plantAnimationPath = "src/main/images/clouds.png";
                sensorAnimationPath = "src/main/images/sunny.gif";
            }
            case "Windy" -> {
                plantAnimationPath = "src/main/images/wind.png";
                sensorAnimationPath = "src/main/images/mulching.png";
            }
            default -> throw new IllegalArgumentException("Unknown weather type: " + weather);
        }

        ImageView sensorGifImageView = new ImageView(new Image(new File(sensorAnimationPath).toURI().toString()));
        sensorGifImageView.setFitWidth(100);
        sensorGifImageView.setFitHeight(100);
        sensorGifImageView.setPreserveRatio(true);
        sensorGifImageView.setId("anim_sensor"); // Mark this for targeted removal later
        gardenGrid.add(sensorGifImageView, 0, 0);

        for (Plant plant : plants) {
            if (plant.isDead()) continue;

            int currentRow = plant.getRow();
            int currentCol = plant.getCol();

            ImageView plantAnimationImageView = new ImageView(new Image(new File(plantAnimationPath).toURI().toString()));
            plantAnimationImageView.setFitWidth(30);
            plantAnimationImageView.setFitHeight(30);
            plantAnimationImageView.setId("anim_plant_" + currentRow + "_" + currentCol); // Mark for cleanup
            gardenGrid.add(plantAnimationImageView, currentCol, currentRow);

            // Create animation
            TranslateTransition plantTransition = new TranslateTransition(Duration.seconds(1), plantAnimationImageView);
            plantTransition.setToX(20);
            plantTransition.setToY(20);
            plantTransition.setAutoReverse(true);
            plantTransition.setCycleCount(2);


            // Update plant lifespan for weather
            switch (weather) {
                case "Rainy" -> plant.boostGrowth();
                case "Sunny", "Cloudy", "Windy" -> plant.getDaysToLiveProperty().set(plant.getDaysToLive() + 1);
                case "Cold", "Snowy" -> {
                    plant.getDaysToLiveProperty().set(plant.getDaysToLive() - 1);
                    if (plant.getDaysToLive() <= 0) {
                        plant.decrementDaysToLive();
                    }
                }
            }
        }

        PauseTransition gifDisplayDuration = new PauseTransition(Duration.seconds(3));
        gifDisplayDuration.play();
    }

    public SensorController(GridPane gridPane) {
        this.gridPane = gridPane;
        this.sensorImage = new Image(new File("src/main/images/sensor.png").toURI().toString());
        // Add heating system
        HeatingSystem heatingSystem = new HeatingSystem();
    }

}
