package rpgcharacters.userflow;

import rpgcharacters.UI;

import java.sql.*;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class RaceMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    private List<String> options;

    private final String RACE_LIST = "List Races";
    private final String RACE_NEW  = "Create a new Race";
    private final String EXIT      = "Back to Admin Menu";

    public RaceMenu(Scanner sc, Connection conn) {
        this.sc = sc;
        this.conn = conn;
        this.options = Arrays.asList( RACE_LIST, RACE_NEW, EXIT );
    }

    private void listRaces() {
        try {
            // get every race, and the name of the quest that rewards it
            String query = "SELECT * "
                         + "FROM race";

            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);

            System.out.println( "\nRaces:" );
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
                printRace( name, mods );
            }

            System.out.println( "" );
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    private void printRace( String name, int[] mods ) {
        System.out.println( "Race: " + name );
        System.out.println( "Stat Modifiers:" );
        UI.printStats( mods );

        UI.printDiv2();
    }

    public void newRace() {

        String name;
        boolean cancelling = false;
        boolean quit = false;

        // take input for race name (which CANNOT be null)

        do {
            UI.printOutput( "Enter race name: ", false );
            name = sc.nextLine();

            if ( name.isEmpty() ) {
                if ( cancelling ) {
                    quit = true;
                } else {
                    UI.printOutput( "Race name cannot be empty! (enter empty name again to cancel)" );
                    cancelling = true;
                }
            }

        } while ( name.isEmpty() && !quit );

        if ( quit ) {
            UI.printOutput( "Cancelled race creation..." );
            return;
        }

        // parse race stats input

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

        UI.printOutput( "Enter race's stat modifiers" );
        UI.printOutput( "Must format as four integers with a sum of 25 (e.g. `15 0 20 -10`)" );
        UI.printOutput( "Order: Power Proficiency Personality Perception" );
        UI.printOutput( "Enter nothing to cancel" );

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
                    UI.printOutput( "Stats add up to invalid sum (got " + stat_sum + ", need " + stat_balance + "), try again" );
                }
            } else {
                UI.printOutput( "Input in invalid format, try again" );
            }
        } while ( !stats_valid );

        if ( quit ) {
            UI.printOutput( "Cancelled race creation...\n" );
            return;
        }

        // try to create the race

        try {
            String query = String.format(
                "INSERT INTO race VALUES ( '%s', %d, %d, %d, %d );",
                name.replaceAll("'", "''"), power_mod, proficiency_mod, personality_mod, perception_mod
            );

            Statement stmt = conn.createStatement();
            stmt.execute( query );

            UI.printOutput( "Race has been created!\n" );
            printRace( name, new int[]{
                power_mod, proficiency_mod, personality_mod, perception_mod
            } );

            System.out.println();
        } catch ( SQLException e ) {
            UI.printOutput( "Could not create race, " + name );
            e.printStackTrace();
        }

    }

    public void enter() {
        UI.clearScreen();
        UI.printMenuTitle( "Race Menu" );

        String option = "";
        int input = -1;

        do {

            UI.printOptions( options );
            input = UI.promptInt( sc, "Select an option: ",
                                  1, options.size() );
            option = options.get( input - 1 );

            switch ( option ) {
                case RACE_LIST:
                    listRaces();
                    UI.printMenuTitle( "Race Menu" );
                    break;
                case RACE_NEW:
                    newRace();
                    UI.printMenuTitle( "Race Menu" );
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
