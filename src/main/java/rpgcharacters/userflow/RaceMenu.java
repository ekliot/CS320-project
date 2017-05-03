package rpgcharacters.userflow;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RaceMenu implements Menu {

    private Connection conn;

    private Scanner sc;

    private List<String> options;

    private final String RACE_LIST = "List Races";
    private final String RACE_NEW  = "Create a new Race";
    private final String EXIT      = "Back to Admin Menu";

    public RaceMenu( Scanner sc ) {
        this.sc = sc;
        this.options = Arrays.asList( RACE_LIST, RACE_NEW, EXIT );
    }

    private void listRaces() {
        try {
            // get every race, and the name of the quest that rewards it
            String query = "SELECT *"
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
        System.out.println( "  POWER : PROFICIENCY : PERSONALITY : PERCEPTION" );
        String modFormat =  "   %3d  :     %3d     :     %3d     :    %3d    ";
        System.out.println( String.format( modFormat,
                              mods[0],   mods[1],      mods[2],     mods[3] )
        );

        System.out.println( "-------------------------------------------------------" ); // length 50
    }

    public void newRace() {

        boolean success = false;
        String name;
        int power_mod       = 0;
        int proficiency_mod = 0;
        int personality_mod = 0;
        int perception_mod  = 0;
        boolean cancelling = false;
        boolean quit = false;

        // take input for race name (which CANNOT be null)

        do {
            System.out.print( "Enter race name: " );
            name = sc.nextLine();

            if ( name.isEmpty() ) {
                if ( cancelling ) {
                    quit = true;
                } else {
                    System.out.println( "Race name cannot be empty! (enter empty name again to cancel)" );
                    cancelling = true;
                }
            }

        } while ( name.isEmpty() && !quit );

        if ( quit ) {
            System.out.println( "Cancelled race creation...\n" );
            return;
        }

        cancelling = false;

        // take input for power modifier

        boolean loop = true;

        do {
            try {
                System.out.print( "Enter archetype's power modifier: " );
                power_mod = sc.nextInt();
                loop = false;
            } catch ( InputMismatchException e ) {
                System.out.println( "Invalid input..." );
                // scrub the Scanner
                sc.nextLine();
            }
        } while ( loop );


        // take input for proficiency modifier

        loop = true;

        do {
            try {
                System.out.print( "Enter archetype's proficiency modifier: " );
                proficiency_mod = sc.nextInt();
                loop = false;
            } catch ( InputMismatchException e ) {
                System.out.println( "Invalid input..." );
                // scrub the Scanner
                sc.nextLine();
            }
        } while ( loop );

        // take input for power modifier

        loop = true;

        do {
            try {
                System.out.print( "Enter archetype's personality modifier: " );
                personality_mod = sc.nextInt();
                loop = false;
            } catch ( InputMismatchException e ) {
                System.out.println( "Invalid input..." );
                // scrub the Scanner
                sc.nextLine();
            }
        } while ( loop );

        // take input for power modifier

        loop = true;

        do {
            try {
                System.out.print( "Enter archetype's perception modifier: " );
                perception_mod = sc.nextInt();
                loop = false;
            } catch ( InputMismatchException e ) {
                System.out.println( "Invalid input..." );
                // scrub the Scanner
                sc.nextLine();
            }
        } while ( loop );

        // try to create the race

        try {
            String query = String.format(
                "INSERT INTO race VALUES ( '%s', %d, %d, %d, %d );",
                name, power_mod, proficiency_mod, personality_mod, perception_mod
            );

            Statement stmt = conn.createStatement();
            stmt.execute( query );

            System.out.println( "Race has been created!\n" );
            printRace( name, new int[]{
                power_mod, proficiency_mod, personality_mod, perception_mod
            } );
            success = true;
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        if ( !success ) {
            System.out.println( "Could not create race, " + name + "\n" );
        }

    }

    private void printMenuTitle() {
        System.out.println( "\n-------------------------------------------------------" );
        System.out.println( "Race Menu" );
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

                // swallow the next line, as it would auto complete on entering newRace()
                // ref: http://stackoverflow.com/questions/7877529/java-string-scanner-input-does-not-wait-for-info-moves-directly-to-next-stateme
                sc.nextLine();

                switch ( option ) {
                    case RACE_LIST:
                        listRaces();
                        break;
                    case RACE_NEW:
                        newRace();
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
