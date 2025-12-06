package com.garden.GardenSimulator.Controllers;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import javafx.scene.image.Image;
import java.io.File;
import java.util.List;
import java.util.Random;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.animation.TranslateTransition;
import javafx.animation.ScaleTransition;
import com.garden.GardenSimulator.View.Logger;
import javafx.scene.image.ImageView;

import com.garden.GardenSimulator.Systems.Fertilizer;
import com.garden.GardenSimulator.Model.Plant;

public class FertilizerController {
    private static final String FERTILIZER_IMAGE_PATH = "src/main/images/farmer_walk.gif"; // Fertilizer image

    private final GridPane gridPane;
    private final Image fertilizerImage;
    private List<Fertilizer> fertilizers;
    private final Random random;

    public EventHandler<ActionEvent> manageFertilizers(List<Plant> plants, Logger logger, int dayCount) {
        if (dayCount % 3 == 0) { // Every 3 days, restock fertilizers
            restockFertilizer("Organic Fertilizer", 8, logger);
            restockFertilizer("Chemical Fertilizer", 8, logger);
        }

        for (Plant plant : plants) {
            if (plant.isDead()) continue; // Skip dead plants
            Fertilizer fertilizer = fertilizers.get(random.nextInt(fertilizers.size()));

            if (fertilizer.isInStock()) {
                fertilizer.reduceStock(1); // Use one unit of fertilizer
                fertilizerApply(plant, fertilizer, logger, dayCount);
            } else {
                logger.addFertilizerLogEntry("Day " + dayCount + ": " + fertilizer.getName() + " stock is not Available");
            }
        }
        return null;
    }

    public void restockFertilizer(String fertilizerName, int amount, Logger logger) {
        for (Fertilizer fertilizer : fertilizers) {
            if (fertilizer.getName().equals(fertilizerName)) {
                fertilizer.increaseStock(amount);
                System.out.println(amount + " units of " + fertilizerName + " is Restocked");
                logger.addFertilizerLogEntry(amount + " units of " + fertilizerName + " is Restocked");
                return;
            }
        }
        System.out.println(fertilizerName + " Fertilizer is not found.");
    }



    public void toHighlightPlant(Plant plant) {

        Node plantNode = getNodebyGridPane(gridPane, plant.getCol(), plant.getRow());

        if (plantNode != null) {

            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), plantNode);
            scaleTransition.setToX(1.5);
            scaleTransition.setToY(1.5);
            scaleTransition.setAutoReverse(true);
            scaleTransition.setCycleCount(2);

            scaleTransition.play();
        }
    }

    public void fertilizerApply(Plant plant, Fertilizer fertilizer, Logger logger, int dayCount) {
        plant.incrementDaysSinceLastFertilized();
        if (plant.getDaysSinceLastFertilized() >= plant.getFertilizingFrequency()) {
            plant.boostGrowth();


            logger.addFertilizerLogEntry("Day " + dayCount + ": Applied " + fertilizer.getName()
                    + " to plant: " + plant.getName() + " at grid (" + plant.getRow() + "," + plant.getCol() + ")");
            toHighlightPlant(plant);
            showFertilizerAnimation(plant);
            plant.resetDaysSinceLastFertilized();
        } else {
            logger.addFertilizerLogEntry("Day " + dayCount + ": Fertilizing not needed and Skipped " + plant.getName()
                    + " at grid (" + plant.getRow() + "," + plant.getCol() + "). Days since last fertilized: "
                    + plant.getDaysSinceLastFertilized() + ". Frequency: " + plant.getFertilizingFrequency());
        }
    }

    public FertilizerController(GridPane gridPane) {
        this.gridPane = gridPane;
        this.fertilizerImage = new Image(new File(FERTILIZER_IMAGE_PATH).toURI().toString());
        fertilizers = List.of(
                new Fertilizer("Organic Fertilizer", 13, 1),
                new Fertilizer("Chemical Fertilizer", 13, 2)
        ); // Initialize with 2 types of fertilizers
        random = new Random();
    }


    public void showFertilizerAnimation(Plant plant) {
        ImageView imageView = new ImageView(fertilizerImage);
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);

        int targetCol = plant.getCol();
        int targetRow = plant.getRow();

        // Start farmer in cell (0,0)
        gridPane.add(imageView, 0, 0);

        // Let layout pass run so cell positions are valid
        gridPane.applyCss();
        gridPane.layout();

        // Get the actual Node for the target cell
        Node targetCell = getNodebyGridPane(gridPane, targetCol, targetRow);
        if (targetCell == null) {
            return;
        }

        // Compute target center in parent coordinates
        double targetX = targetCell.getLayoutX() + targetCell.getBoundsInParent().getWidth() / 2
                - imageView.getFitWidth() / 2;
        double targetY = targetCell.getLayoutY() + targetCell.getBoundsInParent().getHeight() / 2
                - imageView.getFitHeight() / 2;

        // Animate translate from (0,0 cell) to target cell
        TranslateTransition transition = new TranslateTransition(Duration.seconds(2), imageView);
        transition.setToX(targetX - imageView.getLayoutX());
        transition.setToY(targetY - imageView.getLayoutY());
        transition.setOnFinished(event -> gridPane.getChildren().remove(imageView));
        transition.play();
    }


    private Node getNodebyGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }





}

