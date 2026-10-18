package loginApp;
import java.util.Scanner;

public class QuickChatApp{

    private static int choice;
    public static void main(String [] args){
    Scanner scanner = new Scanner(System.in);
    
    System.out.println("=== Quickchat registration & Login ===");
    System.out.println("Enter your first name:");
        String firstname = scanner.nextLine();
        System.out.println("Enter your Last name:");
        String lastname = scanner.nextLine();
    
        Login login = new 
        Login(firstname, lastname);
    
    //Registration loop//
    String registerResult;
        while(true){
            System.out.println("Choose a username(must contain '_' and be 5 characters or less):");
            String username = scanner.nextLine();
            System.out.println("Choose a password(8 min characters, capital letter, number, special character):");
            String password = scanner.nextLine();
            System.out.println("Enter your cell number(e.g. +278311234567)");
            String cell= scanner.nextLine();
    registerResult= login.registerUser(username, password, cell);
        System.out.println(registerResult);
                if
        (registerResult.equals("USer successfully registered.")){
                break;
                }
                System.out.println("Please try again.\n");
        }
    // Login Loop //
        boolean loggedIn = false;
        while(!loggedIn){
            System.out.print("\nLogin username: ");
            String loginUsername = scanner.nextLine();
            System.out.print("Login Password:");
            String loginPassword=
            scanner.nextLine();
            loggedIn =  login.loginUser(loginUsername, loginPassword);
            System.out.println(login.returnLoginStatus(loggedIn));
        }
        System.out.println("\nWelcome to Quickchat.");
        int numMessagesToSend =
                readInt(scanner, "How many messages would you like to send?");
    // Main menu loop//
        boolean running= true;
        while(running){
    System.out.println("\nPlease choose an option");
        System.out.println("1) Send Messages");
        System.out.println("2) Show recently sent messages.");
        System.out.println("3) Quit");
        System.out.println("Your choice:");
        String choiceStr= scanner.nextLine().trim();
            
            switch(choiceStr){
                case "1" -> {
                    if
                            (Message.returnTotalMessages() >= numMessagesToSend){
                        System.out.println("You have already set your number of messages("+ numMessagesToSend+").");
                    }else{
                        handleSendMessage(scanner);
                    }
            }
                case "2" -> {
                    System.out.println("Coming soon");
            }

                case "3" -> {
                    running = false;
            }

                default -> System.out.println("Please enter,= 1, 2 or 3.");
                        }
        }
    System.out.println("\nTotal messages sent:" + Message.returnTotalMessages());}
    private static void
    handleSendMessage(Scanner scanner){
    String recipient;
    while(true){
        System.out.print("enter recipient cell number");
        recipient=scanner.nextLine().trim();
                String recipientResult= Message.checkRecipientCell(recipient);
        System.out.println(recipientResult);
                if
        (recipientResult.equals("Cell phone number sucessfully captured.")){
                break;
                }
    }
    String messageText;
    while(true){
    System.out.print("Enter your message(max 250 characters):");
            messageText=scanner.nextLine();
            String lengthResult= Message.checkMessageLength(messageText);
            System.out.println(lengthResult);
                if
            (lengthResult.equals("message ready to send.")){
                break;}
    
    }
     Message message= new
        Message(recipient, messageText);
    System.out.println("\nWhat would you like to do with this message?");
    System.out.println("1) Send Message");
    System.out.println("2) Discard Message");
    System.out.println("3) Store message to send later");
    System.out.println(" Your Choice:");
        String outcome= message.sentMessage(choice);
               System.out.println(outcome);
               if (choice == 1) {
               System.out.println("\n--- Message Details---");
                    System.out.println("Message ID:" + message.getMessageID());
                        System.out.println("Message Hash" + message.getMessageHash());
                System.out.println("recipient:"+ message.getRecipient());
               System.out.println("Message: " + message.getMessageTExt());
               
               }
    }
// keeps asking until the user types in a valid whole number//
    private static int readInt(Scanner scanner, String prompt) {
        while(true){
        System.out.print(prompt);
        String input= 
                scanner.nextLine().trim();
        try{
            return Integer.parseInt(input);
        }catch (NumberFormatException e){
        System.out.println("Please enter a whole number.");}
            }
    }
   
    
}