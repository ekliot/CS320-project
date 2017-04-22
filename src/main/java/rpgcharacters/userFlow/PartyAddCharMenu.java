
package rpgcharacters.userflow;
import java.util.Scanner;
import java.lang.Boolean;
import java.util.HashMap;
import java.util.ArrayList;

public class PartyAddCharMenu implements Menu {

    private Scanner sc;

    private String username;
    private String partyName;

    private int curMaxAdd;

    /**
     * Constructor Method
     * @param  Scanner sc    scanner inherited from the parent menu.
     */
    public PartyAddCharMenu (Scanner sc,String username, String partyName) {
        this.sc = sc;
        this.username = username;
        this.partyName = partyName;
    }

    private void printMenuTitle() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println("Add character to " + this.partyName);
        System.out.println("-------------------------------------------------------");
    }

    private String selectCharacter() {
        ArrayList<String> charNames = new ArrayList<String>();
        ArrayList<String> userNames = new ArrayList<String>();


        // TODO: Get the names of the characters NOT in the party and add them
        //       to the charNames ArrayList and the corresponding userNames
        //       to the userNames ArrayList.

        charNames.add("Bilbo Baggins");
        charNames.add("John Cena");
        charNames.add("Rick C137");
        userNames.add("idkmanthings");
        userNames.add("username");
        userNames.add("souperPerson8");

        if (charNames.size() != userNames.size()) {
            System.out.println("Something bad happened.");
            System.out.println("charNames.size() != userNames.size()");
            return "error";
        }

        System.out.println("Characters:\n");
        String pString = "";
        int indLen = (charNames.size() + "").length() + 2;
        for (int i = 0; i < charNames.size(); i++) {
            String lineString1 = "";
            String lineString2 = "";
            lineString1 += (i+1) + ".";
            while (lineString1.length() < indLen) {
                lineString1 += " ";
            }
            while (lineString2.length() < indLen) {
                lineString2 += " ";
            }
            lineString1 += "Character: " + charNames.get(i) + "\n";
            lineString2 += "User: " + userNames.get(i) + "\n";
            pString += lineString1 + lineString2 + "\n";
        }
        System.out.print(pString);


        System.out.println("-------------------------------------------------------");
        System.out.print("Enter corresponding number of the character to add: ");
        int input = sc.nextInt();
        while (input < 1 || input > charNames.size()) {
            System.out.println("\nInvalid input...\n");
            System.out.print("Enter corresponding number of the character to add: ");
            input = sc.nextInt();
        }
        return charNames.get(input-1);
    }

    private boolean remChararacter (String charName) {

        // TODO: Remove character from the party.
        // make an appropriate print statement if something goes wrong.
        // return true if it worked; otherwise false.

        System.out.println(charName + " has been added to " + partyName);

        return true;
    }

    /**
    * Defines the loop for this menu
    */
    public void enter () {
        printMenuTitle();
        sc.nextLine();
        boolean success = false;
        int wrongCount = 0;

        do {

            String charToAdd = selectCharacter();

            if (!charToAdd.equals("error")) {
                success = remChararacter(charToAdd);
            }

            wrongCount++;

        } while (!success && wrongCount <= 3);

        if (!success) {
            System.out.println("\nToo many attempts... Returning...\n");
        }
    }

}
