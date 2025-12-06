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

            System.out.println("Points to remember before you simulate the garden:\n" +
                    "   - Select the plants you want to cultivate.\n" +
                    "   - Press \"Start Simulation\" to begin.\n" +
                    "   - Observe the simulation process.\n" +
                    "   - Garden metrics and the weather forecast are on the right side of the screen.\n" +
                    "   - View logs by clicking on \"Show Logs\".\n" +
                    "   - Happy Gardening! \n");

            System.out.println("We are clearing the content of old log file for you!!!");

        } catch (IOException exception) {
            System.err.println("An unexpected error occurred while clearing the old log file: " + exception.getMessage() + " Please try running again");
        }
        initiateApplication(args);
    }

    private static void initiateApplication(String[] args) {
        Application.launch(UserInterface.class, args);
    }
}
