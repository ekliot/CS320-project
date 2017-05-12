package rpgcharacters.userflow;

import rpgcharacters.UI;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class InitMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    private List<String> options;

    private final String LOGIN = "Login";
    private final String REGISTER = "Register";
    private final String EXIT = "Exit";

    /**
    * Constructor Method
    */
    public InitMenu(Connection conn) {
        this.sc = new Scanner(System.in);
        this.conn = conn;
        this.options = Arrays.asList( LOGIN, REGISTER, EXIT );
    }

    /**
    * Defines the loop for this menu
    */
    public void enter() {

        UI.clearScreen();

        UI.printMenuTitle( "arpeegee.io" );

        String option = "";
        int input = -1;

        do {

            UI.printOptions( options );

            input = UI.promptInt( sc, "Select an option: ",
                                  0, options.size() );
            option = options.get( input - 1 );

            switch ( option ) {
                case LOGIN:
                    Menu loginMenu = new LoginMenu(sc, conn);
                    loginMenu.enter();

                    UI.clearScreen();
                    UI.printMenuTitle( "arpeegee.io" );
                    break;
                case REGISTER:
                    Menu createUserMenu = new CreateUserMenu(sc, conn);
                    createUserMenu.enter();

                    UI.clearScreen();
                    UI.printMenuTitle( "arpeegee.io" );
                    break;
                case EXIT:
                    UI.printOutput("\nExiting...");
                    break;
                default:
                    UI.printOutput("\nInvalid input...");
            }

        } while ( !option.equals( EXIT ) );

    }

}
