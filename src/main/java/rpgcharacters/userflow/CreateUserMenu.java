package rpgcharacters.userflow;

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

        // TODO: Modify to use SQL and have specific printouts for cases:
        // - Username already in use
        // - Other error occured
        //
        // Return true if no errors and use is created; False otherwise.

        return true;
    }

    /**
    * Defines the loop for this menu
    */
    public void enter() {
        sc.nextLine();
        boolean validUserInfo = false;
        int wrongCount = 0;

        do {

            System.out.print("Username: ");
            String user = sc.nextLine();

            System.out.print("Password: ");
            String pass = sc.nextLine();

            System.out.print("Confirm password: ");
            String confPass = sc.nextLine();

            if (pass.equals(confPass)) {
                validUserInfo = createUser(user, pass);
            }
            else {
                System.out.println("\nPasswords do not match. Please try again...\n");
            }

            wrongCount++;

        } while (!validUserInfo && wrongCount <= 3);

        if (validUserInfo) {
            System.out.println("\nUser has been created!\n");
        }
        else {
            System.out.println("\nToo many attempts... Returning...\n");
        }
    }

}
