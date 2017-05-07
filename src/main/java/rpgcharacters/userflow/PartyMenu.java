package rpgcharacters.userflow;

import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.InputMismatchException;

public class PartyMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    private String username;

    /**
     * Constructor Method
     */
    public PartyMenu(Scanner sc, String username, Connection conn) {
        this.sc = sc;
        this.username = username;
        this.conn = conn;
    }

    private void printMenuTitle() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println("Party Menu");
        System.out.println("-------------------------------------------------------");
    }

    private String printParties() {
        ArrayList<String> parties = new ArrayList<String>();
        
        try {
            String query = "SELECT name FROM party "
                         + "WHERE gm_username='" + username.replaceAll("'", "''") + "';";
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);

            results.beforeFirst();
            while (results.next()) {
                parties.add(results.getString("name"));
            }

            if (parties.size() == 0) {
                System.out.println("\n You do not have any parties!");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        String partyString = "Your parties:\n";

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

    private void deleteParty(String partyName) {
        try {
            String query = "DELETE FROM party "
                         + "WHERE gm_username = '" + username.replaceAll("'", "''") + "' "
                         + "AND name = '" + partyName.replaceAll("'", "''") + "';";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
            System.out.println(partyName + " has been deleted!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void printParty(String partyName) {
        System.out.println(
            "-------------------------------------------------------\n" + // 50 chars
            partyName + "\n" +
            "  Game master: " + username + "\n" +
            "  Characters:"
        );

        try {
            String query = "SELECT * FROM character as c "
                         + "LEFT OUTER JOIN party as p on c.party_id = p.id "
                         + "WHERE p.name = '" + partyName.replaceAll("'", "''") + "'";
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);

            results.beforeFirst();
            while (results.next()) {
                System.out.println("\tName: " + results.getString("name"));
                System.out.println("\tUser: " + results.getString("user_username") + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void printOptions() {
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
    public void enter() {
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
                        Menu createPartyMenu = new CreatePartyMenu(sc, username, conn);
                        createPartyMenu.enter();
                        break;
                    case 2:
                        party = printParties();
                        if (party == null) break;
                        printParty(party);
                        break;
                    case 3:
                        party = printParties();
                        if (party == null) break;
                        deleteParty(party);
                        break;
                    case 4:
                        party = printParties();
                        if (party == null) break;
                        Menu partyRemoveCharMenu = new PartyRemoveCharMenu(sc, username, party, conn);
                        partyRemoveCharMenu.enter();
                        break;
                    case 5:
                        party = printParties();
                        if (party == null) break;
                        Menu partyAddCharMenu = new PartyAddCharMenu(sc, username, party, conn);
                        partyAddCharMenu.enter();
                        break;
                    case 6:
                        party = printParties();
                        if (party == null) break;
                        Menu editPartyQuestsMenu = new EditPartyQuestsMenu(sc, username, party, conn);
                        editPartyQuestsMenu.enter();
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
