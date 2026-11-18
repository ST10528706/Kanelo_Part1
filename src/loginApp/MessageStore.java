package loginApp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Part 3: arrays, JSON storage and the "Stored Messages" menu.
 * Every message is kept in the parallel lists (ids, hashes, senders,
 * recipients, texts, flags). The three flag arrays (sentMessages,
 * disregardedMessages, storedMessages) hold the message text per flag.
 */
public class MessageStore {

    public static final String SENT = "Sent";
    public static final String DISREGARD = "Disregard";
    public static final String STORED = "Stored";

    // Required arrays
    public static ArrayList<String> sentMessages = new ArrayList<>();
    public static ArrayList<String> disregardedMessages = new ArrayList<>();
    public static ArrayList<String> storedMessages = new ArrayList<>();
    public static ArrayList<String> messageHashes = new ArrayList<>();
    public static ArrayList<String> messageIDs = new ArrayList<>();

    // Parallel lists (same index = same message)
    private static final ArrayList<String> senders = new ArrayList<>();
    private static final ArrayList<String> recipients = new ArrayList<>();
    private static final ArrayList<String> texts = new ArrayList<>();
    private static final ArrayList<String> flags = new ArrayList<>();

    private static int messageNumber = 0;
    private static final Random RANDOM = new Random();

    /** Where stored messages are saved. Tests change this to a temp file. */
    public static String jsonFile = "messages.json";

    /** Name of the logged-in user, shown as the sender. */
    public static String currentSender = "Me";

    // ---------------------------------------------------------------- adding

    /** Adds a message with a random ID. Returns the new message ID. */
    public static String addMessage(String sender, String recipient, String text, String flag) {
        return addMessage(generateMessageID(), sender, recipient, text, flag);
    }

    /** Adds a message with a given ID. Returns the message ID. */
    public static String addMessage(String id, String sender, String recipient,
                                    String text, String flag) {
        String hash = createMessageHash(id, messageNumber, text);
        return addMessage(id, hash, sender, recipient, text, flag);
    }

    /** Adds a message using the ID and hash made by the Message class. */
    public static String addMessage(String id, String hash, String sender,
                                    String recipient, String text, String flag) {
        if (id == null || id.isEmpty()) id = generateMessageID();
        if (hash == null || hash.isEmpty()) hash = createMessageHash(id, messageNumber, text);
        messageNumber++;
        messageIDs.add(id);
        messageHashes.add(hash);
        senders.add(sender);
        recipients.add(recipient);
        texts.add(text);
        flags.add(flag);
        listFor(flag).add(text);
        if (STORED.equals(flag)) {
            saveStoredToJson();
        }
        return id;
     
    }

    private static ArrayList<String> listFor(String flag) {
        if (SENT.equals(flag)) return sentMessages;
        if (DISREGARD.equals(flag)) return disregardedMessages;
        return storedMessages;
    }

    public static String generateMessageID() {
        long n = (long) (RANDOM.nextDouble() * 10_000_000_000L);
        return String.format("%010d", n);
    }

    /** First two digits of ID : message number : FIRSTWORDLASTWORD (all caps). */
    public static String createMessageHash(String id, int number, String text) {
        String[] words = text.trim().split("\\s+");
        String first = words[0].replaceAll("[^A-Za-z0-9]", "");
        String last = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");
        String prefix = id.length() >= 2 ? id.substring(0, 2) : id;
        return (prefix + ":" + number + ":" + first + last).toUpperCase();
    }

    // -------------------------------------------------------------- features

    /** 2a. Sender and recipient of all stored messages. */
    public static String displaySenderAndRecipient() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < flags.size(); i++) {
            if (STORED.equals(flags.get(i))) {
                sb.append("Sender: ").append(senders.get(i))
                  .append(" | Recipient: ").append(recipients.get(i)).append("\n");
            }
        }
        return sb.length() == 0 ? "No stored messages." : sb.toString();
    }

    /** 2b. Longest stored message. */
    public static String longestStoredMessage() {
        String longest = null;
        for (String m : storedMessages) {
            if (longest == null || m.length() > longest.length()) {
                longest = m;
            }
        }
        return longest == null ? "No stored messages." : longest;
    }

    /** 2c. Message text for an ID (or null if not found). */
    public static String getMessageByID(String id) {
        int i = messageIDs.indexOf(id);
        return i < 0 ? null : texts.get(i);
    }

    /** 2c. Recipient and message for an ID, ready to display. */
    public static String searchByMessageID(String id) {
        int i = messageIDs.indexOf(id);
        if (i < 0) return "Message ID not found.";
        return "Recipient: " + recipients.get(i) + "\nMessage: " + texts.get(i);
    }

    /** 2d. All sent or stored messages for one recipient. */
    public static ArrayList<String> searchByRecipient(String recipient) {
        ArrayList<String> found = new ArrayList<>();
        for (int i = 0; i < recipients.size(); i++) {
            boolean sentOrStored = SENT.equals(flags.get(i)) || STORED.equals(flags.get(i));
            if (sentOrStored && recipients.get(i).equals(recipient)) {
                found.add(texts.get(i));
            }
        }
        return found;
    }

    /** 2e. Delete a message using its hash. */
    public static String deleteByHash(String hash) {
        int i = messageHashes.indexOf(hash);
        if (i < 0) return "Message hash not found.";

        String text = texts.get(i);
        String flag = flags.get(i);

        listFor(flag).remove(text);
        messageIDs.remove(i);
        messageHashes.remove(i);
        senders.remove(i);
        recipients.remove(i);
        texts.remove(i);
        flags.remove(i);

        if (STORED.equals(flag)) {
            saveStoredToJson();
        }
        return "Message: \"" + text + "\" successfully deleted.";
    }

    public static String report(String flag) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < flags.size(); i++) {
            if (flag.equals(flags.get(i))) {
                sb.append("Message Hash: ").append(messageHashes.get(i)).append("\n")
                  .append("Message ID: ").append(messageIDs.get(i)).append("\n")
                  .append("Sender: ").append(senders.get(i)).append("\n")
                  .append("Recipient: ").append(recipients.get(i)).append("\n")
                  .append("Message: ").append(texts.get(i)).append("\n\n");
            }
        }
        return sb.length() == 0 ? "No " + flag.toLowerCase() + " messages." : sb.toString();
    }

    /** Hash of the stored message at a position (used for deleting from the menu). */
    public static ArrayList<String> storedHashes() {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < flags.size(); i++) {
            if (STORED.equals(flags.get(i))) result.add(messageHashes.get(i));
        }
        return result;
    }

    /** Writes all stored messages to the JSON file. */
    public static void saveStoredToJson() {
        StringBuilder sb = new StringBuilder("[\n");
        boolean first = true;
        for (int i = 0; i < flags.size(); i++) {
            if (!STORED.equals(flags.get(i))) continue;
            if (!first) sb.append(",\n");
            first = false;
            sb.append("  {\"id\": \"").append(esc(messageIDs.get(i))).append("\", ")
              .append("\"hash\": \"").append(esc(messageHashes.get(i))).append("\", ")
              .append("\"sender\": \"").append(esc(senders.get(i))).append("\", ")
              .append("\"recipient\": \"").append(esc(recipients.get(i))).append("\", ")
              .append("\"message\": \"").append(esc(texts.get(i))).append("\"}");
        }
        sb.append("\n]\n");
        try {
            Files.write(Paths.get(jsonFile), sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Could not save messages: " + e.getMessage());
        }
    }

    /** Reads the JSON file into the stored arrays. Returns how many were loaded. */
    public static int loadStoredFromJson() {
        Path path = Paths.get(jsonFile);
        if (!Files.exists(path)) return 0;

        String json;
        try {
            json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Could not read messages: " + e.getMessage());
            return 0;
        }

        Pattern p = Pattern.compile("\"(\\w+)\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
        Matcher m = p.matcher(json);
        String id = "", hash = "", sender = "", recipient = "";
        int loaded = 0;

        while (m.find()) {
            String key = m.group(1);
            String value = unesc(m.group(2));
            switch (key) {
                case "id": id = value; break;
                case "hash": hash = value; break;
                case "sender": sender = value; break;
                case "recipient": recipient = value; break;
                case "message":
                    if (!messageIDs.contains(id)) {
                        messageIDs.add(id);
                        messageHashes.add(hash);
                        senders.add(sender);
                        recipients.add(recipient);
                        texts.add(value);
                        flags.add(STORED);
                        storedMessages.add(value);
                        loaded++;
                    }
                    break;
                default: break;
            }
        }
        return loaded;
    }

    private static String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private static String unesc(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char n = s.charAt(++i);
                switch (n) {
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    default: sb.append(n);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /** Clears every array (used by tests). */
    public static void reset() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        senders.clear();
        recipients.clear();
        texts.clear();
        flags.clear();
        messageNumber = 0;
    }

    //menu// 

    
    public static void storedMessagesMenu(Scanner in) {
        String choice = "";
        while (!choice.equals("7")) {
            System.out.println("\n--- Stored Messages ---");
            System.out.println("1) Display sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for all messages for a recipient");
            System.out.println("5) Delete a message using its hash");
            System.out.println("6) Display report of all stored messages");
            System.out.println("7) Back to main menu");
            System.out.print("Choose an option: ");
            choice = in.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println(displaySenderAndRecipient());
                    break;
                case "2":
                    System.out.println(longestStoredMessage());
                    break;
                case "3":
                    System.out.print("Enter the message ID: ");
                    System.out.println(searchByMessageID(in.nextLine().trim()));
                    break;
                case "4":
                    System.out.print("Enter the recipient number: ");
                    ArrayList<String> found = searchByRecipient(in.nextLine().trim());
                    if (found.isEmpty()) {
                        System.out.println("No messages found for that recipient.");
                    } else {
                        for (String m : found) System.out.println("\"" + m + "\"");
                    }
                    break;
                case "5":
                    System.out.print("Enter the message hash: ");
                    System.out.println(deleteByHash(in.nextLine().trim().toUpperCase()));
                    break;
                case "6":
                    System.out.println(report(STORED));
                    break;
                case "7":
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
