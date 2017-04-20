package rpgcharacters.userflow;
import java.util.Scanner;
import java.lang.Boolean;
import java.util.HashMap;
import java.util.ArrayList;

public class CreateCharacterMenu implements Menu {

    private Scanner sc;

    private String username;

    private int curMaxAdd;

    /**
     * Constructor Method
     * @param  Scanner sc    scanner inherited from the parent menu.
     */
    public CreateCharacterMenu (Scanner sc,String username) {
        this.sc = sc;
        this.username = username;
    }

    private void printMenuTitle() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println("Create Character");
        System.out.println("-------------------------------------------------------");
    }

    private boolean saveToDB (String name, String story, String race, String archetype, int[] stats) {

        // TODO: Modify to use SQL and have specific printouts for cases:
        // - Username already in use
        // - Other error occured
        //
        // Return true if no errors and use is created; False otherwise.

        return true;
    }

    private int[] getCurStats (String charRace, String charArch) {

        // TODO: Get these values from the db:
        // Order: [power, proficiency, personality, perception]
        int[] raceMods = {5,10,10,0};
        int[] archMods = {5,10,5, 5};

        int[] totalMods = new int[4];
        for (int i = 0; i < 4; i++) {
            totalMods[i] = raceMods[i] + archMods[i];
        }
        return totalMods;
    }

    private int getStat(String statName, int curStatVal) {
        int maxMod = Math.min(curMaxAdd,100 - curStatVal);
        if (maxMod == 0) return curStatVal;

        System.out.println("Character's current " + statName + " stat: " + curStatVal);
        System.out.print("Additional " + statName + " modifier (0 - " + maxMod + "): ");
        int statMod = sc.nextInt();
        while (statMod < 0 || statMod > maxMod) {
            System.out.println("\nInvalid input...\n");
            System.out.println("Character's current " + statName + " stat: " + curStatVal);
            System.out.print("Additional " + statName + " modifier (0 - " + maxMod + "): ");
            statMod = sc.nextInt();
        }
        curMaxAdd -= statMod;
        return curStatVal + statMod;
    }

    private boolean createCharacter () {

        ArrayList<String> charAttributes = new ArrayList<String>();

        System.out.print("Character Name: ");
        String charName = sc.nextLine();

        System.out.print("Character Backstory: ");
        String charStory = sc.nextLine();

        System.out.print("Character Race: ");
        String charRace = sc.nextLine();

        System.out.print("Character Archetype: ");
        String charArch = sc.nextLine();

        int[] curStats = getCurStats(charRace,charArch);

        curMaxAdd = 20;
        curStats[0] = getStat("power",curStats[0]);
        curStats[1] = getStat("proficiency",curStats[1]);
        curStats[2] = getStat("personality",curStats[2]);
        curStats[3] = getStat("perception",curStats[3]);

        return saveToDB(charName,charStory,charRace,charArch,curStats);
    }

    /**
    * Defines the loop for this menu
    */
    public void enter () {
        printMenuTitle();
        sc.nextLine();
        boolean validCharInfo = false;
        int wrongCount = 0;

        do {

            validCharInfo = createCharacter();

            if (!validCharInfo) {
                System.out.println("\nAn error has occured while saving to the database.");
                System.out.println("Please try again...\n");
            }

            wrongCount++;

        } while (!validCharInfo && wrongCount <= 3);

        if (validCharInfo) {
            System.out.println("\nUser has been created!\n");
        }
        else {
            System.out.println("\nToo many attempts... Returning...\n");
        }
    }

}
