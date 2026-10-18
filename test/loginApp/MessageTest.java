
package loginApp;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MessageTest {
    @Before
    public void setUp(){
    Message.resetForTesting();
    }
    @Test
    public void testMessageLengthValid() {
    String shortMessage = "Hi Sam can you join us for dinner tonight?";
        assertEquals("Message ready to send.", Message.checkMessageLength(shortMessage));
    }
    @Test
    public void testMessageLengthInvalid(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 260; i++){
            sb.append("a");
        }
        String longMessage = sb.toString();
        int expectedOver= longMessage.length()-250;
            assertEquals("Message exceeds 250 Characters by " + expectedOver + "; please reduce the size.", Message.checkMessageLength(longMessage));
  }
    // recipient cell number//
    @Test
    public void testRecipientValid(){
    assertEquals("Cell phone number successfully captured.", Message.checkRecipientCell("+27718693002"));
    }
    @Test
    public void TestRecipientInvalid(){
    assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
            Message.checkRecipientCell("0876765437"));
    }
    //Message ID//
    @Test
    public void testMessageIDISValidLength(){
    Message message= new Message("+27718693002", "Hi Sam, can you join us for dinner tonight?");
    assertTrue(message.checkMessagesID());}
    
    //Message Hash//
    @Test
    public void testMessageHashFirstMessage(){
    Message message= new Message("+277718693002", "Hi Sam, can you join us for dinner tonight?");
            String hash= message.getMessageHash();
     String expectedIDPrefix= message.getMessageID().substring(0, 2);
    assertTrue("Hash should start with the first two digits of the message ID", hash.startsWith(expectedIDPrefix + ":0:"));
    assertTrue("Hash should end with the first and last words in capitals", hash.endsWith("HITONIGHT"));
    }
    @Test
    public void testMessageHashesInALoop(){
    String[] recipients= {"+27718693002","+27831234567","+278455556666"};
        String [] messages ={ "Hi Sam, can you join us for dinner tonight?","Please remeber to bring the documents tomorrow", "Thanks so much for the help today." };
    String[] expectedSuffix ={"HITONIGHT", "PLEASETOMORROW","THANKSTODAY"};
    for (int i = 0; i<messages.length; i++){
        Message message= new Message(recipients[i], messages[i]);String hash= message.getMessageHash();
        assertTrue("Hash #" +i + "should end with"+ expectedSuffix[i], hash.endsWith(expectedSuffix[i]));
        assertTrue("Hash #"+ i, hash.contains(":"+i +":"));
                
    }
    }
    //SentMessage//
    @Test
    public void testSentMessageSendChoice(){
    Message message = new Message("+2718693002", " Hi Sam can you join us for dinner tonight?");
    assertEquals("Message successfully sent.", message.sentMessage(1));
    }
    @Test
    public void testSentMessageStoreChoice(){
    Message message = new Message("+2718693002", " Hi Sam can you join us for dinner tonight?");
    assertEquals("Press 0 to delete the message.", message.sentMessage(2));}
    
    @Test
    public void testSentMessagesStoreChoice(){
    Message message = new Message("+2718693002", " Hi Sam can you join us for dinner tonight?");
    assertEquals("Message successfully stored.", message.sentMessage(3));}
    
    //total messages sent//
    @Test
    public void
    testReturnTotalMessagesOnlyCountsSentMessages(){
    Message m1= new Message ("+2718693002"," Hi Sam can you join us for dinner tonight?");
        m1.sentMessage(1);
    Message m2= new Message ("+27845556666"," Second test message.");
        m2.sentMessage(2);
      Message m3= new Message ("+27845556666"," Third test message.");
        m3.sentMessage(3);
    assertEquals(1, Message.returnTotalMessages());
    }
    
  
    
    

    

   
}
