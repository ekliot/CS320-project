package rpgcharacters.userflow;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.InputMismatchException;

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
        System.out.println("\n-------------------------------------------------------");
        System.out.println("Character Menu");
        System.out.println("-------------------------------------------------------");
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
        charsString += "-------------------------------------------------------";

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

    private void deleteCharacter (String charName) {

        // TODO: Delete the character from the database.
        // Make appropriate print statements if something bad happens.

        System.out.println(charName + " has been deleted!");
    }

    private void removeFromParty (String charName) {

        // TODO: Characters can only have one party, so steps are as follows:
        // 1. Check if character is in party
        // 2. if the character is not in a party, make an appropriate print
        //    statement and return.
        // 3. else, get the party and remove the character from that party.
        // 4. Make appropriate print statements if something bad happens.

        System.out.println(charName + " has been deleted!");
    }

    public static void printCharacter (String charName, String username) {
        String story;
        String race;
        String arch;
        String party;
        int power;
        int proficiency;
        int personality;
        int percecption;
        int experience;

        // TODO: Retrieve real data!
        // Make appropriate print statements if something bad happens.

        story = "He did things and stuff";
        race = "Hooman";
        arch = "Hoodrat";
        party = "Fellowship of the ring";
        power = 9001;
        proficiency = 9001;
        personality = 9001;
        percecption = 9001;
        experience = 9001;

        // format story
        if (story.length() > 35) {
            String[] tokens = story.split(" ");
            story = "";
            int curLineLen = 0;
            for (String tok : tokens) {
                if (curLineLen == 0) {
                    story += "\n\t";
                }
                story += tok;
                curLineLen += tok.length();
                if (curLineLen > 40) {
                    curLineLen = 0;
                }
            }
        }


        String pString =
            "\n-------------------------------------------------------\n" + // 50 chars
            charName + "\n" +
            "-------------------------------------------------------\n" + // 50 chars
            "User:        " + username + "\n" +
            "Story:       " + story + "\n" +
            "Race:        " + race + "\n" +
            "Archetype:   " + arch + "\n" +
            "Party:       " + party + "\n" +
            "Power:       " + power + "\n" +
            "Proficiency: " + proficiency + "\n" +
            "Personality: " + personality + "\n" +
            "Percecption: " + percecption + "\n" +
            "Experience:  " + experience + "\n" +
            "-------------------------------------------------------\n";

        System.out.println(pString);
    }

    private void printOptions () {
        String optionsString =
            "Available options:\n" +
            "\t1: Create a new character\n" +
            "\t2: Print character\n" +
            "\t3: Delete character\n" +
            "\t4: Remove a character from a party.\n" +
            "\t5: Go back\n" +
            "-------------------------------------------------------"; // 50 chars;

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
            try {
                input = sc.nextInt();

                switch (input) {
                    case 1:
                        Menu createCharacterMenu = new CreateCharacterMenu(sc,username);
                        createCharacterMenu.enter();
                        break;
                    case 2:
                        character = printChars();
                        printCharacter(character,username);
                        break;
                    case 3:
                        character = printChars();
                        deleteCharacter(character);
                        break;
                    case 4:
                        character = printChars();
                        removeFromParty(character);
                        break;
                    case 5:
                        System.out.println("\nGoing back...\n");
                        break;
                    default:
                        System.out.println("\nInvalid input...\n");
                }
            }
            catch (InputMismatchException e) {
                continue;
            }



        } while (input != exit);
    }

}
