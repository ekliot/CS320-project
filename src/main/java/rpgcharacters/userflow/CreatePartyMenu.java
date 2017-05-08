package rpgcharacters.userflow;

import java.sql.*;
import java.util.Scanner;

public class CreatePartyMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    private String username;

    /**
     * Constructor Method
     * @param  Scanner sc    scanner inherited from the parent menu.
     */
    public CreatePartyMenu(Scanner sc, String username, Connection conn) {
        this.sc = sc;
        this.username = username;
        this.conn = conn;
    }

    private void printMenuTitle() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println("Create Party");
        System.out.println("-------------------------------------------------------");
    }

    private boolean saveToDB(String partyName) {
        try {
            String query = "INSERT INTO party (name, gm_username) VALUES ("
                         + "'" + partyName.replaceAll("'", "''") + "',"
                         + "'" + this.username.replaceAll("'", "''") + "');";
            Statement stmt = conn.createStatement();
            stmt.execute(query);
            return true;
        } catch (SQLException e) {
            if (e.getMessage().startsWith("Unique index or primary key violation")) {
                System.out.println("\nParty already exists!\n");
            } else {
                System.out.println("\nAn error has occured while saving to the database.");
                e.printStackTrace();
            }
            return false;
        }
    }

    private boolean createParty() {

        System.out.print("Party Name: ");
        String partyName = sc.nextLine();

        return saveToDB(partyName);
    }

    /**
    * Defines the loop for this menu
    */
    public void enter() {
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
