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

// This class keeps all the arrays of messages and has the stored message features
public final class MessageStore {

    // every message in one list, in the same order as the ID and hash arrays
    private static final List<Message> allMessages = new ArrayList<>();

    // the five arrays the brief asks for
    private static final List<String> sentMessages = new ArrayList<>();
    private static final List<String> disregardedMessages = new ArrayList<>();
    private static final List<String> storedMessages = new ArrayList<>();
    private static final List<String> messageHashes = new ArrayList<>();
    private static final List<String> messageIDs = new ArrayList<>();

    private MessageStore() { }

    // puts a message into the right arrays depending on its flag
    public static void addMessage(Message m) {
        allMessages.add(m);
        messageIDs.add(m.getMessageID());
        messageHashes.add(m.getMessageHash());

        switch (m.getFlag()) {
            case "Sent" -> sentMessages.add(m.getMessage());
            case "Disregarded" -> disregardedMessages.add(m.getMessage());
            case "Stored" -> storedMessages.add(m.getMessage());
            default -> { }
        }
    }

    // reads the stored messages from the JSON file into the arrays
    // Source: Google (n.d.) Gson User Guide, https://github.com/google/gson
    public static void loadStoredMessages() {
        File file = new File(Message.getStorageFile());
        if (!file.exists() || file.length() == 0) {
            return;
        }
        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<List<Message>>() { }.getType();
            List<Message> list = new Gson().fromJson(reader, type);
            if (list != null) {
                for (Message m : list) {
                    addMessage(m);
                }
            }
        } catch (IOException | JsonSyntaxException e) {
            System.out.println("Could not read stored messages: " + e.getMessage());
        }
    }

    // shows the stored messages that were read from the JSON file
    public static String displayStoredMessages() {
        if (storedMessages.isEmpty()) {
            return "No stored messages found in the file.";
        }
        StringBuilder sb = new StringBuilder("Stored messages read from the file:\n");
        for (int i = 0; i < storedMessages.size(); i++) {
            sb.append(i + 1).append(") ").append(storedMessages.get(i)).append("\n");
        }
        return sb.toString().trim();
    }

    // a. shows the sender and recipient of every stored message
    public static String displaySenderAndRecipient(String sender) {
        StringBuilder sb = new StringBuilder();
        for (Message m : allMessages) {
            if ("Stored".equals(m.getFlag())) {
                sb.append("Sender: ").append(sender)
                  .append(" | Recipient: ").append(m.getRecipient()).append("\n");
            }
        }
        return sb.length() == 0 ? "No stored messages." : sb.toString().trim();
    }

    // b. finds the longest stored message
    public static String longestStoredMessage() {
        String longest = "";
        for (String text : storedMessages) {
            if (text.length() > longest.length()) {
                longest = text;
            }
        }
        return longest.isEmpty() ? "No stored messages." : longest;
    }

    // c. looks for a message by its ID, gives back null if it isn't there
    public static Message findMessageByID(String id) {
        int index = messageIDs.indexOf(id);
        return index >= 0 ? allMessages.get(index) : null;
    }

    // d. gets all the sent or stored messages for one recipient
    public static List<String> searchByRecipient(String recipient) {
        List<String> results = new ArrayList<>();
        for (Message m : allMessages) {
            boolean sentOrStored = "Sent".equals(m.getFlag())
                    || "Stored".equals(m.getFlag());
            if (sentOrStored && m.getRecipient().equals(recipient)) {
                results.add(m.getMessage());
            }
        }
        return results;
    }

    // e. deletes a message by its hash
    public static String deleteByHash(String hash) {
        int index = messageHashes.indexOf(hash);
        if (index < 0) {
            return "Message hash not found.";
        }

        // take it out of the ID, hash and message lists
        Message m = allMessages.get(index);
        allMessages.remove(index);
        messageIDs.remove(index);
        messageHashes.remove(index);

        // also take it out of the array that matches its flag
        switch (m.getFlag()) {
            case "Sent" -> sentMessages.remove(m.getMessage());
            case "Disregarded" -> disregardedMessages.remove(m.getMessage());
            case "Stored" -> {
                storedMessages.remove(m.getMessage());
                saveStoredToFile();
            }
            default -> { }
        }

        // the brief shows the text without its final full stop inside the quotes
        String shown = m.getMessage();
        if (shown.endsWith(".")) {
            shown = shown.substring(0, shown.length() - 1);
        }
        return "Message: \"" + shown + "\" successfully deleted.";
    }

    // f. makes a report with the full details of all messages with that flag
    public static String generateReport(String flag) {
        StringBuilder sb = new StringBuilder();
        for (Message m : allMessages) {
            if (flag.equals(m.getFlag())) {
                sb.append("Message Hash: ").append(m.getMessageHash())
                  .append("\nMessage ID: ").append(m.getMessageID())
                  .append("\nRecipient: ").append(m.getRecipient())
                  .append("\nMessage: ").append(m.getMessage())
                  .append("\n\n");
            }
        }
        return sb.length() == 0 ? "No " + flag.toLowerCase() + " messages." : sb.toString().trim();
    }

    // writes the stored messages that are left back to the JSON file
    private static void saveStoredToFile() {
        List<Message> remaining = new ArrayList<>();
        for (Message m : allMessages) {
            if ("Stored".equals(m.getFlag())) {
                remaining.add(m);
            }
        }
        try (FileWriter writer = new FileWriter(Message.getStorageFile())) {
            new GsonBuilder().setPrettyPrinting().create().toJson(remaining, writer);
        } catch (IOException e) {
            System.out.println("Could not update JSON file: " + e.getMessage());
        }
    }

    // these give back copies so nothing outside can change the real arrays
    public static List<String> getSentMessages() { return new ArrayList<>(sentMessages); }
    public static List<String> getDisregardedMessages() { return new ArrayList<>(disregardedMessages); }
    public static List<String> getStoredMessages() { return new ArrayList<>(storedMessages); }
    public static List<String> getMessageHashes() { return new ArrayList<>(messageHashes); }
    public static List<String> getMessageIDs() { return new ArrayList<>(messageIDs); }

    // empties everything, only used in the unit tests
    public static void reset() {
        allMessages.clear();
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
    }
}