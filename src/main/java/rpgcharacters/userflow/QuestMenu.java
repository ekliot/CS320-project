package rpgcharacters.userflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QuestMenu implements Menu {

    private Connection conn;

    private Scanner sc;

    private List<String> options;

    private final String QUEST_LIST = "List Quests";
    private final String QUEST_NEW  = "Create a new Quest";
    private final String EXIT       = "Back to Admin Menu";

    public QuestMenu( Scanner sc ) {
        this.sc = sc;
        this.options = Arrays.asList( QUEST_LIST, QUEST_NEW, EXIT );
    }

    private void listQuests() {
        try {
            // get every item, and the name of the quest that rewards it
            String query = "SELECT * "
                         + "FROM quest;";

            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery( query );

            System.out.println( "\nQuests:" );
            System.out.println( "==============" );

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
        System.out.println( "Name: " + name );
        System.out.println( "Experience reward: " + exp );
        System.out.println( "Item reward: " + item );
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

    public void newQuest() {

        String name, description;
        int exp = 0;

        boolean cancelling = false;
        boolean quit = false;

        // take input for item name (which CANNOT be null)

        do {
            System.out.print( "Enter quest name: " );
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
            System.out.println( "Cancelled quest creation...\n" );
            return;
        }

        cancelling = false;

        // take input for quest description (which CAN be null)

        System.out.print( "Enter quest description: " );
        description = sc.nextLine();

        // take input for experience

        boolean loop = true;

        do {
            try {
                System.out.print( "Enter quest experience reward: " );
                exp = sc.nextInt();
                loop = false;
            } catch ( InputMismatchException e ) {
                System.out.println( "Invalid input..." );
                // scrub the Scanner
                sc.nextLine();
            }
        } while ( loop );

        // offer choice of choosing an unassigned item, or creating a new one

        System.out.println( "Select an item to assign as this quest's reward:" );
        String item_name = selectItem();

        // try to create the quest

        try {
            String query = "INSERT INTO quest VALUES ("
                         + "'" + name        + "', "
                         + "'" + description + "', "
                         + ""  + exp         + ", "
                         + "'" + item_name   + "' "
                         + ");";

            Statement stmt = conn.createStatement();
            stmt.execute( query );

            System.out.println( "Quest \"" + name + "\" has been created!\n" );
        } catch ( SQLException e ) {
            System.out.println( "Could not create quest, \"" + name + "\"\n" );
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

        printItemSelection( items );

        int selection = -1;
        String selectedItem;

        // prompt and parse selection

        do {

            do {
                try {
                    System.out.print( "Enter selected item: " );
                    selection = sc.nextInt();
                } catch ( InputMismatchException e ) {
                    System.out.println( "Invalid input..." );
                    // scrub the Scanner
                    sc.nextLine();
                }
                // keep looping until a valid value is selected
            } while ( selection < 0 || selection >= items.size() );

            // the first item of the list will always be "Create a new item"
            if ( selection == 0 ) {
                // scrub the Scanner
                sc.nextLine();
                selectedItem = createItem();
            } else {
                selectedItem = items.get( selection );
            }

            // keep looping in case createItem() fails somehow
        } while ( selectedItem.isEmpty() );


        return selectedItem;

    }

    private void printItemSelection( List<String> items ) {

        String itemOptions      = "Unassigned items:\n";
        String itemOptionFormat = "  %d: %s\n";

        for ( int i = 0; i < items.size(); i++ ) {
            itemOptions += String.format( itemOptionFormat, i, items.get( i ) );
        }

        System.out.println( itemOptions );

    }

    private String createItem() {

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
            return "";
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
        } catch ( SQLException e ) {
            System.out.println( "Could not create item, " + name + "\n" );
            e.printStackTrace();
            name = ""; // make empty string for error checking on receiving end of this method
        }

        return name;

    }

    private void printMenuTitle() {
        System.out.println( "\n-------------------------------------------------------" );
        System.out.println( "Quest Menu" );
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
                    case QUEST_LIST:
                        listQuests();
                        break;
                    case QUEST_NEW:
                        newQuest();
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
