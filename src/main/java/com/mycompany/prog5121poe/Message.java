package com.mycompany.prog5121poe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public final class Message {

    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static final String STORAGE_FILE = "messages.json";

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String message;
    private final String messageHash;
    private String flag;

    public Message(String messageID, int messageNumber,
                   String recipient, String message) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.message = message;
        this.messageHash = createMessageHash();
        this.flag = "Pending";
    }

    /** Randomly generated ten-digit message ID. */
    public static String generateMessageID() {
        return String.format("%010d",
                ThreadLocalRandom.current().nextLong(10_000_000_000L));
    }

    public boolean checkMessageID() {
        return messageID != null && messageID.matches("[0-9]{10}");
    }

    public String getMessageIDResult() {
        return checkMessageID()
                ? "Message ID generated: " + messageID
                : "Message ID is invalid.";
    }

    public String checkRecipientCell() {
        if (recipient != null && recipient.matches("^\\+27[0-9]{9}$")) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not "
                + "contain an international code. Please correct the "
                + "number and try again.";
    }

    public boolean checkMessageLength() {
        return message != null && message.length() <= 250;
    }

    public String getMessageLengthMessage() {
        if (message == null) {
            return "Please enter a message of less than 250 characters.";
        }
        if (message.length() > 250) {
            return "Message exceeds 250 characters by "
                    + (message.length() - 250)
                    + "; please reduce the size.";
        }
        return "Message ready to send.";
    }

    public String createMessageHash() {
        if (messageID == null || messageID.length() < 2) {
            return "";
        }

        String firstTwoIDDigits = messageID.substring(0, 2);
        String trimmedMessage = message == null ? "" : message.trim();

        if (trimmedMessage.isEmpty()) {
            return firstTwoIDDigits + ":" + messageNumber + ":";
        }

        String[] words = trimmedMessage.split("\\s+");

        String firstWord = words[0].replaceAll(
                "^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");
        String lastWord = words[words.length - 1].replaceAll(
                "^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");

        return (firstTwoIDDigits + ":" + messageNumber + ":"
                + firstWord + lastWord).toUpperCase(Locale.ROOT);
    }

    /** 1 = send, 2 = disregard, 3 = store for later. */
    public String SentMessage(int choice) {
        switch (choice) {
            case 1 -> {
                flag = "Sent";
                sentMessages.add(this);
                return "Message successfully sent.";
            }
            case 2 -> {
                flag = "Disregarded";
                return "Press 0 to delete the message.";
            }
            case 3 -> {
                flag = "Stored";
                storeMessage();
                return "Message successfully stored.";
            }
            default -> {
                return "Invalid option.";
            }
        }
    }

    /** Details of ONE message: ID, Hash, Recipient, Message. */
    public String printMessage() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + message;
    }

    /** All messages sent while the program is running. */
    public static String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.printMessage()).append("\n\n");
        }
        return sb.toString().trim();
    }

    /** Total number of messages sent. */
    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    /** Saves all stored messages to messages.json. */
    public void storeMessage() {
        storedMessages.add(this);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(STORAGE_FILE)) {
            gson.toJson(storedMessages, writer);
        } catch (IOException e) {
            System.out.println("Could not store message: " + e.getMessage());
        }
    }

    /** Clears saved lists (used by unit tests). */
    public static void resetAll() {
        sentMessages.clear();
        storedMessages.clear();
    }

    public String getMessageID() { return messageID; }
    public int getMessageNumber() { return messageNumber; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public String getMessageHash() { return messageHash; }
    public String getFlag() { return flag; }
    public void setFlag(String flag) { this.flag = flag; }
}