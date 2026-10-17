package com.mycompany.prog5121poe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents one message in QuickChat. Validates the recipient and message
 * length, builds the message ID and hash, and tracks sent and stored messages.
 */
public final class Message {

    // Messages sent while the program is running
    private static final List<Message> sentMessages = new ArrayList<>();

    // Messages stored to send later (also written to the JSON file)
    private static final List<Message> storedMessages = new ArrayList<>();

    // File the stored messages are saved to
    private static final String STORAGE_FILE = "messages.json";

    private final String messageID;      // ten-digit unique ID
    private final int messageNumber;     // number of the message (loop counter)
    private final String recipient;      // recipient cell number
    private final String message;        // the message text
    private final String messageHash;    // e.g. 00:0:HITONIGHT
    private String flag;                 // Pending, Sent, Disregarded or Stored

    /**
     * Creates a message and generates its hash.
     *
     * @param messageID     the ten-digit message ID
     * @param messageNumber the message number
     * @param recipient     the recipient's cell number
     * @param message       the message text
     */
    public Message(String messageID, int messageNumber,
                   String recipient, String message) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.message = message;
        this.messageHash = createMessageHash();
        this.flag = "Pending";
    }

    /**
     * Builds a random ten-digit message ID, adding one digit per loop pass.
     *
     * @return the ten-digit ID as a String
     */
    public static String generateMessageID() {
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(ThreadLocalRandom.current().nextInt(10));
        }
        return id.toString();
    }

    /**
     * Checks that the message ID is exactly ten digits.
     *
     * @return true if the ID is valid
     */
    public boolean checkMessageID() {
        return messageID != null && messageID.matches("[0-9]{10}");
    }

    /**
     * Returns the message shown when an ID has been created.
     *
     * @return "Message ID generated: ID" or an error message
     */
    public String getMessageIDResult() {
        return checkMessageID()
                ? "Message ID generated: " + messageID
                : "Message ID is invalid.";
    }

    /**
     * Checks that the recipient starts with +27 followed by nine digits.
     *
     * @return a success message or an error message
     */
    public String checkRecipientCell() {
        if (recipient != null && recipient.matches("^\\+27[0-9]{9}$")) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not "
                + "contain an international code. Please correct the "
                + "number and try again.";
    }

    /**
     * Checks that the message is no longer than 250 characters.
     *
     * @return true if the message length is valid
     */
    public boolean checkMessageLength() {
        return message != null && message.length() <= 250;
    }

    /**
     * Returns the length result message, including by how much the
     * message is too long.
     *
     * @return a success or failure message
     */
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

    /**
     * Creates the message hash: first two ID digits, message number,
     * then the first and last words in capitals (e.g. 00:0:HITONIGHT).
     *
     * @return the message hash
     */
    public String createMessageHash() {
        if (messageID == null || messageID.length() < 2) {
            return "";
        }

        // First two digits of the ID
        String firstTwoIDDigits = messageID.substring(0, 2);
        String trimmedMessage = message == null ? "" : message.trim();

        if (trimmedMessage.isEmpty()) {
            return firstTwoIDDigits + ":" + messageNumber + ":";
        }

        // Split the message into words, then take the first and last
        String[] words = trimmedMessage.split("\\s+");

        // Remove punctuation from the start and end of each word
        String firstWord = words[0].replaceAll(
                "^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");
        String lastWord = words[words.length - 1].replaceAll(
                "^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");

        return (firstTwoIDDigits + ":" + messageNumber + ":"
                + firstWord + lastWord).toUpperCase(Locale.ROOT);
    }

    /**
     * Applies the user's choice to the message.
     *
     * @param choice 1 = send, 2 = disregard, 3 = store to send later
     * @return the confirmation message for that choice
     */
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

    /**
     * Returns the details of this message in the required order:
     * ID, Hash, Recipient, Message.
     *
     * @return the formatted message details
     */
    public String printMessage() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + message;
    }

    /**
     * Returns the details of all messages sent while the program runs.
     *
     * @return all sent messages, separated by blank lines
     */
    public static String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.printMessage()).append("\n\n");
        }
        return sb.toString().trim();
    }

    /**
     * Returns the total number of messages sent.
     *
     * @return the number of sent messages
     */
    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    /**
     * Saves all stored messages to messages.json.
     * Source: Google (n.d.) Gson User Guide. Available at:
     * https://github.com/google/gson (Accessed: 17 October 2026).
     */
    public void storeMessage() {
        storedMessages.add(this);

        // Gson converts the list of Message objects into JSON text
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(STORAGE_FILE)) {
            gson.toJson(storedMessages, writer);
        } catch (IOException e) {
            System.out.println("Could not store message: " + e.getMessage());
        }
    }

    /** Clears the sent and stored lists (used by the unit tests). */
    public static void resetAll() {
        sentMessages.clear();
        storedMessages.clear();
    }

    // this is the Getters and setter
    public String getMessageID() { return messageID; }
    public int getMessageNumber() { return messageNumber; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public String getMessageHash() { return messageHash; }
    public String getFlag() { return flag; }
    public void setFlag(String flag) { this.flag = flag; }
}