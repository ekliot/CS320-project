package rpgcharacters.userflow;

import rpgcharacters.UI;

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
                UI.printOutput("Party already exists!\n");
            } else {
                UI.printOutput("An error has occured while saving to the database.");
                // e.printStackTrace();
            }
            return false;
        }
    }

    private boolean createParty() {

        UI.printOutput( "Party Name: ", false );
        String partyName = sc.nextLine();

        return saveToDB( partyName );

    }

    /**
    * Defines the loop for this menu
    */
    public void enter() {
        UI.printMenuTitle( "Create Party" );
        UI.printDiv2();

        boolean validCharInfo = false;
        int wrongCount = 0;

        do {

            validCharInfo = createParty();

            if (!validCharInfo) {
                UI.printOutput("An error has occured while saving to the database.");
                UI.printOutput("Please try again...\n");
            }

            wrongCount++;

        } while (!validCharInfo && wrongCount <= 3);

        if (validCharInfo) {
            UI.printOutput("Party has been created!");
        }
        else {
            UI.printOutput("Too many attempts... Returning...");
        }
    }

}
