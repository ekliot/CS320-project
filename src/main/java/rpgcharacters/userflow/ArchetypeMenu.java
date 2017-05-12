package rpgcharacters.userflow;

import rpgcharacters.UI;

import java.sql.*;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ArchetypeMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    private List<String> options;

    private final String ARCH_LIST = "List Archetypes";
    private final String ARCH_NEW  = "Create a new Archetype";
    private final String EXIT      = "Back to Admin Menu";

    public ArchetypeMenu(Scanner sc, Connection conn) {
        this.sc = sc;
        this.conn = conn;
        this.options = Arrays.asList( ARCH_LIST, ARCH_NEW, EXIT );
    }

    private void listArchetype() {
        try {
            // get every archetype, and the name of the quest that rewards it
            String query = "SELECT * "
                         + "FROM archetype";

            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);

            System.out.println( "\nArchetypes:" );
            System.out.println( "==============" );

            results.beforeFirst();

            while ( results.next() ) {
                String name  = results.getString("name");
                int[] mods = new int[]{
                    results.getInt( "power_mod" ),
                    results.getInt( "proficiency_mod" ),
                    results.getInt( "personality_mod" ),
                    results.getInt( "perception_mod" )
                };
                printArchetype( name, mods );
            }

            System.out.println( "" );
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    private void printArchetype( String name, int[] mods ) {
        System.out.println( "Archetype: " + name );
        System.out.println( "Stat Modifiers:" );
        UI.printStats( mods );

        UI.printDiv2();
    }

    public void newArchetype() {

        String name;
        boolean cancelling = false;
        boolean quit = false;

        // take input for archetype name (which CANNOT be null)

        do {
            System.out.print( "Enter archetype name: " );
            name = sc.nextLine();

            if ( name.isEmpty() ) {
                if ( cancelling ) {
                    quit = true;
                } else {
                    System.out.println( "Archetype name cannot be empty! (enter empty name again to cancel)" );
                    cancelling = true;
                }
            }

        } while ( name.isEmpty() && !quit );

        if ( quit ) {
            System.out.println( "Cancelled archetype creation...\n" );
            return;
        }

        // parse archetype stats input

        boolean stats_valid = false;
        String stat_input;
        String stat_input_regex = "-?\\d+ -?\\d+ -?\\d+ -?\\d+";
        String[] stats;

        int power_mod       = 0;
        int proficiency_mod = 0;
        int personality_mod = 0;
        int perception_mod  = 0;

        int stat_sum        = 0;
        int stat_balance    = 25;

        System.out.println( "Enter archetype's stat modifiers" );
        System.out.println( "Must format as four integers with a sum of 25 (e.g. `15 0 20 -10`)" );
        System.out.println( "Order: Power Proficiency Personality Perception" );
        System.out.println( "Enter nothing to cancel" );

        do {
            stat_input = sc.nextLine();

            if ( stat_input.isEmpty() ) {
                quit = true;
                break;
            }

            if ( java.util.regex.Pattern.matches( stat_input_regex, stat_input ) ) {

                stats = stat_input.split( " " );

                power_mod       = Integer.parseInt( stats[0] );
                proficiency_mod = Integer.parseInt( stats[1] );
                personality_mod = Integer.parseInt( stats[2] );
                perception_mod  = Integer.parseInt( stats[3] );

                stat_sum = power_mod + proficiency_mod + personality_mod + perception_mod;

                if ( stat_sum == stat_balance ) {
                    stats_valid = true;
                } else {
                    System.out.println( "Stats add up to invalid sum (got " + stat_sum + ", need " + stat_balance + "), try again" );
                }
            } else {
                System.out.println( "Input in invalid format, try again" );
            }
        } while ( !stats_valid );

        if ( quit ) {
            System.out.println( "Cancelled archetype creation...\n" );
            return;
        }

        // try to create the archetype

        try {
            String query = String.format(
                "INSERT INTO archetype VALUES ( '%s', %d, %d, %d, %d );",
                name.replaceAll("'", "''"), power_mod, proficiency_mod, personality_mod, perception_mod
            );

            Statement stmt = conn.createStatement();
            stmt.execute( query );

            System.out.println( "Archetype has been created!\n" );
            printArchetype( name, new int[]{
                power_mod, proficiency_mod, personality_mod, perception_mod
            } );
        } catch ( SQLException e ) {
            System.out.println( "Could not create archetype, " + name + "\n" );
            e.printStackTrace();
        }

    }

    public void enter() {

        UI.clearScreen();

        UI.printMenuTitle( "Archetypes Menu" );

        String option = "";
        int input = -1;

        do {

            UI.printOptions( options );

            input = UI.promptInt( sc, "Select an option: ",
                                  0, options.size() );
            option = options.get( input - 1 );

            switch ( option ) {
                case ARCH_LIST:
                    listArchetype();
                    break;
                case ARCH_NEW:
                    newArchetype();
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
