package rpgcharacters.userflow;

import rpgcharacters.UI;

import java.sql.Connection;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class MainMenu implements Menu {

    private Scanner sc;
    private Connection conn;

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
    public MainMenu(Scanner sc, String username, boolean isAdmin, Connection conn) {
        this.sc = sc;
        this.username = username;
        this.isAdmin = isAdmin;
        this.conn = conn;

        if (isAdmin) {
            this.options = Arrays.asList(MENU_CHAR, MENU_PARTY, MENU_ADMIN, LOG_OUT);
        } else {
            this.options = Arrays.asList(MENU_CHAR, MENU_PARTY, LOG_OUT);
        }
    }

    /**
    * Defines the loop for this menu
    */
    public void enter() {
        UI.printMenuTitle( "Main Menu" );

        String option = "";
        int input = -1;

        do {

            UI.printOptions( options );

            input = UI.promptInt( sc, "Select an option: ",
                                  0, options.size() );
            option = options.get( input - 1);

            switch (option) {
                case MENU_CHAR:
                    Menu characterMenu = new CharacterMenu(sc, this.username, conn);
                    characterMenu.enter();

                    UI.printMenuTitle( "Main Menu" );
                    break;
                case MENU_PARTY:
                    Menu partyMenu = new PartyMenu(sc, this.username, conn);
                    partyMenu.enter();

                    UI.printMenuTitle( "Main Menu" );
                    break;
                case MENU_ADMIN:
                    Menu adminMenu = new AdminMenu(sc, conn);
                    adminMenu.enter();

                    UI.printMenuTitle( "Main Menu" );
                    break;
                case LOG_OUT:
                    UI.printOutput("\nLogging out...\n");
                    break;
                default:
                    UI.printOutput("\nInvalid input...\n");
            }

        } while (!option.equals(LOG_OUT));
    }

}
