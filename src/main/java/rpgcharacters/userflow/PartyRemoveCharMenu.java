package rpgcharacters.userflow;

import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;

public class PartyRemoveCharMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    private String partyName;

    /**
     * Constructor Method
     * @param  Scanner sc    scanner inherited from the parent menu.
     */
    public PartyRemoveCharMenu(Scanner sc, String partyName, Connection conn) {
        this.sc = sc;
        this.partyName = partyName;
        this.conn = conn;
    }

    private void printMenuTitle() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println("Remove Character From " + this.partyName);
        System.out.println("-------------------------------------------------------");
    }

    private String[] selectCharacter() {
        ArrayList<String> charNames = new ArrayList<String>();
        ArrayList<String> userNames = new ArrayList<String>();

        try {
            String query = "SELECT * FROM character as c "
                         + "LEFT OUTER JOIN party as p on c.party_id = p.id "
                         + "WHERE p.name = '" + this.partyName.replaceAll("'", "''") + "'";
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);

            results.beforeFirst();
            while (results.next()) {
                charNames.add(results.getString("name"));
                userNames.add(results.getString("user_username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (charNames.size() == 0) return null;

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
        System.out.print("Enter corresponding number of the character to remove: ");
        //sc.nextInt(); // clear buffer
        int input = sc.nextInt();

        while (input < 1 || input > charNames.size()) {
            System.out.println("\nInvalid input...\n");
            System.out.print("Enter corresponding number of the character to remove: ");
            input = sc.nextInt();
        }

        return new String[]{
            charNames.get(input-1),
            userNames.get(input-1)
        };
    }

    private boolean removeChararacter(String charName, String charUsername) {
        try {
            String query = "UPDATE character "
                         + "SET party_id = NULL "
                         + "WHERE user_username = '" + charUsername.replaceAll("'", "''") + "' "
                         + "AND name = '" + charName.replaceAll("'", "''") + "';";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
            System.out.println(charName + " has been removed from " + this.partyName);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
    * Defines the loop for this menu
    */
    public void enter() {
        printMenuTitle();
        sc.nextLine();
        boolean success = false;
        int wrongCount = 0;

        do {

            String[] character = selectCharacter();

            if (character == null) {
                System.out.println("\nThere are no characters in this party!\n");
                success = true;
            } else {
                success = removeChararacter(character[0], character[1]);
            }

            wrongCount++;

        } while (!success && wrongCount <= 3);

        if (!success) {
            System.out.println("\nToo many attempts... Returning...\n");
        }
    }

}
