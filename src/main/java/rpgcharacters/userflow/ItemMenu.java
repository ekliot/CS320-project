package rpgcharacters.userflow;

import rpgcharacters.UI;

import java.sql.*;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ItemMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    private List<String> options;

    private final String ITEM_LIST = "List Items";
    private final String ITEM_NEW  = "Create a new Item";
    private final String EXIT      = "Back to Admin Menu";

    public ItemMenu(Scanner sc, Connection conn) {
        this.sc = sc;
        this.conn = conn;
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
            // e.printStackTrace();
            UI.printOutput( "There was an error querying items" );
        }
    }

    private void printItem ( String name, String quest, String desc ) {
        System.out.println( "Name: " + name );
        System.out.println( "Rewarded for: " + ( quest == null ? "Not rewarded" : quest ) );
        System.out.println( "Description:" );

        UI.printParagraph( desc, 46, 4 );

        UI.printDiv2();
    }

    private void newItem() {

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
                    UI.printOutput( "Item name cannot be empty! (enter empty name again to cancel)" );
                    cancelling = true;
                }
            }

        } while ( name.isEmpty() && !quit );

        if ( quit ) {
            UI.printOutput( "Cancelled item creation..." );
            return;
        }

        cancelling = false;

        // take input for item description (which CAN be null)

        UI.printOutput( "Enter item description: ", false );
        description = sc.nextLine();

        // try to create the item

        try {
            String query = "INSERT INTO item VALUES ("
                         + "'" + name.replaceAll("'", "''") + "', "
                         + "'" + description.replaceAll("'", "''") + "'"
                         + ");";

            Statement stmt = conn.createStatement();
            stmt.execute( query );

            UI.printOutput( "Item " + name + " has been created!" );
        } catch ( SQLException e ) {
            UI.printOutput( "Could not create item, " + name );
            // e.printStackTrace();
        }

    }

    public void enter() {
        UI.printMenuTitle( "Item Menu" );

        String option = "";
        int input = -1;

        do {

            UI.printOptions( options );

            input = UI.promptInt( sc, "Select an option: ",
                                  1, options.size() );
            option = options.get( input - 1 );

            switch ( option ) {
                case ITEM_LIST:
                    listItems();
                    break;
                case ITEM_NEW:
                    newItem();
                    break;
                case EXIT:
                    UI.printOutput( "Going back..." );
                    break;
                default:
                    UI.printOutput( "Invalid input..." );
            }

        } while ( !option.equals( EXIT ) );
    }

}
