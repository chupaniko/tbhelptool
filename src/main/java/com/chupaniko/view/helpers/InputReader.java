package com.chupaniko.view.helpers;

import java.util.Scanner;

/**
 * Gets user input from console.
 *
 * @author Nikolai Chupakhin
 * @version 1.0
 * @since 1.0
 */
public class InputReader {

    /**
     * Object for processing user input from console.
     */
    private Scanner scanner;

    /**
     * Creates an input reader.
     *
     * @param scanner Object for processing user input from console.
     */
    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Gets a line of user console input.
     *
     * @return String of user console input.
     */
    public String getUserInput() {
        String userInput = "";


        userInput = scanner.nextLine();
        return userInput;
    }
}
