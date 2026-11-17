package com.mycompany.prog5121poe;

import static org.junit.Assert.*;
import java.io.File;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

// Unit tests for MessageStore using the test data from Part 3
public class MessageStoreTest {

    // the text of the five test messages
    private static final String T1 = "Did you get the cake?";
    private static final String T2 = "Where are you? You are late! I have asked you to be on time.";
    private static final String T3 = "Yohoooo, I am at your gate.";
    private static final String T4 = "It is dinner time!";
    private static final String T5 = "Ok, I am leaving without you.";

    // runs before every test and sets up the five messages
    @Before
    public void setUp() {
        Message.resetAll();
        MessageStore.reset();

        // use a test file so the real messages.json doesn't get changed
        Message.setStorageFile("target/test-store.json");
        new File("target/test-store.json").delete();

        Message m1 = new Message("1000000001", 0, "+27834557896", T1);
        Message m2 = new Message("2000000002", 1, "+27838884567", T2);
        Message m3 = new Message("3000000003", 2, "+27834484567", T3);
        Message m4 = new Message("0838884567", 3, "+27838884000", T4);
        Message m5 = new Message("5000000005", 4, "+27838884567", T5);

        m1.SentMessage(1);   // sent
        m2.SentMessage(3);   // stored
        m3.SentMessage(2);   // disregard
        m4.SentMessage(1);   // sent
        m5.SentMessage(3);   // stored

        MessageStore.addMessage(m1);
        MessageStore.addMessage(m2);
        MessageStore.addMessage(m3);
        MessageStore.addMessage(m4);
        MessageStore.addMessage(m5);
    }

    // the sent array should have messages 1 and 4
    @Test
    public void sentMessagesArrayPopulated() {
        assertEquals(Arrays.asList(T1, T4), MessageStore.getSentMessages());
    }

    // the disregarded array should have message 3
    @Test
    public void disregardedMessagesArrayPopulated() {
        assertEquals(Arrays.asList(T3), MessageStore.getDisregardedMessages());
    }

    // there should be one hash and one ID for each of the 5 messages
    @Test
    public void hashAndIDArraysPopulated() {
        assertEquals(5, MessageStore.getMessageHashes().size());
        assertEquals(5, MessageStore.getMessageIDs().size());
        assertTrue(MessageStore.getMessageHashes().contains("20:1:WHERETIME"));
        assertTrue(MessageStore.getMessageIDs().contains("0838884567"));
    }

    // message 2 is the longest stored message
    @Test
    public void longestMessage() {
        assertEquals(T2, MessageStore.longestStoredMessage());
    }

    // searching for ID 0838884567 should find message 4
    @Test
    public void searchForMessageID() {
        Message found = MessageStore.findMessageByID("0838884567");
        assertNotNull(found);
        assertEquals(T4, found.getMessage());
    }

    // +27838884567 has messages 2 and 5
    @Test
    public void searchByRecipient() {
        assertEquals(Arrays.asList(T2, T5),
                MessageStore.searchByRecipient("+27838884567"));
    }

    // deleting message 2 using its hash
    @Test
    public void deleteByHash() {
        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time\" successfully deleted.",
                MessageStore.deleteByHash("20:1:WHERETIME"));
        assertEquals(Arrays.asList(T5), MessageStore.getStoredMessages());
    }

    // a hash that doesn't exist shouldn't delete anything
    @Test
    public void deleteUnknownHash() {
        assertEquals("Message hash not found.", MessageStore.deleteByHash("99:9:NONE"));
    }

    // the report should show the hash, recipient and message
    @Test
    public void displayReport() {
        String report = MessageStore.generateReport("Sent");
        assertTrue(report.contains("Message Hash: 10:0:DIDCAKE"));
        assertTrue(report.contains("Recipient: +27834557896"));
        assertTrue(report.contains("Message: " + T1));
        assertTrue(report.contains("Message: " + T4));
    }

    // the stored messages should be read back from the JSON file
    @Test
    public void storedMessagesLoadedFromJson() {
        MessageStore.reset();
        MessageStore.loadStoredMessages();
        assertEquals(Arrays.asList(T2, T5), MessageStore.getStoredMessages());
    }

    // the stored messages read from the file should be shown to the user
    @Test
    public void displayStoredMessages() {
        String shown = MessageStore.displayStoredMessages();
        assertTrue(shown.contains(T2));
        assertTrue(shown.contains(T5));
    }
}