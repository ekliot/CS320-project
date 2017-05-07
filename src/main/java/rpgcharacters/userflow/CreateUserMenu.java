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
        try {
            String query = "INSERT INTO user VALUES ("
                         + "'" + user.replaceAll("'", "''") + "',"
                         + "'" + pass.replaceAll("'", "''") + "');";
            Statement stmt = conn.createStatement();
            stmt.execute(query);
            
            query = "SELECT COUNT( * ) AS userCount;";
            ResultSet result = stmt.executeQuery(query);

            if (result.getInt("userCount") == 1) {
                query = "GRANT dbAdmin "
                      + "TO (SELECT username "
                      +     "FROM user "
                      +     "WHERE username = '" + user.replaceAll("'", "''") + "');";
            } else {
                query = "GRANT dbUser "
                      + "TO (SELECT username "
                      +     "FROM user "
                      +     "WHERE username = '" + user.replaceAll("'", "''") + "');";
            }

            stmt.executeQuery(query);
            
            return true;
        } catch (SQLException e) {
            if (e.getMessage().startsWith("Unique index or primary key violation")) {
                System.out.println("\nUser already exists!\n");
            } else {
                e.printStackTrace();
            }
            return false;
        }
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
