package rpgcharacters.userflow;

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
        System.out.println( "  POWER : PROFICIENCY : PERSONALITY : PERCEPTION" );
        String modFormat =  "   %3d  :     %3d     :     %3d     :    %3d    ";
        System.out.println( String.format( modFormat,
                              mods[0],   mods[1],      mods[2],     mods[3] )
        );

        System.out.println( "-------------------------------------------------------" ); // length 50
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
                name, power_mod, proficiency_mod, personality_mod, perception_mod
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

    private void printMenuTitle() {
        System.out.println( "\n-------------------------------------------------------" );
        System.out.println( "Archetype Menu" );
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

                // swallow the next line, as it would auto complete on entering newArchetype()
                // ref: http://stackoverflow.com/questions/7877529/java-string-scanner-input-does-not-wait-for-info-moves-directly-to-next-stateme
                sc.nextLine();

                switch ( option ) {
                    case ARCH_LIST:
                        listArchetype();
                        break;
                    case ARCH_NEW:
                        newArchetype();
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
