package rpgcharacters.userflow;

import rpgcharacters.UI;

import java.sql.*;
import java.util.Scanner;

public class CreateUserMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    /**
     * Constructor Method
     * @param  Scanner sc    scanner inherited from the parent menu.
     */
    public CreateUserMenu(Scanner sc, Connection conn) {
        this.sc = sc;
        this.conn = conn;
    }

    public boolean createUser(String user, String pass) {
        try {
            String query = "INSERT INTO user VALUES ("
                         + "'" + user.replaceAll("'", "''") + "',"
                         + "'" + pass.replaceAll("'", "''") + "');";
            Statement stmt = conn.createStatement();
            stmt.execute(query);

            return true;
        } catch (SQLException e) {
            if (e.getMessage().startsWith("Unique index or primary key violation")) {
                UI.printOutput("User already exists!\n");
            } else {
                // e.printStackTrace();
                UI.printOutput( "There was an error creating a new user" );
            }
            return false;
        }
    }

    /**
     * Defines the loop for this menu
     */
    public void enter() {

        boolean validUserInfo = false;
        int wrongCount = 0;

        do {

            UI.printOutput("Username: ", false);
            String user = sc.nextLine();

            UI.printOutput("Password: ", false);
            String pass = sc.nextLine();

            UI.printOutput("Confirm password: ", false);
            String confPass = sc.nextLine();

            if (pass.equals(confPass)) {
                validUserInfo = createUser(user, pass);
            }
            else {
                UI.printOutput("Passwords do not match. Please try again...\n");
            }

            wrongCount++;

        } while (!validUserInfo && wrongCount <= 3);

        if (validUserInfo) {
            UI.printOutput("User has been created!");
        }
        else {
            UI.printOutput("Too many attempts... Returning...");
        }

    }

}
