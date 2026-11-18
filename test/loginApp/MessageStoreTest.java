package loginApp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

public class MessageStoreTest {

    // Known IDs so the tests are repeatable
    private static final String ID1 = "1000000001";
    private static final String ID2 = "2000000002";
    private static final String ID3 = "3000000003";
    private static final String ID4 = "4000000004";
    private static final String ID5 = "5000000005";

    private static final String MSG2 =
            "Where are you? You are late! I have asked you to be on time.";

    @Before
    public void setUp() throws IOException {
        Path temp = Files.createTempFile("messages", ".json");
        MessageStore.jsonFile = temp.toString();
        MessageStore.reset();

        // Test data messages 1 to 5
        MessageStore.addMessage(ID1, "Developer", "+27834557896", "Did you get the cake?", MessageStore.SENT);
        MessageStore.addMessage(ID2, "Developer", "+27838884567", MSG2, MessageStore.STORED);
        MessageStore.addMessage(ID3, "Developer", "+27834484567", "Yohoooo, I am at your gate.", MessageStore.DISREGARD);
        MessageStore.addMessage(ID4, "Developer", "0838884567", "It is dinner time!", MessageStore.SENT);
        MessageStore.addMessage(ID5, "Developer", "+27838884567", "Ok, I am leaving without you.", MessageStore.STORED);
    }

    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {
        ArrayList<String> expected = new ArrayList<>();
        expected.add("Did you get the cake?");
        expected.add("It is dinner time!");
        assertEquals(expected, MessageStore.sentMessages);
    }

    @Test
    public void testDisregardedAndStoredArraysPopulated() {
        assertEquals(1, MessageStore.disregardedMessages.size());
        assertEquals("Yohoooo, I am at your gate.", MessageStore.disregardedMessages.get(0));
        assertEquals(2, MessageStore.storedMessages.size());
    }

    @Test
    public void testHashAndIdArraysPopulated() {
        assertEquals(5, MessageStore.messageIDs.size());
        assertEquals(5, MessageStore.messageHashes.size());
    }

    @Test
    public void testMessageHashFormat() {
        assertEquals("00:0:HITONIGHT",
                MessageStore.createMessageHash("0012345678", 0, "Hi Mike, can you join us for dinner tonight?"));
        assertEquals("10:0:DIDCAKE", MessageStore.messageHashes.get(0));
    }

    @Test
    public void testDisplayLongestMessage() {
        assertEquals(MSG2, MessageStore.longestStoredMessage());
    }

    @Test
    public void testSearchForMessageID() {
        assertEquals("It is dinner time!", MessageStore.getMessageByID(ID4));
        assertEquals("Recipient: 0838884567\nMessage: It is dinner time!",
                MessageStore.searchByMessageID(ID4));
        assertEquals("Message ID not found.", MessageStore.searchByMessageID("9999999999"));
    }

    @Test
    public void testSearchAllMessagesForRecipient() {
        ArrayList<String> expected = new ArrayList<>();
        expected.add(MSG2);
        expected.add("Ok, I am leaving without you.");
        assertEquals(expected, MessageStore.searchByRecipient("+27838884567"));
    }

    @Test
    public void testDeleteMessageUsingHash() {
        String hash = MessageStore.messageHashes.get(1); // Test Message 2
        assertEquals("Message: \"" + MSG2 + "\" successfully deleted.",
                MessageStore.deleteByHash(hash));
        assertEquals(1, MessageStore.storedMessages.size());
        assertEquals("Message hash not found.", MessageStore.deleteByHash(hash));
    }

    @Test
    public void testDisplayReport() {
        String report = MessageStore.report(MessageStore.SENT);
        assertTrue(report.contains("Message Hash: " + MessageStore.messageHashes.get(0)));
        assertTrue(report.contains("Recipient: +27834557896"));
        assertTrue(report.contains("Message: Did you get the cake?"));
        assertTrue(report.contains("Recipient: 0838884567"));
        assertTrue(report.contains("Message: It is dinner time!"));
    }

    @Test
    public void testStoredMessagesReadBackFromJson() {
        
        MessageStore.reset();
        int loaded = MessageStore.loadStoredFromJson();
        assertEquals(2, loaded);
        assertEquals(MSG2, MessageStore.storedMessages.get(0));
        assertEquals("Ok, I am leaving without you.", MessageStore.storedMessages.get(1));
    }
}