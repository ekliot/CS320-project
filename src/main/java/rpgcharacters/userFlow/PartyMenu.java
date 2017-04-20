package rpgcharacters.userflow;

import java.util.Scanner;
import java.util.ArrayList;

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

    private void printOptions () {
        String optionsString =
            "Available options:\n" +
            "\t1: Create a new party\n" +
            "\t2: Print party\n" +
            "\t3: Delete party\n" +
            "\t4: Remove a character from a party\n" +
            "\t5: Go back\n" +
            "-------------------------------------------------------"; // 50 chars;
        System.out.println(optionsString);
        System.out.print("Please enter the number of the desired option here: ");
    }

    /**
    * Defines the loop for this menu
    */
    public void enter () {
        printMenuTitle();
        int input;
        String party;
        int exit = 5;
        do {

            printOptions();
            input = sc.nextInt();

            switch (input) {
                case 1:
                    System.out.println("\nCreate Party\n");
                    break;
                case 2:
                    party = printParties();
                    System.out.println("\nPrint " + party + "\n");
                    break;
                case 3:
                    party = printParties();
                    System.out.println("\nDelete " + party + "\n");
                    break;
                case 4:
                    party = printParties();
                    System.out.println("\nRemove character from " + party + "\n");
                    break;
                case 5:
                    System.out.println("\nGoing back...\n");
                    break;
                default:
                    System.out.println("\nInvalid input...\n");
            }

        } while (input != exit);
    }

}
