package rpgcharacters.userflow;

import rpgcharacters.UI;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class QuestMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    private List<String> options;

    private final String QUEST_LIST = "List Quests";
    private final String QUEST_NEW  = "Create a new Quest";
    private final String EXIT       = "Back to Admin Menu";

    public QuestMenu(Scanner sc, Connection conn) {
        this.sc = sc;
        this.conn = conn;
        this.options = Arrays.asList( QUEST_LIST, QUEST_NEW, EXIT );
    }

    private void listQuests() {
        try {
            // get every item, and the name of the quest that rewards it
            String query = "SELECT * "
                         + "FROM quest;";

            // these options to createStatement let us use beforeFirst() after using last()
            Statement stmt = conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY );
            ResultSet results = stmt.executeQuery( query );

            if ( !results.last() ) {
                UI.printOutput( "There are no quests in the database!" );
                return;
            }

            System.out.println();
            System.out.println( "Quests:" );
            UI.printDiv1();

            results.beforeFirst();

            while ( results.next() ) {
                String quest = results.getString( "name"        );
                String desc  = results.getString( "description" );
                int    exp   = results.getInt(    "experience"  );
                String item  = results.getString( "item_name"   );
                printQuest( quest, desc, exp, item );
            }

            System.out.println( "" );
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    private void printQuest ( String name, String desc, int exp, String item ) {
        System.out.println( name );
        System.out.println( "  Experience: " + exp );
        System.out.println( "  Item reward: " + item );
        System.out.println( "  Description:" );

        if ( !desc.isEmpty() ) {
            UI.printParagraph( desc, 46, 4 );
        }

        UI.printDiv2();
    }

    public void newQuest() {

        String name, description;
        int exp = 0;

        boolean cancelling = false;
        boolean quit = false;

        // take input for item name (which CANNOT be null)

        do {
            UI.printOutput( "Enter quest name: ", false );
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
            UI.printOutput( "Cancelled quest creation...\n" );
            return;
        }

        cancelling = false;

        // take input for quest description (which CAN be null)

        UI.printOutput( "Enter quest description: ", false );
        description = sc.nextLine();

        // take input for experience
        exp = UI.promptInt( sc, "Enter quest experience reward: ",
                            0, Integer.MAX_VALUE );

        // offer choice of choosing an unassigned item, or creating a new one

        UI.printOutput( "Select an item to assign as this quest's reward:" );
        String item_name = selectItem();

        // try to create the quest
        try {
            String query = "INSERT INTO quest VALUES ("
                         + "'" + name.replaceAll("'", "''")        + "', "
                         + "'" + description.replaceAll("'", "''") + "', "
                         + ""  + exp                               + ", "
                         + "'" + item_name.replaceAll("'", "''")   + "' "
                         + ");";

            Statement stmt = conn.createStatement();
            stmt.execute( query );

            UI.printOutput( "Quest \"" + name + "\" has been created!\n" );
        } catch ( SQLException e ) {
            UI.printOutput( "Could not create quest, \"" + name + "\"\n" );
            e.printStackTrace();
        }

    }

    private String selectItem() {

        String item;
        ArrayList<String> items = new ArrayList<String>();
        items.add( "Create a new item" );

        try {
            // get every item, and the name of the quest that rewards it
            String query = "SELECT I.name AS item "
                         + "FROM item AS I LEFT OUTER JOIN quest AS Q on I.name=Q.item_name "
                         + "WHERE Q.item_name IS NULL;";

            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery( query );

            results.beforeFirst();

            while ( results.next() ) {
                item = results.getString( "item" );
                items.add( item );
            }
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        UI.printOptions( items, "Unassigned items:" );

        int selection = -1;
        String selectedItem;

        // prompt and parse selection

        do {

            selection = UI.promptInt( sc, "Select numbered item: ",
                                      1, items.size() );

            // the first item of the list will always be "Create a new item"
            if ( selection == 1 ) {
                selectedItem = createItem();
            } else {
                selectedItem = items.get( selection - 1 );
            }

            // keep looping in case createItem() fails somehow
        } while ( selectedItem.isEmpty() );

        return selectedItem;

    }

    private String createItem() {

        String name, description;
        boolean cancelling = false;
        boolean quit = false;

        // take input for item name (which CANNOT be null)

        do {
            UI.printOutput( "Enter item name: ", false );
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
            UI.printOutput( "Cancelled item creation...\n" );
            return "";
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
            name = ""; // make empty string for error checking on receiving end of this method
        }

        return name;

    }

    public void enter() {
        UI.clearScreen();
        UI.printMenuTitle( "Quest Menu" );

        String option = "";
        int input = -1;

        do {

            UI.printOptions( options );
            input = UI.promptInt( sc, "Select an option: ",
                                  1, options.size() );
            option = options.get( input - 1 );

            switch ( option ) {
                case QUEST_LIST:
                    listQuests();
                    UI.printMenuTitle( "Quest Menu" );
                    break;
                case QUEST_NEW:
                    newQuest();
                    UI.printMenuTitle( "Quest Menu" );
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
