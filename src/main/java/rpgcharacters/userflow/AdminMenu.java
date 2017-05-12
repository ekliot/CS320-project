package rpgcharacters.userflow;

import rpgcharacters.UI;

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
    // private final String MENU_GRANT = "Admin-ify a user";
    private final String EXIT       = "Return to Main Menu";

    public AdminMenu( Scanner sc, Connection conn ) {
        this.sc = sc;
        this.conn = conn;
        this.options = Arrays.asList( MENU_ITEM, MENU_QUEST, MENU_RACE, MENU_ARCH, EXIT );
    }

    public void enter() {

        UI.clearScreen();
        UI.printMenuTitle( "Admin Menu" );

        String option = "";
        int input = -1;

        do {

            UI.printOptions( options );

            input = UI.promptInt( sc, "Select an option: ",
                                  0, options.size() );
            option = options.get( input - 1 );

            switch ( option ) {
                case MENU_ITEM:
                    Menu itemMenu = new ItemMenu( sc, conn );
                    itemMenu.enter();

                    UI.printMenuTitle( "Admin Menu" );
                    break;
                case MENU_QUEST:
                    Menu questMenu = new QuestMenu( sc, conn );
                    questMenu.enter();

                    UI.printMenuTitle( "Admin Menu" );;
                    break;
                case MENU_RACE:
                    Menu raceMenu = new RaceMenu( sc, conn );
                    raceMenu.enter();

                    UI.printMenuTitle( "Admin Menu" );
                    break;
                case MENU_ARCH:
                    Menu archetypeMenu = new ArchetypeMenu( sc, conn );
                    archetypeMenu.enter();

                    UI.printMenuTitle( "Admin Menu" );
                    break;
                // case MENU_GRANT:
                //     Menu grantAdminMenu = new GrantAdminMenu( sc, conn );
                //     grantAdminMenu.enter();
                //
                //     UI.printMenuTitle( "Admin Menu" );
                //     break;
                case EXIT:
                    UI.printOutput( "\nGoing back...\n" );
                    break;
                default:
                    UI.printOutput( "\nInvalid input...\n" );
            }

        } while ( !option.equals( EXIT ) );
    }

}
