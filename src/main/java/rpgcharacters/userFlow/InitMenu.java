package rpgcharacters.userflow;
import java.util.Scanner;
import java.util.InputMismatchException;

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
        System.out.println("-------------------------------------------------------");
    }

    private void printOptions () {
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
    public void enter () {
        printMenuTitle();
        int input;
        do {

            printOptions();
            try {
                input = sc.nextInt();

                switch (input) {
                    case 1:
                        Menu loginMenu = new LoginMenu(sc);
                        loginMenu.enter();
                        break;
                    case 2:
                        Menu createUserMenu = new CreateUserMenu(sc);
                        createUserMenu.enter();
                        break;
                    case 3:
                        System.out.println("\nExiting...\n");
                        break;
                    default:
                        System.out.println("\nInvalid input...\n");
                }
            }
            catch (InputMismatchException e) {
                continue;
            }

        } while (input != 3);
    }

}
