package com.mycompany.prog5121poe;

import java.util.Scanner;

/**
 * Runs the QuickChat menu after a user has logged in successfully.
 */
public class QuickChat {

    /**
     * Shows the welcome message and the main menu. The while loop keeps
     * the application running until the user chooses to quit.
     *
     * @param scanner the shared Scanner used for all user input
     */
    public static void start(Scanner scanner) {
        System.out.println("Welcome to QuickChat.");

        // The user decides at the start how many messages they may enter
        int limit = readInt(scanner, "How many messages do you wish to enter? ", 1);
        boolean messagesEntered = false;  // true once all messages are entered
        boolean running = true;           // the while loop runs until Quit

        while (running) {
            System.out.println("\n1) Send Messages\n2) Show recently sent messages\n3) Quit");
            int option = readInt(scanner, "Choose an option: ", 1);

            switch (option) {
                case 1 -> {
                    if (messagesEntered) {
                        System.out.println("You have already entered all "
                                + limit + " messages.");
                    } else {
                        // For loop: runs exactly as many times as the user chose.
                        // The loop counter i is the message number (starts at 0).
                        for (int i = 0; i < limit; i++) {
                            System.out.println("\nMessage " + (i + 1) + " of " + limit);
                            createAndHandleMessage(scanner, i);
                        }
                        messagesEntered = true;
                        System.out.println("\nTotal messages sent: "
                                + Message.returnTotalMessages());
                    }
                }
                // Feature still in development
                case 2 -> System.out.println("Coming Soon.");
                case 3 -> {
                    System.out.println("Goodbye.");
                    running = false;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    /**
     * Collects one message from the user, validates it, and lets them
     * send, disregard or store it.
     *
     * @param scanner the shared Scanner
     * @param number  the message number (the for loop counter)
     */
    private static void createAndHandleMessage(Scanner scanner, int number) {
        // Generate a random ten-digit ID for this message
        String id = Message.generateMessageID();

        // Keep asking until the recipient number is valid
        String recipient;
        while (true) {
            System.out.print("Recipient cell number: ");
            recipient = scanner.nextLine().trim();
            String result = new Message(id, number, recipient, "x").checkRecipientCell();
            System.out.println(result);
            if (result.startsWith("Cell phone number successfully")) break;
        }

        // Keep asking until the message is 250 characters or fewer
        String text;
        while (true) {
            System.out.print("Message: ");
            text = scanner.nextLine();
            if (new Message(id, number, recipient, text).checkMessageLength()) {
                System.out.println("Message sent");
                break;
            }
            System.out.println("Please enter a message of less than 250 characters.");
        }

        // Build the final message and ask what to do with it
        Message msg = new Message(id, number, recipient, text);

        System.out.println("1) Send Message  2) Disregard Message  3) Store Message to send later");
        int choice = readInt(scanner, "Choose: ", 1);
        System.out.println(msg.SentMessage(choice));

        if (choice == 2) {
            // Disregarded messages are deleted when the user presses 0
            int zero = readInt(scanner, "", 0);
            if (zero == 0) System.out.println("Message deleted.");
        } else if (choice == 1) {
            // Show the full details of a sent message
            System.out.println(msg.printMessage());
        }
    }

    /**
     * Reads a whole number, repeating until it is valid and at least min.
     *
     * @param scanner the shared Scanner
     * @param prompt  the text shown to the user
     * @param min     the smallest accepted value
     * @return the valid number entered
     */
    private static int readInt(Scanner scanner, String prompt, int min) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min) return value;
            } catch (NumberFormatException ignored) {
                // Not a number: fall through and ask again
            }
            System.out.println("Please enter a valid number.");
        }
    }
}