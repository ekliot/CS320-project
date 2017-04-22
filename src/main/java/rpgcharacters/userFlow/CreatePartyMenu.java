package rpgcharacters.userflow;
import java.util.Scanner;
import java.lang.Boolean;
import java.util.HashMap;
import java.util.ArrayList;

public class CreatePartyMenu implements Menu {

    private Scanner sc;

    private String username;

    private int curMaxAdd;

    /**
     * Constructor Method
     * @param  Scanner sc    scanner inherited from the parent menu.
     */
    public CreatePartyMenu (Scanner sc, String username) {
        this.sc = sc;
        this.username = username;
    }

    private void printMenuTitle() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println("Create Party");
        System.out.println("-------------------------------------------------------");
    }

    private boolean saveToDB (String partyName) {

        // TODO: Modify to use SQL and have specific printouts for cases:
        // - Party name already in use
        // - Other error occured
        //
        // Return true if no errors and use is created; False otherwise.

        return true;
    }

    private boolean createParty () {

        System.out.print("Party Name: ");
        String partyName = sc.nextLine();

        return saveToDB(partyName);
    }

    /**
    * Defines the loop for this menu
    */
    public void enter () {
        printMenuTitle();
        sc.nextLine();
        boolean validCharInfo = false;
        int wrongCount = 0;

        do {

            validCharInfo = createParty();

            if (!validCharInfo) {
                System.out.println("\nAn error has occured while saving to the database.");
                System.out.println("Please try again...\n");
            }

            wrongCount++;

        } while (!validCharInfo && wrongCount <= 3);

        if (validCharInfo) {
            System.out.println("\nParty has been created!\n");
        }
        else {
            System.out.println("\nToo many attempts... Returning...\n");
        }
    }

}
