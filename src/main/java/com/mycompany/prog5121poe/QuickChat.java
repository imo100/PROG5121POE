package com.mycompany.prog5121poe;

import java.util.Scanner;

public class QuickChat {

    public static void start(Scanner scanner) {
        System.out.println("Welcome to QuickChat.");

        int limit = readInt(scanner, "How many messages do you wish to enter? ", 1);
        int created = 0;
        boolean running = true;

        while (running) {
            System.out.println("\n1) Send Messages\n2) Show recently sent messages\n3) Quit");
            int option = readInt(scanner, "Choose an option: ", 1);

            switch (option) {
                case 1 -> {
                    if (created >= limit) {
                        System.out.println("You have entered all " + limit + " messages.");
                    } else {
                        createAndHandleMessage(scanner, created);
                        created++;
                    }
                }
                case 2 -> System.out.println("Coming Soon.");
                case 3 -> {
                    System.out.println("Total messages sent: " + Message.returnTotalMessages());
                    running = false;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void createAndHandleMessage(Scanner scanner, int number) {
        String id = Message.generateMessageID();

        String recipient;
        while (true) {
            System.out.print("Recipient cell number: ");
            recipient = scanner.nextLine().trim();
            Message probe = new Message(id, number, recipient, "x");
            String result = probe.checkRecipientCell();
            System.out.println(result);
            if (result.startsWith("Cell phone number successfully")) break;
        }

        String text;
        while (true) {
            System.out.print("Message: ");
            text = scanner.nextLine();
            Message probe = new Message(id, number, recipient, text);
            if (probe.checkMessageLength()) {
                System.out.println("Message sent");
                break;
            }
            System.out.println("Please enter a message of less than 250 characters.");
        }

        Message msg = new Message(id, number, recipient, text);

        System.out.println("1) Send Message  2) Disregard Message  3) Store Message to send later");
        int choice = readInt(scanner, "Choose: ", 1);
        System.out.println(msg.SentMessage(choice));

        if (choice == 2) {
            int zero = readInt(scanner, "", 0);
            if (zero == 0) System.out.println("Message deleted.");
        } else if (choice == 1) {
            System.out.println(msg.printMessage());
        }
    }

    private static int readInt(Scanner scanner, String prompt, int min) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min) return value;
            } catch (NumberFormatException ignored) { }
            System.out.println("Please enter a valid number.");
        }
    }
}
