package rpgcharacters.userflow;

import java.sql.Connection;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class AdminMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    private List<String> options;

    private final String MENU_ITEM  = "Item Options";
    private final String MENU_QUEST = "Quest Options";
    private final String MENU_RACE  = "Race Options";
    private final String MENU_ARCH  = "Archetype Options";
    private final String MENU_GRANT = "Admin-ify a user";
    private final String EXIT       = "Back to Main Menu";

    public AdminMenu(Scanner sc, Connection conn) {
        this.sc = sc;
        this.conn = conn;
        this.options = Arrays.asList( MENU_ITEM, MENU_QUEST, MENU_RACE, MENU_ARCH, MENU_GRANT, EXIT );
    }

    private void printMenuTitle() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println("Admin Menu");
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

                switch ( option ) {
                    case MENU_ITEM:
                        Menu itemMenu = new ItemMenu(sc, conn);
                        itemMenu.enter();
                        break;
                    case MENU_QUEST:
                        Menu questMenu = new QuestMenu(sc, conn);
                        questMenu.enter();
                        break;
                    case MENU_RACE:
                        Menu raceMenu = new RaceMenu(sc, conn);
                        raceMenu.enter();
                        break;
                    case MENU_ARCH:
                        Menu archetypeMenu = new ArchetypeMenu(sc, conn);
                        archetypeMenu.enter();
                        break;
                    case MENU_GRANT:
                        // Menu grantAdminMenu = new GrantAdminMenu(sc, conn);
                        // grantAdminMenu.enter();
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
