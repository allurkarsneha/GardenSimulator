package com.garden.GardenSimulator.View;

import com.garden.GardenSimulator.Controllers.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.garden.GardenSimulator.Systems.Sensor;
import com.garden.GardenSimulator.Systems.Cleaner;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.util.Duration;
import com.garden.GardenSimulator.Model.*;
import javafx.scene.effect.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class UserInterface extends Application {
    protected GridPane gardenGrid;
    protected ListView<TextFlow> dayLogList;
    protected ListView<TextFlow> wateringLogList;
    protected ListView<TextFlow> heatingLogList;
    protected ListView<TextFlow> insectLogList;
    protected ListView<TextFlow> fertilizerLogList;
    protected ListView<TextFlow> cleanerLogList;
    protected Random random = new Random();
    protected ImageView weatherImageView;
    protected Label weatherLabel;
    protected ListView<String> directoryListView;
    protected Timeline simulationTimeline;
    protected PestController pestController;
    protected String currentWeather;
    protected ProgressBar waterProgressBar;
    protected Label waterProgressLabel;
    protected ProgressBar temperatureProgressBar;
    protected Label temperatureProgressLabel;
    private GardenController gardenController;
    protected FertilizerController fertilizerController;
    protected Button startSimulationButton;
    protected Button pauseSimulationButton;
    private final HashMap<String, Image> imageCache = new HashMap<>();
    private Label fertilizerStockLabel;
    private ProgressBar fertilizerStockProgressBar;
    private int fertilizerStock = 100;
    private SensorController sensorController;
    private static final int MAX_LOG_ITEMS = 500;

    @Override
    public void start(Stage primaryStage) {
        // Background root
        StackPane root = new StackPane();
        createAnimatedBackground(root);

        // Main layout over background
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle(
                "-fx-background-color: #0a1628;"
        );

        root.getChildren().add(mainLayout);

        // Garden grid and controllers
        createGardenGrid();
        pestController = new PestController(gardenGrid);
        fertilizerController = new FertilizerController(gardenGrid);
        sensorController = new SensorController(gardenGrid);
        gardenController = new GardenController(
                pestController,
                sensorController,
                gardenGrid
        );


        // Center: plant selector + grid + buttons
        VBox centerBox = new VBox();
        centerBox.setSpacing(10);

        VBox plantBox = createPlantBox();
        VBox.setVgrow(plantBox, Priority.NEVER);
        VBox.setVgrow(gardenGrid, Priority.ALWAYS);

        HBox buttonBox = createButtonBox();  // start/pause buttons for now

        centerBox.getChildren().addAll(plantBox, gardenGrid, buttonBox);
        mainLayout.setCenter(centerBox);

        // Logs accordion at the bottom
        createLogLists();
        Accordion logAccordion = createLogList();
        logAccordion.setVisible(false);
        logAccordion.managedProperty().bind(logAccordion.visibleProperty());
        logAccordion.setPrefHeight(260);          // good space for logs
        logAccordion.setMinHeight(260);
        mainLayout.setBottom(logAccordion);
        BorderPane.setMargin(logAccordion, new Insets(15, 0, 0, 0));

        // Weather / metrics on the right
        VBox weatherBox = createWeatherBox();
        VBox progressBox = createProgressBox();
        HBox tempAndWaterBox = createTempAndWaterBox(progressBox);
        Label directoryLabel = createDirectoryLabel();
        createDirectoryListView();
        VBox rightPane = createRightPane(weatherBox, tempAndWaterBox, directoryLabel);
        mainLayout.setRight(rightPane);
        BorderPane.setMargin(rightPane, new Insets(0, 0, 0, 15));

        // Fertilizer stock labels
        fertilizerStockLabel = new Label("Fertilizer Stock: " + fertilizerStock);
        fertilizerStockLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        fertilizerStockLabel.setTextFill(Color.LIMEGREEN);
        fertilizerStockProgressBar = new ProgressBar(1.0);
        fertilizerStockProgressBar.setPrefWidth(200);

        // Show / Hide Logs button
        Button toggleLogsButton = new Button("Show Logs");
        toggleLogsButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #00d9ff, #0099cc); " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: #00ffff; " +
                        "-fx-border-radius: 12; " +
                        "-fx-background-radius: 12; " +
                        "-fx-padding: 8 16; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 15px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 217, 255, 0.6), 10, 0, 0, 0);"
        );
        toggleLogsButton.setOnMouseEntered(e -> toggleLogsButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #00ffff, #00b8d4); " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: #00ffff; " +
                        "-fx-border-radius: 12; " +
                        "-fx-background-radius: 12; " +
                        "-fx-padding: 8 16; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 15px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 255, 255, 0.9), 15, 0, 0, 0); " +
                        "-fx-scale-x: 1.05; -fx-scale-y: 1.05;"
        ));
        toggleLogsButton.setOnMouseExited(e -> toggleLogsButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #00d9ff, #0099cc); " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: #00ffff; " +
                        "-fx-border-radius: 12; " +
                        "-fx-background-radius: 12; " +
                        "-fx-padding: 8 16; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 15px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 217, 255, 0.6), 10, 0, 0, 0);"
        ));
        toggleLogsButton.setOnAction(event -> {
            boolean nowVisible = !logAccordion.isVisible();
            logAccordion.setVisible(nowVisible);
            toggleLogsButton.setText(nowVisible ? "Hide Logs" : "Show Logs");
        });

        buttonBox.getChildren().add(toggleLogsButton);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Automated Gardening System");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

        startAnimation();
    }

    // === Background ===
    private void createAnimatedBackground(Pane root) {
        Pane backgroundPane = new Pane();
        backgroundPane.setPickOnBounds(false);

        for (int i = 0; i < 30; i++) {
            Circle particle = new Circle(
                    random.nextDouble() * 1920,
                    random.nextDouble() * 1080,
                    random.nextDouble() * 3 + 1
            );

            Color[] colors = {
                    Color.web("#00d9ff", 0.3),
                    Color.web("#00ffaa", 0.3),
                    Color.web("#ff00ff", 0.2),
                    Color.web("#ffff00", 0.2),
                    Color.web("#00ff00", 0.3)
            };
            particle.setFill(colors[random.nextInt(colors.length)]);

            TranslateTransition translate =
                    new TranslateTransition(Duration.seconds(random.nextDouble() * 20 + 10), particle);
            translate.setByY(random.nextDouble() * 200 - 100);
            translate.setByX(random.nextDouble() * 200 - 100);
            translate.setCycleCount(Timeline.INDEFINITE);
            translate.setAutoReverse(true);
            translate.play();

            FadeTransition fade =
                    new FadeTransition(Duration.seconds(random.nextDouble() * 3 + 2), particle);
            fade.setFromValue(0.1);
            fade.setToValue(0.8);
            fade.setCycleCount(Timeline.INDEFINITE);
            fade.setAutoReverse(true);
            fade.play();

            backgroundPane.getChildren().add(particle);
        }

        root.getChildren().addFirst(backgroundPane);
    }

    // === Garden grid ===
    private void createGardenGrid() {
        gardenGrid = new GridPane();
        gardenGrid.setPadding(new Insets(20));
        gardenGrid.setHgap(10);
        gardenGrid.setVgap(10);

        String imagePath = "file:src/main/images/soil.png";

        for (int col = 0; col < 12; col++) {
            for (int row = 0; row < 7; row++) {
                StackPane cell = new StackPane();
                cell.setMinSize(70, 70);

                cell.setStyle(
                        "-fx-border-color: #2d5f3f; " +
                                "-fx-border-width: 2px; " +
                                "-fx-background-image: url('" + imagePath + "');" +
                                "-fx-background-size: cover; " +
                                "-fx-background-position: center; " +
                                "-fx-background-radius: 8; " +
                                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0, 0, 2);"
                );

                gardenGrid.add(cell, col, row);
            }
        }

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1.5), gardenGrid);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();

        HBox.setHgrow(gardenGrid, Priority.ALWAYS);
    }

    // === Logs ===
    private void createLogLists() {
        dayLogList = new ListView<>();
        wateringLogList = new ListView<>();
        heatingLogList = new ListView<>();
        insectLogList = new ListView<>();
        cleanerLogList = new ListView<>();
        fertilizerLogList = new ListView<>();

        VBox.setVgrow(dayLogList, Priority.ALWAYS);
        VBox.setVgrow(wateringLogList, Priority.ALWAYS);
        VBox.setVgrow(heatingLogList, Priority.ALWAYS);
        VBox.setVgrow(insectLogList, Priority.ALWAYS);
        VBox.setVgrow(cleanerLogList, Priority.ALWAYS);
        VBox.setVgrow(fertilizerLogList, Priority.ALWAYS);
    }

    private Accordion createLogList() {
        dayLogList = createStyledListView("Day Logs");
        wateringLogList = createStyledListView("Watering Logs");
        heatingLogList = createStyledListView("Heating Logs");
        insectLogList = createStyledListView("Insect Attack Logs");
        cleanerLogList = createStyledListView("Cleaner Logs");
        fertilizerLogList = createStyledListView("Fertilizer Logs");

        TitledPane dayLogPane = createStyledTitledPane("Day Logs", dayLogList);
        TitledPane wateringLogPane = createStyledTitledPane("Watering Logs", wateringLogList);
        TitledPane heatingLogPane = createStyledTitledPane("Heating Logs", heatingLogList);
        TitledPane insectLogPane = createStyledTitledPane("Insect Attack Logs", insectLogList);
        TitledPane cleanerLogPane = createStyledTitledPane("Cleaner Logs", cleanerLogList);
        TitledPane fertilizerLogPane = createStyledTitledPane("Fertilizer Logs", fertilizerLogList);

        Accordion logAccordion = new Accordion(
                dayLogPane,
                wateringLogPane,
                heatingLogPane,
                insectLogPane,
                cleanerLogPane,
                fertilizerLogPane
        );
        logAccordion.setStyle(
                "-fx-background-color: rgba(10, 18, 30, 0.95); " +
                        "-fx-border-color: #00d9ff; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 15; " +
                        "-fx-background-radius: 15;"
        );
        return logAccordion;
    }


    private VBox createPlantBox() {
        VBox plantBox = new VBox();
        plantBox.setSpacing(12);

        Label plantLabel = new Label("Select Plants");
        plantLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        plantLabel.setTextFill(Color.web("#00ffaa"));
        plantLabel.setPadding(new Insets(10, 0, 0, 0));
        plantLabel.setStyle(
                "-fx-effect: dropshadow(gaussian, rgba(0, 255, 170, 0.9), 12, 0, 0, 2);"
        );

        HBox plantImagesBox = new HBox();
        plantImagesBox.setSpacing(27);

        VBox barleyBox = createPlantOption("src/main/images/barley.gif", "Barley");
        VBox mangoBox = createPlantOption("src/main/images/mango.gif", "Mango");
        VBox watermelonBox = createPlantOption("src/main/images/watermelon.gif", "Watermelon");
        VBox spinachBox = createPlantOption("src/main/images/spinach.gif", "Spinach");
        VBox orchidBox = createPlantOption("src/main/images/orchid.gif", "Orchid");


        plantImagesBox.getChildren().addAll(barleyBox, mangoBox, watermelonBox, spinachBox, orchidBox);
        plantBox.getChildren().addAll(plantLabel, plantImagesBox);

        VBox.setVgrow(plantBox, Priority.NEVER);
        return plantBox;
    }

    private VBox createPlantOption(String fileName, String plantType) {
        ImageView imageView = getImageView(fileName);
        Button button = new Button();
        button.setGraphic(imageView);
        button.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(0, 50, 30, 0.9), rgba(0, 30, 20, 0.9)); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-border-color: #00ff88; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 12; " +
                        "-fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 255, 136, 0.5), 10, 0, 0, 0);"
        );


        button.setOnAction(_ -> {
            // Prevent infinite loop when grid is full
            if (!hasEmptyCell()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Garden is full");
                alert.setHeaderText(null);
                alert.setContentText("All plots are occupied!!");
                alert.showAndWait();
                return;
            }

            int row, col;
            do {
                row = random.nextInt(7);
                col = random.nextInt(12);
            } while (!isCellEmpty(row, col));

            Plant plant = null;
            if ("Barley".equals(plantType)) {
                plant = new Barley(row, col);
            } else if ("Mango".equals(plantType)) {
                plant = new Mango(row, col);
            } else if ("Watermelon".equals(plantType)) {
                plant = new Watermelon(row, col);
            } else if ("Spinach".equals(plantType)) {
                plant = new Spinach(row, col);
            } else if ("Orchid".equals(plantType)) {
                plant = new Orchid(row, col);
            }

            if (plant != null) {
                gardenController.addPlant(plant);
                animatePlant(plant);
                updateGardenGrid();
                updateLog();
                updateDirectory();
            }
        });

        Label nameLabel = new Label(plantType);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        nameLabel.setStyle(
                "-fx-background-color: linear-gradient(to right, rgba(0, 217, 255, 0.8), rgba(0, 255, 170, 0.8)); " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: #00ffaa; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 5 15; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 255, 170, 0.6), 8, 0, 0, 0);"
        );

        VBox plantBox = new VBox(imageView, button, nameLabel);
        plantBox.setAlignment(Pos.CENTER);
        plantBox.setSpacing(8);

        return plantBox;
    }

    private VBox createWeatherBox() {
        Label weatherReportLabel = new Label("Weather Forecast");
        weatherReportLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        weatherReportLabel.setStyle(
                "-fx-background-color: linear-gradient(to right, rgba(0, 150, 255, 0.9), rgba(0, 217, 255, 0.9)); " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: #00d9ff; " +
                        "-fx-border-width: 2px; " +
                        "-fx-padding: 12px; " +
                        "-fx-background-radius: 12px; " +
                        "-fx-border-radius: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 217, 255, 0.7), 10, 0, 0, 0);"
        );

        weatherImageView = new ImageView();
        weatherImageView.setFitWidth(120);
        weatherImageView.setFitHeight(120);

        weatherLabel = new Label("Day 1: Start");
        weatherLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        weatherLabel.setStyle(
                "-fx-background-color: rgba(0, 217, 255, 0.2); " +
                        "-fx-text-fill: #00ffff; " +
                        "-fx-border-color: #00d9ff; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 8 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 217, 255, 0.5), 8, 0, 0, 0);"
        );

        VBox weatherBox = new VBox();
        weatherBox.setSpacing(12);
        weatherBox.setAlignment(Pos.CENTER);
        weatherBox.getChildren().addAll(weatherReportLabel, weatherImageView, weatherLabel);
        weatherBox.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(11, 20, 35, 0.95), rgba(15, 30, 50, 0.95)); " +
                        "-fx-background-radius: 15; " +
                        "-fx-padding: 15; " +
                        "-fx-border-color: #00d9ff; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 15; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 217, 255, 0.4), 15, 0, 0, 0);"
        );
        return weatherBox;
    }

    private VBox createProgressBox() {
        VBox progressBox = new VBox();
        progressBox.setSpacing(12);

        waterProgressBar = new ProgressBar(0);
        waterProgressBar.setPrefWidth(220);
        waterProgressBar.setStyle("-fx-accent: #ff6b6b;");

        waterProgressLabel = new Label("Water percentage: 0%");
        waterProgressLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        waterProgressLabel.setStyle(
                "-fx-background-color: rgba(0, 217, 255, 0.2); " +
                        "-fx-text-fill: #00ffff; " +
                        "-fx-padding: 6 10; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: #00d9ff; " +
                        "-fx-border-width: 1px;"
        );

        temperatureProgressBar = new ProgressBar(0);
        temperatureProgressBar.setPrefWidth(220);
        temperatureProgressBar.setStyle("-fx-accent: #ff6b6b;");

        temperatureProgressLabel = new Label("Temperature percentage: 0%");
        temperatureProgressLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        temperatureProgressLabel.setStyle(
                "-fx-background-color: rgba(255, 107, 107, 0.2); " +
                        "-fx-text-fill: #ffaa00; " +
                        "-fx-padding: 6 10; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: #ffaa00; " +
                        "-fx-border-width: 1px;"
        );

        progressBox.getChildren().addAll(
                temperatureProgressBar,
                temperatureProgressLabel,
                waterProgressBar,
                waterProgressLabel
        );
        progressBox.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(11, 20, 35, 0.95), rgba(15, 30, 50, 0.95)); " +
                        "-fx-background-radius: 15; " +
                        "-fx-padding: 15; " +
                        "-fx-border-color: #00d9ff; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 15; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 217, 255, 0.4), 15, 0, 0, 0);"
        );
        return progressBox;
    }


    private HBox createTempAndWaterBox(VBox progressBox) {
        HBox tempAndWaterBox = new HBox();
        tempAndWaterBox.setSpacing(10);
        tempAndWaterBox.setAlignment(Pos.CENTER);
        tempAndWaterBox.getChildren().addAll(progressBox);
        tempAndWaterBox.setStyle("-fx-background-color: transparent;");
        return tempAndWaterBox;
    }

    private Label createDirectoryLabel() {
        Label directoryLabel = new Label("METRICS");
        directoryLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        directoryLabel.setStyle(
                "-fx-background-color: linear-gradient(to right, rgba(0, 255, 170, 0.9), rgba(0, 217, 255, 0.9)); " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: #00ffaa; " +
                        "-fx-border-radius: 12; " +
                        "-fx-background-radius: 12; " +
                        "-fx-padding: 10 15; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 255, 170, 0.7), 10, 0, 0, 0);"
        );
        return directoryLabel;
    }

    private void createDirectoryListView() {
        directoryListView = new ListView<>();
        directoryListView.setStyle(
                "-fx-border-color: #00ffaa;" +
                        "-fx-border-width: 2px;" +
                        "-fx-padding: 0;" +
                        "-fx-background-insets: 0;" +
                        "-fx-background-radius: 12;" +
                        "-fx-control-inner-background: transparent;"
        );

        directoryListView.setPrefSize(260, 380);
        directoryListView.setMaxSize(260, 380);
        VBox.setVgrow(directoryListView, Priority.ALWAYS);

        directoryListView.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    Label label = new Label(item);
                    label.setStyle(
                            "-fx-text-fill: white;" +
                                    "-fx-font-family: 'Arial';" +
                                    "-fx-font-size: 14px;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-padding: 6px;"
                    );

                    int index = getIndex();
                    if (index % 2 == 0) {
                        setStyle("-fx-background-color: rgba(0, 100, 80, 0.3);");
                    } else {
                        setStyle("-fx-background-color: rgba(0, 80, 100, 0.3);");
                    }

                    setGraphic(label);
                    setText(null);
                }
            }
        });

        VBox.setVgrow(directoryListView, Priority.ALWAYS);
    }

    private VBox createRightPane(VBox weatherBox, HBox tempAndWaterBox, Label directoryLabel) {
        VBox rightPane = new VBox();
        rightPane.setSpacing(12);
        rightPane.getChildren().addAll(
                weatherBox,
                tempAndWaterBox,
                directoryLabel,
                directoryListView
        );
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        rightPane.setStyle("-fx-background-color: transparent;");
        return rightPane;
    }

    private HBox createButtonBox() {
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(12);

        startSimulationButton = new Button("Start Simulation");
        startSimulationButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #00ff88, #00cc66); " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: #00ffaa; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10 20; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 255, 136, 0.6), 10, 0, 0, 0);"
        );
        startSimulationButton.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        startSimulationButton.setOnAction(_ -> startSimulation());

        pauseSimulationButton = new Button("Pause Simulation");
        pauseSimulationButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #ff6b6b, #ee5555); " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: #ff8888; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10 20; " +
                        "-fx-effect: dropshadow(gaussian, rgba(255, 107, 107, 0.6), 10, 0, 0, 0);"
        );
        pauseSimulationButton.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        pauseSimulationButton.setOnAction(_ -> pauseSimulation());
        pauseSimulationButton.setDisable(true);

        buttonBox.getChildren().addAll(startSimulationButton, pauseSimulationButton);
        buttonBox.setStyle("-fx-background-color: transparent;");
        return buttonBox;
    }

    // === Misc helpers ===
    private TitledPane createStyledTitledPane(String title, ListView<TextFlow> listView) {
        TitledPane pane = new TitledPane(title, listView);
        pane.setStyle(
                "-fx-font-weight: bold; " +
                        "-fx-text-fill: #00ffaa; " +
                        "-fx-background-color: rgba(15, 23, 42, 0.95);"
        );
        return pane;
    }

    private ListView<TextFlow> createStyledListView(String placeholder) {
        ListView<TextFlow> listView = new ListView<>();
        listView.setPlaceholder(new Label(placeholder));
        listView.setPrefHeight(150);
        listView.setStyle(
                "-fx-background-color: rgba(0, 50, 80, 0.15); " +
                        "-fx-border-radius: 8; " +
                        "-fx-border-color: #00d9ff; " +
                        "-fx-border-width: 1px;"
        );
        VBox.setVgrow(listView, Priority.ALWAYS);
        return listView;
    }


    private void updateWeatherImage(String weather) {
        String weatherImagePath = switch (weather) {
            case "Sunny" -> "src/main/images/sunny1.gif";
            case "Rainy" -> "src/main/images/rain.gif";
            case "Cold" -> "src/main/images/cold.gif";
            case "Snowy" -> "src/main/images/snowflakes.gif";
            case "Cloudy" -> "src/main/images/cloudy1.gif";
            case "Windy" -> "src/main/images/windy.gif";
            default -> null;
        };
        if (weatherImagePath != null) {
            weatherImageView.setImage(new Image(new File(weatherImagePath).toURI().toString()));
        }

        weatherLabel.setText("Day " + (gardenController.getDay()) + ": " + weather);

        int temperature = getTemperatureForDay(weather);
        temperatureProgressLabel.setText(temperature + " F");
        temperatureProgressLabel.setStyle(getLabelFontForWeather(weather));

        waterProgressLabel.setText(getWaterLevelStringForLabelFontForWeather(weather));
        waterProgressLabel.setStyle("-fx-text-fill: white;");

        updateProgressBars(temperature);
    }

    private int getTemperatureForDay(String currentWeather) {
        return switch (currentWeather) {
            case "Sunny" -> 90;
            case "Rainy" -> 50;
            case "Cold" -> 10;
            case "Snowy" -> 2;
            case "Cloudy" -> 70;
            case "Windy" -> 14;
            default -> 100;
        };
    }

    private String getLabelFontForWeather(String currentWeather) {
        return switch (currentWeather) {
            case "Sunny" -> "-fx-text-fill: #ffd93d;";
            case "Rainy" -> "-fx-text-fill: #a855f7;";
            case "Cloudy" -> "-fx-text-fill: #22c55e;";
            case "Cold" -> "-fx-text-fill: #a5b4fc;";
            case "Snowy" -> "-fx-text-fill: white;";
            case "Windy" -> "-fx-text-fill: #60a5fa;";
            default -> "-fx-text-fill: white;";
        };
    }

    private String getWaterLevelStringForLabelFontForWeather(String currentWeather) {
        return switch (currentWeather) {
            case "Sunny" -> "80 %";
            case "Rainy" -> "10 %";
            case "Cloudy" -> "40 %";
            case "Cold" -> "20%";
            case "Snowy" -> "10%";
            case "Windy" -> "30%";
            default -> "25 %";
        };
    }

    private void updateDirectory() {
        directoryListView.getItems().clear();
        directoryListView.getItems().add("Plants:");

        long barleyCount = gardenController.getPlants().stream().filter(p -> p.getName().equals("Barley")).count();
        long mangoCount = gardenController.getPlants().stream().filter(p -> p.getName().equals("Mango")).count();
        long watermelonCount = gardenController.getPlants().stream().filter(p -> p.getName().equals("Watermelon")).count();
        long spinachCount = gardenController.getPlants().stream().filter(p -> p.getName().equals("Spinach")).count();
        long orchidCount = gardenController.getPlants().stream().filter(p -> p.getName().equals("Orchid")).count();

        directoryListView.getItems().add("  - Barley: " + barleyCount);
        directoryListView.getItems().add("  - Mango: " + mangoCount);
        directoryListView.getItems().add("  - Watermelon: " + watermelonCount);
        directoryListView.getItems().add("  - Spinach: " + spinachCount);
        directoryListView.getItems().add("  - Orchid: " + orchidCount);

        directoryListView.getItems().add("Good Insects:");

        long ladyBugCount = gardenController.getInsects().stream().filter(i -> i.getName().equals("LadyBug")).count();
        long spiderCount = gardenController.getInsects().stream().filter(i -> i.getName().equals("Spider")).count();

        directoryListView.getItems().add("  - LadyBug: " + ladyBugCount);
        directoryListView.getItems().add("  - Spider: " + spiderCount);

        directoryListView.getItems().add("Pests:");

        long aphidCount = gardenController.getInsects().stream().filter(i -> i.getName().equals("Aphid")).count();
        long caterpillarCount = gardenController.getInsects().stream().filter(i -> i.getName().equals("Caterpillar")).count();

        directoryListView.getItems().add("  - Aphid: " + aphidCount);
        directoryListView.getItems().add("  - Caterpillar: " + caterpillarCount);
    }

    private void animatePlant(Plant plant) {
        javafx.scene.Node node = getNodeByRowColumnIndex(plant.getRow(), plant.getCol(), gardenGrid);
        if (node != null) {
            ScaleTransition st = new ScaleTransition(Duration.millis(500), node);
            st.setByX(1.2);
            st.setByY(1.2);
            st.setCycleCount(2);
            st.setAutoReverse(true);
            st.play();
        }
    }

    private void updateGardenGrid() {
        String imagePath = "file:src/main/images/soil.png";

        // Clear and reset cells
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 12; col++) {
                StackPane cell = (StackPane) getNodeByRowColumnIndex(row, col, gardenGrid);
                if (cell != null) {
                    cell.getChildren().clear();
                    cell.setStyle(
                            "-fx-border-color: linear-gradient(to bottom, #2d5f3f, #1a3d2e); " +
                                    "-fx-border-width: 2px; " +
                                    "-fx-background-image: url('" + imagePath + "'); " +
                                    "-fx-background-size: cover; " +
                                    "-fx-background-position: center; " +
                                    "-fx-background-radius: 8; " +
                                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0, 0, 2); " +
                                    "-fx-alignment: center;"
                    );
                }
            }
        }

        // Plants
        for (Plant plant : gardenController.getPlants()) {
            StackPane cell = (StackPane) getNodeByRowColumnIndex(plant.getRow(), plant.getCol(), gardenGrid);
            if (cell == null) continue;

            if (!plant.isDead()) {
                ImageView plantImage = getImageView("src/main/images/" + plant.getName().toLowerCase() + ".gif");
                if (plantImage != null) {
                    plantImage.setFitWidth(65);
                    plantImage.setFitHeight(65);
                    StackPane.setAlignment(plantImage, Pos.TOP_CENTER);
                    cell.getChildren().add(plantImage);
                }
                Tooltip.install(cell, new Tooltip(plant.getName()));
            } else {
                Label deadLabel = new Label("X");
                deadLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: brown;");
                cell.getChildren().add(deadLabel);
                cell.setStyle(
                        "-fx-border-color: black; -fx-alignment: center; -fx-background-color: brown;"
                );
            }
        }

        // Insects
        for (Insect insect : gardenController.getInsects()) {
            StackPane cell = (StackPane) getNodeByRowColumnIndex(insect.getRow(), insect.getCol(), gardenGrid);
            if (cell == null) continue;

            ImageView insectImage = getImageView("src/main/images/" + insect.getName().toLowerCase() + ".gif");
            if (insectImage != null) {
                insectImage.setFitWidth(35);
                insectImage.setFitHeight(35);
                StackPane.setAlignment(insectImage, Pos.BOTTOM_CENTER);
                cell.getChildren().add(insectImage);
                Tooltip.install(cell, new Tooltip(insect.getName()));

                if (insect.isPest()) {
                    cell.setStyle(
                            "-fx-border-color: black; -fx-alignment: center; -fx-background-color: red;"
                    );
                } else {
                    cell.setStyle(
                            "-fx-border-color: black; -fx-alignment: center; -fx-background-color: blue;"
                    );
                }
            }
        }

        // Cleaners
        for (Plant plant : gardenController.getPlants()) {
            if (plant.getPestAttacks() > 2 && !plant.isDead()) {

                // find the cleaner assigned in managePests (the busy one)
                Cleaner assigned = pestController.getCleaners().stream()
                        .filter(Cleaner::isBusy)   // or use a flag you already have
                        .findFirst()
                        .orElse(null);

                if (assigned == null) {
                    continue;
                }

                // draw the cleaner at this plant's grid cell
                StackPane cell = (StackPane) getNodeByRowColumnIndex(
                        plant.getRow(), plant.getCol(), gardenGrid);
                if (cell == null) {
                    continue;
                }

                ImageView cleanerImage = getImageView("src/main/images/cleaner.png");
                if (cleanerImage != null) {
                    cleanerImage.setFitWidth(35);
                    cleanerImage.setFitHeight(35);
                    StackPane.setAlignment(cleanerImage, Pos.CENTER);
                    cell.getChildren().add(cleanerImage);
                }
            }
        }

    }

    private boolean isCellEmpty(int row, int col) {
        StackPane cell = (StackPane) getNodeByRowColumnIndex(row, col, gardenGrid);
        return cell == null || cell.getChildren().isEmpty();
    }

    private boolean hasEmptyCell() {
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 12; col++) {
                if (isCellEmpty(row, col)) return true;
            }
        }
        return false;
    }

    private Image getImage(String fileName) {
        return imageCache.computeIfAbsent(fileName, key -> {
            try {
                FileInputStream input = new FileInputStream(key);
                return new Image(input);
            } catch (FileNotFoundException e) {
                System.out.println("File is not found: " + key);
                return null;
            }
        });
    }

    private ImageView getImageView(String fileName) {
        Image image = getImage(fileName);
        if (image != null) {
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(35);
            imageView.setFitHeight(35);
            return imageView;
        }
        return null;
    }

    private void addLogEntries(ListView<TextFlow> logList, List<String> logEntries, Color color) {
        for (String entry : logEntries) {
            String[] parts = entry.split(": ");
            TextFlow textFlow;

            if (parts.length > 1) {
                String timestamp = parts[0];
                String message = parts[1];

                Text timestampText = new Text(timestamp + ": ");
                timestampText.setFill(Color.GRAY);
                timestampText.setFont(Font.font("Arial", FontWeight.BOLD, 14));

                Text messageText = new Text(message + "\n");
                messageText.setFill(color);
                messageText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

                textFlow = new TextFlow(timestampText, messageText);
            } else {
                Text text = new Text(entry + "\n");
                text.setFill(color);
                text.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                textFlow = new TextFlow(text);
            }

            textFlow.setPadding(new Insets(5, 10, 5, 10));
            textFlow.setStyle(
                    "-fx-background-color: rgba(248, 250, 252, 0.12); " +
                            "-fx-background-radius: 5; " +
                            "-fx-border-color: rgba(148, 163, 184, 0.8); " +
                            "-fx-border-radius: 5; " +
                            "-fx-border-width: 1;"
            );

            // Cap items to hold most recent 500 in memory
            logList.getItems().add(textFlow);
            if (logList.getItems().size() > MAX_LOG_ITEMS) {
                logList.getItems().remove(0);
            }
        }
    }


    private void updateLog() {
        dayLogList.getItems().clear();
        wateringLogList.getItems().clear();
        heatingLogList.getItems().clear();
        insectLogList.getItems().clear();
        cleanerLogList.getItems().clear();
        fertilizerLogList.getItems().clear();

        addLogEntries(dayLogList, gardenController.getLogger().getDayLogEntries(), Color.CORNFLOWERBLUE);
        addLogEntries(wateringLogList, gardenController.getLogger().getWateringLogEntries(), Color.LIGHTGREEN);
        addLogEntries(heatingLogList, gardenController.getLogger().getHeatingLogEntries(), Color.ORANGE);
        addLogEntries(insectLogList, gardenController.getLogger().getInsectLogEntries(), Color.RED);
        addLogEntries(cleanerLogList, gardenController.getLogger().getCleanerLogEntries(), Color.MEDIUMPURPLE);
        addLogEntries(fertilizerLogList, gardenController.getLogger().getFertilizerLogEntries(), Color.GOLD);
    }

    private void startSimulation() {
        startSimulationButton.setDisable(true);
        if (gardenController.getDay() == 0) {
            simulateDay();
            simulationTimeline = new Timeline(new KeyFrame(Duration.seconds(1), _ -> simulateDay()));
            simulationTimeline.setCycleCount(Timeline.INDEFINITE);
        }
        simulationTimeline.play();
        pauseSimulationButton.setDisable(false);
    }

    private void pauseSimulation() {
        pauseSimulationButton.setDisable(true);
        if (simulationTimeline != null) {
            simulationTimeline.pause();
        }
        startSimulationButton.setDisable(false);
    }

    private void startAnimation() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGardenGrid();
            }
        };
        timer.start();
    }

    private static javafx.scene.Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        for (javafx.scene.Node node : gridPane.getChildren()) {
            Integer r = GridPane.getRowIndex(node);
            Integer c = GridPane.getColumnIndex(node);
            int rr = (r == null) ? 0 : r;
            int cc = (c == null) ? 0 : c;
            if (rr == row && cc == column) {
                return node;
            }
        }
        return null;
    }

    private void simulateDay() {
        String weather = gardenController.getCurrentWeather();
        if (weather == null) {
            System.err.println("Error: Weather is null. Defaulting to 'Sunny'.");
            weather = "Sunny";
        }
        currentWeather = weather;
        gardenController.simulateDay();

        fertilizerController.manageFertilizers(
                gardenController.getPlants(),
                gardenController.getLogger(),
                gardenController.getDay()
        );
        updateLog();
        updateGardenGrid();
        updateWeatherImage(weather);
        updateDirectory();
        Sensor currentSensor = new Sensor(weather, 10);
        sensorController.generateAnimation(currentSensor, gardenGrid, gardenController.getPlants());
        for (Plant plant : gardenController.getPlants()) {
            plant.adjustLifespanForWeather(weather);
        }
    }

    private void updateProgressBars(int temperature) {
        double temperatureProgress = temperature / 100.0;
        temperatureProgressBar.setProgress(temperatureProgress);
        temperatureProgressLabel.setText("Temperature: " + temperature + " F");

        if (temperature <= 50) {
            temperatureProgressBar.setStyle("-fx-accent: #0096ff;");
            temperatureProgressLabel.setTextFill(Color.web("#60a5fa"));
        } else if (temperature <= 70) {
            temperatureProgressBar.setStyle("-fx-accent: #0096ff;");
            temperatureProgressLabel.setTextFill(Color.web("#4ade80"));
        } else if (temperature <= 90) {
            temperatureProgressBar.setStyle("-fx-accent: #0096ff;");
            temperatureProgressLabel.setTextFill(Color.web("#fdba74"));
        } else {
            temperatureProgressBar.setStyle("-fx-accent: #0096ff;");
            temperatureProgressLabel.setTextFill(Color.web("#fca5a5"));
        }

        if (temperature <= 50) {
            waterProgressBar.setProgress(0.25);
            waterProgressBar.setStyle("-fx-accent: #0b1423;");
            waterProgressLabel.setText("Water Level: 25%");
            waterProgressLabel.setTextFill(Color.web("#60a5fa"));
        } else if (temperature <= 70) {
            waterProgressBar.setProgress(0.5);
            waterProgressBar.setStyle("-fx-accent: #0b1423;");
            waterProgressLabel.setText("Water Level: 50%");
            waterProgressLabel.setTextFill(Color.web("#4ade80"));
        } else if (temperature <= 90) {
            waterProgressBar.setProgress(0.75);
            waterProgressBar.setStyle("-fx-accent: #0b1423;");
            waterProgressLabel.setText("Water Level: 75%");
            waterProgressLabel.setTextFill(Color.web("#fdba74"));
        } else {
            waterProgressBar.setProgress(1.0);
            waterProgressBar.setStyle("-fx-accent: #0b1423;");
            waterProgressLabel.setText("Water Level: 100%");
            waterProgressLabel.setTextFill(Color.web("#fca5a5"));
        }
    }
}