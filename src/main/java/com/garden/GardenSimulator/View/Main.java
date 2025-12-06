package com.garden.GardenSimulator.View;


import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;
import javafx.application.Application;


public class Main {
    public static void main(String[] args) {
        String logFile = "GardenSimulator_Logs.txt";
        try {
            Files.newBufferedWriter(Paths.get(logFile)).close();
            System.out.println("Welcome to our Garden!! :) \n");

            System.out.println(
                    "Before you start the garden simulation:\n" +
                            "  - Select the plants you want to cultivate.\n" +
                            "  - Press \"Start Simulation\" to begin.\n" +
                            "  - Watch how the garden evolves over time.\n" +
                            "  - Garden metrics and the weather forecast are shown on the right.\n" +
                            "  - Click \"Show Logs\" to review detailed events.\n" +
                            "  - Happy gardening!\n"
            );

            System.out.println("Preparing a fresh run: clearing any previous log entries...");


        } catch (IOException exception) {
            System.err.println("An unexpected error occurred while clearing the old log file: " + exception.getMessage() + " Please try running again");
        }
        initiateApplication(args);
    }

    private static void initiateApplication(String[] args) {
        Application.launch(UserInterface.class, args);
    }
}
