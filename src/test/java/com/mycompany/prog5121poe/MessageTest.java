package com.mycompany.prog5121poe;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Message class, using the test data from the brief.
 */
public class MessageTest {

    private Message msg1, msg2;

    // Runs before every test: clears old data and creates the two test messages
    @Before
    public void setUp() {
        Message.resetAll();
        // Test message 1: valid number, will be sent
        msg1 = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        // Test message 2: invalid number (no international code), will be discarded
        msg2 = new Message("1234567890", 1, "08575975889",
                "Hi Keegan, did you receive the payment?");
    }

    // Message under 250 characters should be ready to send
    @Test
    public void messageLengthSuccess() {
        assertEquals("Message ready to send.", msg1.getMessageLengthMessage());
    }

    // Message of 260 characters is 10 over the limit
    @Test
    public void messageLengthFailure() {
        Message longMsg = new Message("0012345678", 0, "+27718693002", "a".repeat(260));
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                longMsg.getMessageLengthMessage());
    }

    // +27718693002 has the international code, so it should be accepted
    @Test
    public void recipientCorrectlyFormatted() {
        assertEquals("Cell phone number successfully captured.", msg1.checkRecipientCell());
    }

    // 08575975889 has no international code, so it should be rejected
    @Test
    public void recipientIncorrectlyFormatted() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                msg2.checkRecipientCell());
    }

    // Hash for test message 1 should be 00:0:HITONIGHT
    @Test
    public void messageHashTestCase1() {
        assertEquals("00:0:HITONIGHT", msg1.createMessageHash());
    }

    // Checks the hash of every test message in a loop
    @Test
    public void allMessageHashesInLoop() {
        Message[] messages = {msg1, msg2};
        String[] expected = {"00:0:HITONIGHT", "12:1:HIPAYMENT"};
        for (int i = 0; i < messages.length; i++) {
            assertEquals(expected[i], messages[i].createMessageHash());
        }
    }

    // A generated ID should be ten digits and give the correct message
    @Test
    public void messageIDCreated() {
        String id = Message.generateMessageID();
        Message m = new Message(id, 0, "+27718693002", "Hello there");
        assertTrue(m.checkMessageID());
        assertEquals("Message ID generated: " + id, m.getMessageIDResult());
    }

    // Choice 1 = send
    @Test
    public void sendMessage() {
        assertEquals("Message successfully sent.", msg1.SentMessage(1));
    }

    // Choice 2 = disregard
    @Test
    public void disregardMessage() {
        assertEquals("Press 0 to delete the message.", msg2.SentMessage(2));
    }

    // Choice 3 = store to send later
    @Test
    public void storeMessage() {
        assertEquals("Message successfully stored.", msg1.SentMessage(3));
    }

    // Message 1 is sent and message 2 is discarded, so the total should be 1
    @Test
    public void totalMessagesReturned() {
        msg1.SentMessage(1);
        msg2.SentMessage(2);
        assertEquals(1, Message.returnTotalMessages());
    }
}