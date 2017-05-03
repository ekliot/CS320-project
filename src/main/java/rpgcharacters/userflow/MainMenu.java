package rpgcharacters.userflow;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import java.sql.Connection;

public class MainMenu implements Menu {

    private Scanner sc;

    private String username;
    private boolean isAdmin;

    private List<String> options;

    private final String MENU_CHAR  = "Character Menu";
    private final String MENU_PARTY = "Party Menu";
    private final String MENU_ADMIN = "Admin Menu";
    private final String LOG_OUT    = "Log Out";

    /**
    * Constructor Method
    */
    public MainMenu (Scanner sc, String username, boolean isAdmin) {
        this.sc = sc;
        this.username = username;
        this.isAdmin = isAdmin;

        if ( isAdmin ) {
            this.options = Arrays.asList( MENU_CHAR, MENU_PARTY, MENU_ADMIN, LOG_OUT );
        } else {
            this.options = Arrays.asList( MENU_CHAR, MENU_PARTY, LOG_OUT );
        }
    }

    private void printMenuTitle() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println("Main Menu");
        System.out.println("-------------------------------------------------------");
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

    /**
    * Defines the loop for this menu
    */
    public void enter ( Connection conn ) {
        printMenuTitle();

        String option = "";
        int input = -1;
        String exit = LOG_OUT;

        do {

            printOptions();

            try {
                input = sc.nextInt();

                if ( input <= 0 || input > options.size() ) {
                    option = "";
                } else {
                    option = options.get( input - 1 );
                }

                switch ( option ) {
                    case MENU_CHAR:
                        Menu characterMenu = new CharacterMenu( sc, username );
                        characterMenu.enter( conn );
                        break;
                    case MENU_PARTY:
                        Menu partyMenu = new PartyMenu( sc, username );
                        partyMenu.enter( conn );
                        break;
                    case MENU_ADMIN:
                        // Menu adminMenu = new AdminMenu( sc );
                        // adminMenu.enter( conn );
                        break;
                    case LOG_OUT:
                        System.out.println( "\nLogging out...\n" );
                        break;
                    default:
                        System.out.println( "\nInvalid input...\n" );
                }
            } catch ( InputMismatchException e ) {
                System.out.println( "\nInvalid input...\n" );
                continue;
            }

        } while ( !option.equals( LOG_OUT ) );
    }

}
