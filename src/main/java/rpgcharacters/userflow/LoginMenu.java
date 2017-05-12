package rpgcharacters.userflow;

import rpgcharacters.UI;

import java.sql.*;
import java.util.Scanner;

public class LoginMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    private boolean isAdmin = false;

    /**
     * Constructor Method
     * @param  Scanner sc    scanner inherited from the parent menu.
    **/
    public LoginMenu(Scanner sc, Connection conn) {
        this.sc = sc;
        this.conn = conn;
    }

    private boolean checkLogin(String user, String pass) {

        boolean valid = false;
        isAdmin = false;

        // TODO remove this hack
        // this is just here to account for a potentially empty db w/o user data
        if (user.equals("admin") && pass.equals("admin")) {
            isAdmin = true;
            valid = true;
            return valid;
        }

        try {

            String query = "SELECT COUNT(*) AS usercount "
                         + "FROM user "
                         + "WHERE username='" + user.replaceAll("'", "''") + "' "
                         + "AND password='" + pass.replaceAll("'", "''") + "';";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            result.beforeFirst();

            // check if the result set actually has a row, and that the
            // usercount isn't 0 (i.e. the user/pass combo exists, and is the only one)
            valid = (result.next() && result.getInt("usercount") == 1);

            // TODO more robust way to check if user is admin
            if (valid && user.equals("admin") && pass.equals("admin")) {
                isAdmin = true;
            }

        } catch (SQLException e) {
            System.out.println("There was an error validating the login");
            e.printStackTrace();
        }

        return valid;

    }

    /**
     * Defines the loop for this menu
    **/
    public void enter() {

        UI.clearScreen();

        UI.printMenuTitle( "Login Menu" );
        UI.printDiv2();

        UI.printOutput("Enter your username and password (enter nothing to cancel)");

        String user, pass;
        int wrongCount = 0;
        int wrongMax = 3;
        boolean validLogin = false;

        do {

            UI.printOutput("Username: ", false);
            user = sc.nextLine();

            if (user.isEmpty()) {
                break;
            }

            UI.printOutput("Password: ", false);
            pass = sc.nextLine();

            if ( pass.isEmpty() ) {
                break;
            }

            validLogin = checkLogin(user, pass);

            if (!validLogin) {
                wrongCount++;
                UI.printOutput(
                    "The username and/or password is incorrect " +
                    "(Attempt " + wrongCount + "/" + wrongMax + ")"
                );
            }

        } while (!validLogin && wrongCount < wrongMax);

        if ( validLogin ) {

            UI.clearScreen();

            Menu mainMenu = new MainMenu(sc, user, isAdmin, conn);
            mainMenu.enter();

        } else if (wrongCount >= wrongMax) {

            UI.printOutput("Too many attempts! Returning...");

        } else {

            UI.printOutput("Returning...");

        }

    }

}
