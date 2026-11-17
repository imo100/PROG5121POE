package com.mycompany.prog5121poe;

import static org.junit.Assert.*;
import java.io.File;
import org.junit.Before;
import org.junit.Test;

// Unit tests for the Message class using the test data from the brief
public class MessageTest {

    private Message msg1, msg2;

    // runs before every test and creates the two test messages
    @Before
    public void setUp() {
        Message.resetAll();

        // use a test file so the real messages.json doesn't get changed
        Message.setStorageFile("target/test-messages.json");
        new File("target/test-messages.json").delete();

        // message 1 has a valid number and gets sent
        msg1 = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        // message 2 has no international code and gets discarded
        msg2 = new Message("1234567890", 1, "08575975889",
                "Hi Keegan, did you receive the payment?");
    }

    // a short message should be ready to send
    @Test
    public void messageLengthSuccess() {
        assertEquals("Message ready to send.", msg1.getMessageLengthMessage());
    }

    // 260 characters is 10 over the limit
    @Test
    public void messageLengthFailure() {
        Message longMsg = new Message("0012345678", 0, "+27718693002", "a".repeat(260));
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                longMsg.getMessageLengthMessage());
    }

    // this number has +27 so it should be accepted
    @Test
    public void recipientCorrectlyFormatted() {
        assertEquals("Cell phone number successfully captured.", msg1.checkRecipientCell());
    }

    // this number has no international code so it should be rejected
    @Test
    public void recipientIncorrectlyFormatted() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                msg2.checkRecipientCell());
    }

    // the hash for message 1 should be 00:0:HITONIGHT
    @Test
    public void messageHashTestCase1() {
        assertEquals("00:0:HITONIGHT", msg1.createMessageHash());
    }

    // checks the hash of every test message in a loop
    @Test
    public void allMessageHashesInLoop() {
        Message[] messages = {msg1, msg2};
        String[] expected = {"00:0:HITONIGHT", "12:1:HIPAYMENT"};
        for (int i = 0; i < messages.length; i++) {
            assertEquals(expected[i], messages[i].createMessageHash());
        }
    }

    // a generated ID should be 10 digits
    @Test
    public void messageIDCreated() {
        String id = Message.generateMessageID();
        Message m = new Message(id, 0, "+27718693002", "Hello there");
        assertTrue(m.checkMessageID());
        assertEquals("Message ID generated: " + id, m.getMessageIDResult());
    }

    // an ID with less than 10 digits should be rejected
    @Test
    public void messageIDTooShort() {
        Message m = new Message("123456789", 0, "+27718693002", "Hello there");
        assertFalse(m.checkMessageID());
    }

    // an ID with more than 10 digits should be rejected
    @Test
    public void messageIDTooLong() {
        Message m = new Message("12345678901", 0, "+27718693002", "Hello there");
        assertFalse(m.checkMessageID());
    }

    // choice 1 sends the message
    @Test
    public void sendMessage() {
        assertEquals("Message successfully sent.", msg1.SentMessage(1));
    }

    // choice 2 disregards the message
    @Test
    public void disregardMessage() {
        assertEquals("Press 0 to delete the message.", msg2.SentMessage(2));
    }

    // after pressing 0 the message should be marked as Deleted
    @Test
    public void deleteMessageSetsState() {
        msg2.SentMessage(2);
        assertEquals("Message deleted.", msg2.deleteMessage());
        assertEquals("Deleted", msg2.getFlag());
    }

    // choice 3 stores the message for later
    @Test
    public void storeMessage() {
        assertEquals("Message successfully stored.", msg1.SentMessage(3));
    }

    // message 1 is sent and message 2 is discarded, so the total is 1
    @Test
    public void totalMessagesReturned() {
        msg1.SentMessage(1);
        msg2.SentMessage(2);
        assertEquals(1, Message.returnTotalMessages());
    }
}