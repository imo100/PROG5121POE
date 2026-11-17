package com.mycompany.prog5121poe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

// This class stores one message and does all the checks on it
public final class Message {

    // keeps every message that was sent while the program is running
    private static final List<Message> sentMessages = new ArrayList<>();

    // keeps every message that was stored in this run
    private static final List<Message> storedMessages = new ArrayList<>();

    // the JSON file the stored messages get saved to
    private static String storageFile = "messages.json";

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String message;
    private final String messageHash;
    private String flag;    // Pending, Sent, Disregarded, Stored or Deleted

    public Message(String messageID, int messageNumber,
                   String recipient, String message) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.message = message;
        this.messageHash = createMessageHash();
        this.flag = "Pending";
    }

    // makes a random 10 digit ID by adding one random digit each time the loop runs
    public static String generateMessageID() {
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(ThreadLocalRandom.current().nextInt(10));
        }
        return id.toString();
    }

    // the ID is only valid if it is exactly 10 digits
    public boolean checkMessageID() {
        return messageID != null && messageID.matches("[0-9]{10}");
    }

    public String getMessageIDResult() {
        return checkMessageID()
                ? "Message ID generated: " + messageID
                : "Message ID is invalid.";
    }

    // the number must start with +27 and have 9 more digits after it
    public String checkRecipientCell() {
        if (recipient != null && recipient.matches("^\\+27[0-9]{9}$")) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not "
                + "contain an international code. Please correct the "
                + "number and try again.";
    }

    // the message can't be longer than 250 characters
    public boolean checkMessageLength() {
        return message != null && message.length() <= 250;
    }

    // tells the user if the message is fine or how many characters it is over by
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

    // builds the hash, for example 00:0:HITONIGHT
    public String createMessageHash() {
        if (messageID == null || messageID.length() < 2) {
            return "";
        }

        // first two digits of the ID
        String firstTwoIDDigits = messageID.substring(0, 2);
        String trimmedMessage = message == null ? "" : message.trim();

        if (trimmedMessage.isEmpty()) {
            return firstTwoIDDigits + ":" + messageNumber + ":";
        }

        // split the message into words so I can get the first and last one
        String[] words = trimmedMessage.split("\\s+");

        // take away any punctuation like commas and question marks
        String firstWord = words[0].replaceAll(
                "^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");
        String lastWord = words[words.length - 1].replaceAll(
                "^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");

        return (firstTwoIDDigits + ":" + messageNumber + ":"
                + firstWord + lastWord).toUpperCase(Locale.ROOT);
    }

    // 1 = send, 2 = disregard, 3 = store the message for later
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

    // marks the message as deleted and takes it out of the lists
    public String deleteMessage() {
        flag = "Deleted";
        sentMessages.remove(this);
        storedMessages.remove(this);
        return "Message deleted.";
    }

    // shows one message in the order ID, hash, recipient, message
    public String printMessage() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + message;
    }

    // shows all the messages that were sent
    public static String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.printMessage()).append("\n\n");
        }
        return sb.toString().trim();
    }

    // how many messages have been sent so far
    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    // the unit tests use this so they don't change the real JSON file
    public static void setStorageFile(String path) {
        storageFile = path;
    }

    public static String getStorageFile() {
        return storageFile;
    }

    // saves the message to the JSON file and keeps the ones already in there
    // Source: Google (n.d.) Gson User Guide, https://github.com/google/gson
    public void storeMessage() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // read the old messages first so they don't get overwritten
        List<Message> all = loadStoredMessages(gson);
        all.add(this);
        storedMessages.add(this);

        try (FileWriter writer = new FileWriter(storageFile)) {
            gson.toJson(all, writer);
        } catch (IOException e) {
            System.out.println("Could not store message: " + e.getMessage());
        }
    }

    // reads the JSON file, or gives back an empty list if the file is missing,
    // empty or has bad JSON in it
    private static List<Message> loadStoredMessages(Gson gson) {
        File file = new File(storageFile);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<List<Message>>() { }.getType();
            List<Message> existing = gson.fromJson(reader, type);
            return existing == null ? new ArrayList<>() : existing;
        } catch (IOException | JsonSyntaxException e) {
            System.out.println("Existing JSON was invalid; starting a new file.");
            return new ArrayList<>();
        }
    }

    // clears the lists, only used in the unit tests
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