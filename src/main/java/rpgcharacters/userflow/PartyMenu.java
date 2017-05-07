package rpgcharacters.userflow;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.InputMismatchException;

import java.sql.Connection;

public class PartyMenu implements Menu {

    private Scanner sc;

    private String username;

    /**
     * Constructor Method
     */
    public PartyMenu (Scanner sc, String username) {
        this.sc = sc;
        this.username = username;
    }

    private void printMenuTitle() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println("Party Menu");
        System.out.println("-------------------------------------------------------");
    }

    private String printParties () {
        String partyString = "Your parties:\n";

        ArrayList<String> parties = new ArrayList<String>();
        // TODO: replace with real data from sql db
        parties.add("Bob's Builders");
        parties.add("The Fellowship of the Rings");
        parties.add("Squad o' Fish");

        for (int i = 0; i < parties.size(); i++) {
            partyString += "\t" + (i+1) + ". " + parties.get(i) + "\n";
        }
        partyString += "-------------------------------------------------------";

        System.out.println(partyString);
        System.out.print("Please enter the number of the desired party here: ");
        int input = sc.nextInt();

        while (input < 1 || input > parties.size()) {
            System.out.println("\nInvalid input!\n");
            System.out.print("Please enter the number of the desired party here: ");
            input = sc.nextInt();
        }

        return parties.get(input-1);
    }

    private void deleteParty (String partyName) {

        // TODO: Delete the character from the database.
        // Make appropriate print statements if something bad happens.

        System.out.println(partyName + " has been deleted!");
    }

    private void printParty (String partyName) {
        String gameMaster;
        ArrayList<String> charNames = new ArrayList<String>();
        ArrayList<String> userNames = new ArrayList<String>();

        // TODO: Add all character names to the charNames arraylist and the
        //       corresponding usernames to the userNames arraylist.
        // Make appropriate print statements if something bad happens.

        gameMaster = "master dood";
        charNames.add("Bilbo Baggins");
        charNames.add("John Cena");
        charNames.add("Rick C137");
        userNames.add("idkmanthings");
        userNames.add("username");
        userNames.add("souperPerson8");

        System.out.println(
            "--------------------------------------------------\n" + // 50 chars
            partyName + "\n" +
            "--------------------------------------------------\n" + // 50 chars
            "Game master: " + gameMaster + "\n" +
            "Characters:"
        );

        if (charNames.size() != userNames.size()) {
            System.out.println("Something bad happened.");
            System.out.println("charNames.size() != userNames.size()");
        }
        else {
            for (int i = 0; i < charNames.size(); i++) {
                String userPr = userNames.get(i);
                if (userPr.equals(this.username)) {
                    userPr = "You";
                }
                CharacterMenu.printCharacter(charNames.get(i),userNames.get(i));
            }
        }
    }

    private void printOptions () {
        String optionsString =
            "Available options:\n" +
            "\t1: Create a new party\n" +
            "\t2: Print party\n" +
            "\t3: Delete party\n" +
            "\t4: Remove a character from a party\n" +
            "\t5: Add a character to a party\n" +
            "\t6: Edit a party's quests\n" +
            "\t7: Go back\n" +
            "-------------------------------------------------------"; // 50 chars;
        System.out.println(optionsString);
        System.out.print("Please enter the number of the desired option here: ");
    }

    /**
    * Defines the loop for this menu
    */
    public void enter ( Connection conn ) {
        printMenuTitle();
        int input = 0;
        String party;
        int exit = 7;
        do {

            printOptions();
            try {
                input = sc.nextInt();

                switch (input) {
                    case 1:
                        Menu createPartyMenu = new CreatePartyMenu(sc,username);
                        createPartyMenu.enter( conn );
                        break;
                    case 2:
                        party = printParties();
                        printParty(party);
                        break;
                    case 3:
                        party = printParties();
                        deleteParty(party);
                        break;
                    case 4:
                        party = printParties();
                        Menu partyRemCharMenu = new PartyRemCharMenu(sc,username,party);
                        partyRemCharMenu.enter( conn );
                        break;
                    case 5:
                        party = printParties();
                        Menu partyAddCharMenu = new PartyAddCharMenu(sc,username,party);
                        partyAddCharMenu.enter( conn );
                        break;
                    case 6:
                        party = printParties();
                        Menu editPartyQuestsMenu = new EditPartyQuestsMenu(sc,username,party);
                        editPartyQuestsMenu.enter( conn );
                        break;
                    case 7:
                        System.out.println("\nGoing back...\n");
                        break;
                    default:
                        System.out.println("\nInvalid input...\n");
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\nInvalid input...\n");
                continue;
            }

        } while (input != exit);
    }

}
