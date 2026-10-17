package com.mycompany.prog5121poe;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class MessageTest {

    private Message msg1, msg2;

    @Before
    public void setUp() {
        Message.resetAll();
        msg1 = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        msg2 = new Message("1234567890", 1, "08575975889",
                "Hi Keegan, did you receive the payment?");
    }

    @Test
    public void messageLengthSuccess() {
        assertEquals("Message ready to send.", msg1.getMessageLengthMessage());
    }

    @Test
    public void messageLengthFailure() {
        Message longMsg = new Message("0012345678", 0, "+27718693002", "a".repeat(260));
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                longMsg.getMessageLengthMessage());
    }

    @Test
    public void recipientCorrectlyFormatted() {
        assertEquals("Cell phone number successfully captured.", msg1.checkRecipientCell());
    }

    @Test
    public void recipientIncorrectlyFormatted() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                msg2.checkRecipientCell());
    }

    @Test
    public void messageHashTestCase1() {
        assertEquals("00:0:HITONIGHT", msg1.createMessageHash());
    }

    @Test
    public void allMessageHashesInLoop() {
        Message[] messages = {msg1, msg2};
        String[] expected = {"00:0:HITONIGHT", "12:1:HIPAYMENT"};
        for (int i = 0; i < messages.length; i++) {
            assertEquals(expected[i], messages[i].createMessageHash());
        }
    }

    @Test
    public void messageIDCreated() {
        String id = Message.generateMessageID();
        Message m = new Message(id, 0, "+27718693002", "Hello there");
        assertTrue(m.checkMessageID());
        assertEquals("Message ID generated: " + id, m.getMessageIDResult());
    }

    @Test
    public void sendMessage() {
        assertEquals("Message successfully sent.", msg1.SentMessage(1));
    }

    @Test
    public void disregardMessage() {
        assertEquals("Press 0 to delete the message.", msg2.SentMessage(2));
    }

    @Test
    public void storeMessage() {
        assertEquals("Message successfully stored.", msg1.SentMessage(3));
    }

    @Test
    public void totalMessagesReturned() {
        msg1.SentMessage(1);
        msg2.SentMessage(2);
        assertEquals(1, Message.returnTotalMessages());
    }
}
