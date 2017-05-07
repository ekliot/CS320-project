package rpgcharacters.userflow;

import java.sql.Connection;
import java.util.Scanner;
import java.util.InputMismatchException;

public class InitMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    /**
    * Constructor Method
    */
    public InitMenu(Connection conn) {
        this.sc = new Scanner(System.in);
        this.conn = conn;
    }

    private void printMenuTitle() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println("RPG Characters Application");
    }

    private void printOptions() {
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
    public void enter() {
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

                switch (input) {
                    case 1:
                        Menu loginMenu = new LoginMenu(sc, conn);
                        loginMenu.enter();
                        break;
                    case 2:
                        Menu createUserMenu = new CreateUserMenu(sc, conn);
                        createUserMenu.enter();
                        break;
                    case 3:
                        System.out.println("\nExiting...");
                        break;
                    default:
                        System.out.println("\nInvalid input...");
                }
            } catch (InputMismatchException e) {
                System.out.println("\nInvalid input...");
                continue;
            }

        } while (input != 3);

    }

}
