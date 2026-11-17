package com.mycompany.prog5121poe;

import java.util.List;
import java.util.Scanner;

// This class runs the QuickChat menu once the user has logged in
public class QuickChat {

    public static void start(Scanner scanner, String senderName) {
        System.out.println("Welcome to QuickChat.");

        // load the stored messages from the JSON file into the arrays and show them
        MessageStore.loadStoredMessages();
        System.out.println(MessageStore.displayStoredMessages());

        // the user decides how many messages they want to enter
        int limit = readInt(scanner, "How many messages do you wish to enter? ", 1);
        boolean messagesEntered = false;
        boolean running = true;

        // this while loop keeps the menu going until the user picks Quit
        while (running) {
            System.out.println("\n1) Send Messages\n2) Show recently sent messages\n3) Quit\n4) Stored Messages");
            int option = readInt(scanner, "Choose an option: ", 1);

            switch (option) {
                case 1 -> {
                    if (messagesEntered) {
                        System.out.println("You have already entered all "
                                + limit + " messages.");
                    } else {
                        // the for loop runs once for each message, and i is the message number
                        for (int i = 0; i < limit; i++) {
                            System.out.println("\nMessage " + (i + 1) + " of " + limit);
                            createAndHandleMessage(scanner, i);
                        }
                        messagesEntered = true;
                        System.out.println("\nTotal messages sent: "
                                + Message.returnTotalMessages());
                    }
                }
                case 2 -> System.out.println("Coming Soon.");
                case 3 -> {
                    System.out.println("Goodbye.");
                    running = false;
                }
                case 4 -> storedMessagesMenu(scanner, senderName);
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // the menu for the stored messages option
    private static void storedMessagesMenu(Scanner scanner, String senderName) {
        boolean back = false;
        while (!back) {
            System.out.println("\nSTORED MESSAGES");
            System.out.println("1) Display sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for all messages for a recipient");
            System.out.println("5) Delete a message using its hash");
            System.out.println("6) Display a report of all stored messages");
            System.out.println("0) Back");
            int choice = readInt(scanner, "Choose an option: ", 0);

            switch (choice) {
                case 1 -> System.out.println(MessageStore.displaySenderAndRecipient(senderName));
                case 2 -> System.out.println(MessageStore.longestStoredMessage());
                case 3 -> {
                    System.out.print("Enter the message ID: ");
                    Message found = MessageStore.findMessageByID(scanner.nextLine().trim());
                    if (found == null) {
                        System.out.println("Message ID not found.");
                    } else {
                        System.out.println("Recipient: " + found.getRecipient());
                        System.out.println("Message: " + found.getMessage());
                    }
                }
                case 4 -> {
                    System.out.print("Enter the recipient number: ");
                    List<String> results = MessageStore.searchByRecipient(scanner.nextLine().trim());
                    if (results.isEmpty()) {
                        System.out.println("No messages found for that recipient.");
                    } else {
                        for (String text : results) {
                            System.out.println(text);
                        }
                    }
                }
                case 5 -> {
                    System.out.print("Enter the message hash: ");
                    System.out.println(MessageStore.deleteByHash(scanner.nextLine().trim()));
                }
                case 6 -> System.out.println(MessageStore.generateReport("Stored"));
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // asks for one message, checks it, then lets the user send, disregard or store it
    private static void createAndHandleMessage(Scanner scanner, int number) {
        String id = Message.generateMessageID();

        // keep asking until the number is valid
        String recipient;
        while (true) {
            System.out.print("Recipient cell number: ");
            recipient = scanner.nextLine().trim();
            String result = new Message(id, number, recipient, "x").checkRecipientCell();
            System.out.println(result);
            if (result.startsWith("Cell phone number successfully")) break;
        }

        // keep asking until the message is 250 characters or less
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

        Message msg = new Message(id, number, recipient, text);

        System.out.println("1) Send Message  2) Disregard Message  3) Store Message to send later");
        int choice = readInt(scanner, "Choose: ", 1);
        System.out.println(msg.SentMessage(choice));

        // save the message in the arrays (sent, disregarded or stored)
        if (choice >= 1 && choice <= 3) {
            MessageStore.addMessage(msg);
        }

        if (choice == 2) {
            // the message only gets deleted when the user presses 0
            int zero = readInt(scanner, "", 0);
            if (zero == 0) System.out.println(msg.deleteMessage());
        } else if (choice == 1) {
            // show the full details of the message that was sent
            System.out.println(msg.printMessage());
        }
    }

    // keeps asking until the user types a whole number that is at least min
    private static int readInt(Scanner scanner, String prompt, int min) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min) return value;
            } catch (NumberFormatException ignored) {
                // not a number, so just ask again
            }
            System.out.println("Please enter a valid number.");
        }
    }
}