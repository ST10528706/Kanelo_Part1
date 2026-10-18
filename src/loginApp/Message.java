package loginApp;
import java.io.File;
import java.nio.file.Paths;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Message{

    private static int
   totalMessagesCreated = 0;

    private static int
     totalMessagesSent = 0;
    private static final List<Message>
    sentMessages = new ArrayList<>();
    
    private static final String
    JSON_FILE = "messages.json";
    
    
    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private final String messageHash;

    public Message(String recipient,String messageText){
        this.messageID = 
     generateMessageID();
        this.messageNumber =
     totalMessagesCreated;
        totalMessagesCreated++;
        this.recipient = recipient;
        this.messageText= messageText;
        this.messageHash = 
     createMessageHash();
    }

    public boolean checkMessagesID(){
        return messageID != null &&
messageID.length() <=10;
    }

    public static String 
checkRecipientCell(String cell){
    if (cell == null){
        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }
    boolean hasCountryCode =
cell.startsWith("+");
    boolean rightLength =
cell.length() <=12;
    if (hasCountryCode && rightLength){
        return "Cell phone number successfully captured.";
    }
    return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
}

/** Ensures the message text is not longer than 250 character. */
    public static String
checkMessageLength(String text){
    if (text== null){
        text = "";
    }
    if (text.length() <=250){
        return "Message ready to send.";
    }
    int over = text.length() - 250;
    return "Message exceeds 250 Characters by " + over + "; please reduce the size.";                                                        
}
/**
* builds and returns the message hash:
    * first 2 digits of the ID : number of the message: first+last word(caps)*/
 public final String
createMessageHash(){
    String idPart =
messageID.length() >=2 ?
messageID.substring(0, 2) : messageID;
    String[] words =
messageText.trim().split("\\s+");
    String first = words.length >
0 ? stripPunctuation(words[0]) : "";
    String last = words.length >
0 ? stripPunctuation(words[words.length - 1]) :"";
    return idPart + ":" + messageNumber + ":" + (first + last).toUpperCase();
}

    private static String
stripPunctuation(String word){
    return word.replaceAll("[^a-zA-Z0-9]", "");
}

/** choice:1 = send, 2 = disregard, 3= store */ 
    public String sentMessage(int choice){
        switch (choice){
            case 1:
                totalMessagesSent++;
                sentMessages.add(this);
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                StoreMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option, please choose 1, 2 or 3.";
        }
    }
/**Returns the full details of every message sent*/
    public static String
printMessages(){
    if (sentMessages.isEmpty()){
      return "No message has been sent yet."; 
    }
    StringBuilder sb = new
StringBuilder();
    for (Message m : sentMessages){
        sb.append("Message ID:").append(m.messageID).append("\n")
        .append("Message Hash:").append(m.messageHash).append("\n")
        .append("Recipient").append(m.recipient).append("\n")
        .append("Message").append(m.messageText).append("\n\n");    
    }
    return sb.toString();
}
    public static int
returnTotalMessages(){
    return totalMessagesSent;
}
public String StoreMessage(){
     try {
        String newEntry = " {\n"
        +"             \"messageID\": \"" + escape(messageID)+ "\",\n"
        +"             \"messageHash\": \"" + escape(messageHash) + "\",\n" 
        +"             \"recipient\": \"" + escape(recipient)+ "\",\n" 
        +"             \"message\":\""+escape(messageText) + "\"\n" 
        + " }";        
     File file= new File(JSON_FILE);
     List<String> existing = new ArrayList<>();
     if(file.exists()){
         String content = new String(Files.readAllBytes(Paths.get(JSON_FILE))).trim();
         if (content.length()> 2){
             content = content.substring(1, content.length() -1).trim();
             if (!content.isEmpty()){
                 existing.add(content);
             }
         }
     }
     existing.add(newEntry);
     try (FileWriter writer = new FileWriter(file)){
         writer.write("[\n");
         writer.write(String.join(",\n", existing));
         writer.write("\n]");
     }
     return "message stored to " + JSON_FILE;
     } catch (IOException e){
         return " Could not store message:" + e.getMessage();
     }  
}
    private static String escape(String s){
      return s == null ? "" : 
    s.replace("\\", "\\\\").replace("\"","\\\"");
    }
    private static String
generateMessageID(){
    Random rand= new Random();
    StringBuilder sb = new
StringBuilder();
    for (int i = 0; i < 10; i++){
        sb.append(rand.nextInt(10));
    }
    return sb.toString();
}
//------------getters---------//

    public String getMessageID()
    { return messageID; }
       public int getMessageNumber()
       {return messageNumber;}
       public String getRecipient()
       {return recipient;}
       public String getMessageTExt()
       {return messageText;}
       public String getMessageHash()
       {return messageHash;}
       
       public static void
    resetForTesting(){
    totalMessagesCreated= 0;
    totalMessagesSent= 0;
    sentMessages.clear();
    }
}