package rpgcharacters.userflow;

import java.sql.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class GrantAdminMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    private List<String> options;

    private final String ADMIN_GRANT  = "Grant admin priviliges";
    private final String ADMIN_REVOKE = "Revoke admin priviliges";
    private final String EXIT         = "Back to Admin Menu";

    public GrantAdminMenu(Scanner sc, Connection conn) {
        this.sc = sc;
        this.conn = conn;
        this.options = Arrays.asList( ADMIN_GRANT, ADMIN_REVOKE, EXIT );
    }

    private ArrayList<String> listUsers() {

        ArrayList<String> users = new ArrayList<String>();

        try {
            String query = "SELECT username FROM user;";
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery( query );

            String optionsString = "Available users:\n";
            String optionFormat = "\t%d: %s\n";

            int i = 0;

            res.beforeFirst();

            while ( res.next() ) {
                optionsString += String.format( optionFormat, (i+1), options.get(i) );
                users.set( i, res.getString( "name" ) );
                i++;
            }

            if ( i > 0 ) {
                optionsString += "-------------------------------------------------------"; // 50 chars;

                System.out.println( optionsString );
            }

        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        return users;

    }

    private void adminGrant() {

        ArrayList<String> users = listUsers();

        if ( users.size() == 0 ) {
            System.out.println( "Found no users" );
            return;
        }

        int selection;

        boolean cancelling = false;
        boolean quit = false;

        do {
            sc.nextLine(); // scrubscrubscrub

            System.out.print( "Enter user's number: " );
            selection = sc.nextInt();

            if ( selection < 1 || selection > users.size() ) {
                if ( cancelling ) {
                    quit = true;
                } else {
                    System.out.println( "Invalid input (enter invalid input again to cancel)" );
                    cancelling = true;
                }
            } else {
                break;
            }

        } while ( !quit );

        String name = users.get( selection );

        try {
            String query = "GRANT dbAdmin "
                         + "TO (SELECT username "
                         +     "FROM user "
                         +     "WHERE username = '" + name.replaceAll("'", "''") + "');";
            Statement stmt = conn.createStatement();
            stmt.executeQuery( query );


            System.out.println( "Successfully granted admin privilege to " + name + "!" );
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

    }

    private void adminRevoke() {

        ArrayList<String> users = listUsers();

        if ( users.size() == 0 ) {
            System.out.println( "Found no users" );
            return;
        }

        int selection;

        boolean cancelling = false;
        boolean quit = false;

        do {
            sc.nextLine(); // scrubscrubscrub

            System.out.print( "Enter user's number: " );
            selection = sc.nextInt();

            if ( selection < 1 || selection > users.size() ) {
                if ( cancelling ) {
                    quit = true;
                } else {
                    System.out.println( "Invalid input (enter invalid input again to cancel)" );
                    cancelling = true;
                }
            } else {
                break;
            }

        } while ( !quit );

        String name = users.get(selection);

        try {
            String query = "REVOKE dbAdmin "
                         + "TO (SELECT username "
                         +     "FROM user "
                         +     "WHERE username = '" + name.replaceAll("'", "''") + "');";
            Statement stmt = conn.createStatement();
            stmt.executeQuery( query );

            System.out.println( "Successfully revoked admin privilege from " + name + "!" );
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

    }

    private void printMenuTitle() {
        System.out.println( "\n-------------------------------------------------------" );
        System.out.println( "Privilige Granting Menu" );
        System.out.println( "-------------------------------------------------------" );
    }

    private void printOptions () {
        String optionsString = "Available options:\n";
        String optionFormat = "\t%d: %s\n";

        for ( int i = 0; i < options.size(); i++ ) {
            optionsString += String.format( optionFormat, (i+1), options.get( i ) );
        }

        optionsString += "-------------------------------------------------------"; // 50 chars;

        System.out.println( optionsString );

        System.out.print( "Please enter the number of the desired option here: " );
    }

    public void enter() {

        printMenuTitle();

        String option = "";
        int input = -1;

        do {

            printOptions();

            try {
                input = sc.nextInt();

                if ( input <= 0 || input > options.size() ) {
                    option = "";
                } else {
                    option = options.get( input - 1 );
                }

                // swallow the next line, as it would auto complete on entering newItem()
                // ref: http://stackoverflow.com/questions/7877529/java-string-scanner-input-does-not-wait-for-info-moves-directly-to-next-stateme
                sc.nextLine();

                switch ( option ) {
                    case ADMIN_GRANT:
                        adminGrant();
                        break;
                    case ADMIN_REVOKE:
                        adminRevoke();
                        break;
                    case EXIT:
                        System.out.println( "\nGoing back...\n" );
                        break;
                    default:
                        System.out.println( "\nInvalid input...\n" );
                }
            } catch ( InputMismatchException e ) {
                System.out.println( "\nInvalid input...\n" );
                continue;
            }

        } while ( !option.equals( EXIT ) );
    }

}
