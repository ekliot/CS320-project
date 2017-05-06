package rpgcharacters.userflow;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ItemMenu implements Menu {

    private Connection conn;

    private Scanner sc;

    private List<String> options;

    private final String ITEM_LIST = "List Items";
    private final String ITEM_NEW  = "Create a new Item";
    private final String EXIT      = "Back to Admin Menu";

    public ItemMenu( Scanner sc ) {
        this.sc = sc;
        this.options = Arrays.asList( ITEM_LIST, ITEM_NEW, EXIT );
    }

    private void listItems() {

        String item, quest, desc;

        try {
            // get every item, and the name of the quest that rewards it
            String query = "SELECT I.name AS item, I.description AS description, Q.name AS quest "
                         + "FROM item AS I LEFT OUTER JOIN quest AS Q on I.name=Q.item_name;";

            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);

            System.out.println( "\nCurrent Items:" );
            System.out.println( "==============" );

            results.beforeFirst();

            while ( results.next() ) {
                item  = results.getString("item");
                quest = results.getString("quest");
                desc  = results.getString("description");
                printItem( item, quest, desc );
            }

            System.out.println( "" );
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    private void printItem ( String name, String quest, String desc ) {
        System.out.println( "Name: " + name );
        // TODO this line is being finicky
        System.out.println( "Rewarded for: " + ( quest == null ? "Not rewarded" : quest ) );
        System.out.println( "Description:" );

        if ( !desc.isEmpty() ) {
            String[] descTokens = desc.split(" ");
            String indent = "  ";
            int curLen = indent.length();

            System.out.print( indent );

            for ( String tok : descTokens ) {
                if ( curLen + tok.length() > 46 ) {
                    System.out.print( "\n" + indent + tok );
                    curLen = indent.length() + tok.length();
                }
                else {
                    System.out.print( tok + " " );
                    curLen += tok.length() + 1;
                }
            }

            System.out.println( "" );
        }

        System.out.println( "-------------------------------------------------------" ); // length 50
    }

    private void newItem() {

        boolean success = false;
        String name, description;
        boolean cancelling = false;
        boolean quit = false;

        // take input for item name (which CANNOT be null)

        do {
            System.out.print( "Enter item name: " );
            name = sc.nextLine();

            if ( name.isEmpty() ) {
                if ( cancelling ) {
                    quit = true;
                } else {
                    System.out.println( "Item name cannot be empty! (enter empty name again to cancel)" );
                    cancelling = true;
                }
            }

        } while ( name.isEmpty() && !quit );

        if ( quit ) {
            System.out.println( "Cancelled item creation...\n" );
            return;
        }

        cancelling = false;

        // take input for item description (which CAN be null)

        System.out.print( "Enter item description: " );
        description = sc.nextLine();

        // try to create the item

        try {
            String query = "INSERT INTO item VALUES ("
                         + "'" + name + "', "
                         + "'" + description + "'"
                         + ");";

            Statement stmt = conn.createStatement();
            stmt.execute( query );

            System.out.println( "Item " + name + " has been created!\n" );
            success = true;
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        if ( !success ) {
            System.out.println( "Could not create item, " + name + "\n" );
        }

    }

    private void printMenuTitle() {
        System.out.println( "\n-------------------------------------------------------" );
        System.out.println( "Item Menu" );
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

    public void enter( Connection conn ) {
        this.conn = conn;

        printMenuTitle();

        String option = "";
        int input = -1;
        String exit = EXIT;

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
                    case ITEM_LIST:
                        listItems();
                        break;
                    case ITEM_NEW:
                        newItem();
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
