package rpgcharacters.userflow;

import java.util.Scanner;
import java.util.ArrayList;

public class CharacterMenu implements Menu {

    private Scanner sc;

    private String username;

    /**
    * Constructor Method
    */
    public CharacterMenu (Scanner sc, String username) {
        this.sc = sc;
        this.username = username;
    }

    private void printMenuTitle() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("Character Menu");
        System.out.println("--------------------------------------------------");
    }

    private String printChars () {
        String charsString = "Your characters:\n";

        ArrayList<String> characters = new ArrayList<String>();
        // TODO: replace with real data from sql db
        characters.add("Bob the builder");
        characters.add("Bilbo Baggins");
        characters.add("Nemo the Fish");

        for (int i = 0; i < characters.size(); i++) {
            charsString += "\t" + (i+1) + ". " + characters.get(i) + "\n";
        }
        charsString += "--------------------------------------------------";

        System.out.println(charsString);
        System.out.print("Please enter the number of the desired character here: ");
        int input = sc.nextInt();

        while (input < 1 || input > characters.size()) {
            System.out.println("\nInvalid input!\n");
            System.out.print("Please enter the number of the desired character here: ");
            input = sc.nextInt();
        }

        return characters.get(input-1);
    }

    private void printOptions () {
        String optionsString =
            "Available options:\n" +
            "\t1: Create a new character\n" +
            "\t2: Print character\n" +
            "\t3: Delete character\n" +
            "\t4: Remove a character from a party.\n" +
            "\t5: Go back\n" +
            "--------------------------------------------------"; // 50 chars;

        System.out.println(optionsString);
        System.out.print("Please enter the number of the desired option here: ");
    }

    /**
    * Defines the loop for this menu
    */
    public void enter () {
        printMenuTitle();
        int input;
        int exit = 5;
        String character;
        do {

            printOptions();
            input = sc.nextInt();

            switch (input) {
                case 1:
                    System.out.println("\nCreate Character\n");
                    break;
                case 2:
                    character = printChars();
                    System.out.println("\nPrint " + character + "\n");
                    break;
                case 3:
                    character = printChars();
                    System.out.println("\nDelete " + character + "\n");
                    break;
                case 4:
                    character = printChars();
                    System.out.println("\nRemove " + character + " from a party\n");
                    break;
                case 5:
                    System.out.println("\nGoing back...\n");
                    break;
                default:
                    System.out.println("\nInvalid input...\n");
            }

        } while (input != exit);
    }

}
