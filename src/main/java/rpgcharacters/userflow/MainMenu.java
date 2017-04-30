package rpgcharacters.userflow;
import java.util.Scanner;
import java.util.InputMismatchException;

import java.sql.Connection;

public class MainMenu implements Menu {

    private Scanner sc;

    private String username;
    private boolean isAdmin;

    /**
    * Constructor Method
    */
    public MainMenu (Scanner sc, String username, boolean isAdmin) {
        this.sc = sc;
        this.username = username;
        this.isAdmin = isAdmin;
    }

    private void printMenuTitle() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println("Main Menu");
        System.out.println("-------------------------------------------------------");
    }

    private void printOptions () {
        String optionsString = "Available options:\n" +
            "\t1: Character Menu\n" +
            "\t2: Party Menu\n";

        if (isAdmin) optionsString += "\t3: Admin Menu\n" +
                                    "\t4: Log Out\n";
        else optionsString += "\t3: Log Out\n";

        optionsString += "-------------------------------------------------------"; // 50 chars;
        System.out.println(optionsString);
        System.out.print("Please enter the number of the desired option here: ");
    }

    /**
    * Defines the loop for this menu
    */
    public void enter ( Connection conn ) {
        printMenuTitle();
        int input = 0;
        int exit = 3;
        if (isAdmin) exit = 4;
        do {

            printOptions();
            try {
                input = sc.nextInt();

                switch (input) {
                    case 1:
                        Menu characterMenu = new CharacterMenu(sc,username);
                        characterMenu.enter( conn );
                        break;
                    case 2:
                        Menu partyMenu = new PartyMenu(sc,username);
                        partyMenu.enter( conn );
                        break;
                    case 3:
                        if (isAdmin) {
                            System.out.println("\nAdmin Menu\n");
                        }
                        else {
                            System.out.println("\nLogging out...\n");
                        }
                        break;
                    case 4:
                        if (isAdmin) { //pass through to default if not admin.
                            System.out.println("\nLogging out...\n");
                            break;
                        }
                    default:
                        System.out.println("\nInvalid input...\n");
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\nInvalid input...\n");
                continue;
            }

        } while (input != exit);
    }

}
