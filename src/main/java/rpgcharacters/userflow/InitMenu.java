package rpgcharacters.userflow;

import java.util.Scanner;
import java.util.InputMismatchException;

import java.sql.Connection;

public class InitMenu implements Menu {

    private Scanner sc;

    /**
    * Constructor Method
    */
    public InitMenu () {
        this.sc = new Scanner(System.in);
    }

    private void printMenuTitle() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println("Init Menu");
    }

    private void printOptions () {
        System.out.println("-------------------------------------------------------");
        System.out.println(
            "Available options:\n" +
            "\t1: Login\n" +
            "\t2: Create User\n" +
            "\t3: Exit\n" +
            "-------------------------------------------------------" // 50 chars
        );
        System.out.print("Please enter the number of the desired option here: ");
    }

    /**
    * Defines the loop for this menu
    */
    public void enter ( Connection conn ) {

        // clear the screen
        final String ANSI_CLS = "\u001b[2J";
        final String ANSI_HOME = "\u001b[H";
        System.out.print(ANSI_CLS + ANSI_HOME);
        System.out.flush();

        printMenuTitle();
        int input = 0;

        do {

            printOptions();

            try {
                input = sc.nextInt();

                switch ( input ) {
                    case 1:
                        Menu loginMenu = new LoginMenu( sc );
                        loginMenu.enter( conn );
                        break;
                    case 2:
                        Menu createUserMenu = new CreateUserMenu( sc );
                        createUserMenu.enter( conn );
                        break;
                    case 3:
                        System.out.println( "\nExiting..." );
                        break;
                    default:
                        System.out.println( "\nInvalid input..." );
                }
            } catch ( InputMismatchException e ) {
                System.out.println( "\nInvalid input..." );
                continue;
            }

        } while ( input != 3 );

    }

}
