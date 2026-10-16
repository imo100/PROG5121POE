package com.mycompany.prog5121poe;

import java.util.Locale;

/**
 * Represents a message created in QuickChat.
 */
public final class Message {

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

    public boolean checkMessageID() {
        return messageID != null
                && messageID.matches("[0-9]{10}");
    }

    public String checkRecipientCell() {
        if (recipient != null
                && recipient.matches("^\\+27[0-9]{9}$")) {
            return "Cell phone number successfully captured.";
        }

        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    public boolean checkMessageLength() {
        return message != null && message.length() <= 250;
    }

    public String getMessageLengthMessage() {
        if (message == null) {
            return "Please enter a message.";
        }

        if (message.length() > 250) {
            return "Message exceeds 250 characters by "
                    + (message.length() - 250)
                    + "; please reduce the size.";
        }

        return "Message ready to send.";
    }

    /**
     * Hash format: first two ID digits:message number:first and last words.
     * Example: 00:0:HITONIGHT
     * @return 
     */
    public String createMessageHash() {
        if (messageID == null || messageID.length() < 2
                || message == null || message.trim().isEmpty()) {
            return "";
        }

        String[] words = message.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "");
        String lastWord = words[words.length - 1]
                .replaceAll("[^a-zA-Z0-9]", "");

        return messageID.substring(0, 2)
                + ":" + messageNumber
                + ":" + (firstWord + lastWord).toUpperCase(Locale.ROOT);
    }

    /**
     * Records the user's choice for the message.
     * @param choice
     * @return 
     */
    public String SentMessage(int choice) {
        switch (choice) {
            case 1 -> {
                flag = "Sent";
                return "Message successfully sent.";
            }

            case 2 -> {
                flag = "Disregarded";
                return "Press 0 to delete the message.";
            }

            case 3 -> {
                flag = "Stored";
                return "Message successfully stored.";
            }

            default -> {
                return "Invalid message option.";
            }
        }
    }

    public String printMessages() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + message;
    }

    public String getMessageID() {
        return messageID;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}