package rpgcharacters.userflow;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.InputMismatchException;

public class EditPartyQuestsMenu implements Menu {

    private Scanner sc;

    private String username;
    private String partyName;

    /**
     * Constructor Method
     */
    public EditPartyQuestsMenu (Scanner sc, String username, String partyName) {
        this.sc = sc;
        this.username = username;
        this.partyName = partyName;
    }

    private void printMenuTitle() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println(partyName + "'s quests Menu");
        System.out.println("-------------------------------------------------------");
    }

    private String printQuests() {


        ArrayList<String> available = new ArrayList<String>();
        // TODO: replace with real data from sql db
        available.add("Bring the sexy back");
        available.add("Show them how to duggy");
        available.add("Let the good times roll");

        ArrayList<String> active = new ArrayList<String>();
        // TODO: replace with real data from sql db
        active.add("Play that funky music");
        active.add("Stay alive");
        active.add("Get down tonight");

        ArrayList<String> completed = new ArrayList<String>();
        // TODO: replace with real data from sql db
        completed.add("Don't stop believin'");
        completed.add("Get some satisfaction");
        completed.add("Come up with a theme for these fake quest names");


        String questString = "\nAVAILABLE:\n";
        int num = 0;
        for (int i = 0; i < available.size(); i++) {
            num++;
            questString += "\t" + num + ". " + available.get(i) + "\n";
        }
        questString += "\nACTIVE:\n";
        for (int i = 0; i < active.size(); i++) {
            num++;
            questString += "\t" + num + ". " + active.get(i) + "\n";
        }
        questString += "\nCOMPLETED:\n";
        for (int i = 0; i < completed.size(); i++) {
            num++;
            questString += "\t" + num + ". " + completed.get(i) + "\n";
        }
        questString += "-------------------------------------------------------";

        System.out.println(questString);
        System.out.print("Please enter the number of the desired quest here: ");
        int input = sc.nextInt();

        while (input < 1 || input > available.size() + active.size() + completed.size()) {
            System.out.println("\nInvalid input!\n");
            System.out.print("Please enter the number of the desired quest here: ");
            input = sc.nextInt();
        }

        if (input > available.size() + active.size()) {
            return completed.get(input-1 - (available.size() + active.size()));
        }
        else if (input > available.size()) {
            return active.get(input-1 - available.size());
        }
        return available.get(input-1);
    }

    private void cancelQuest (String questName) {

        // TODO:
        // 1. Check if the quest is in the current party's active quests.
        // 2a. If not, print an appropriate error message.
        // 2b. If so, remove it from the party's quests.
        // 3. print an appropriate error message if something bad happens.

        System.out.println(questName + " has been canceled!");
    }

    private void completeQuest (String questName) {

        // TODO:
        // 1. Check if the quest is in the current party's active quests.
        // 2a. If not, print an appropriate error message.
        // 2b. If so, mark it complete.
        // 3. print an appropriate error message if something bad happens.

        System.out.println(questName + " has been completed!");
    }

    private void activateQuest (String questName) {

        // TODO:
        // 1. Check if the quest is not in the current party's quests.
        // 2a. If not, print an appropriate error message.
        // 2b. If so, mark it active.
        // 3. print an appropriate error message if something bad happens.

        System.out.println(questName + " has been activated!");
    }

    private void printQuest (String questName) {
        String description;
        int experience;
        String rewardItemName;
        String rewardItemDesc; // reward Item Description

        // TODO: Get real data from the database yo

        description = "Go do things a stuff";
        experience = 5; // idk
        rewardItemName = "Pain Maker";
        rewardItemDesc = "It makes pain happen.";

        // format description
        if (description.length() > 35) {
            String[] tokens = description.split(" ");
            description = "";
            int curLineLen = 0;
            for (String tok : tokens) {
                if (curLineLen == 0) {
                    description += "\n\t";
                }
                description += tok;
                curLineLen += tok.length();
                if (curLineLen > 40) {
                    curLineLen = 0;
                }
            }
        }

        // format rewardItemDesc
        if (description.length() > 20) {
            String[] tokens = description.split(" ");
            description = "";
            int curLineLen = 0;
            for (String tok : tokens) {
                if (curLineLen == 0) {
                    description += "\n\t";
                }
                description += tok;
                curLineLen += tok.length();
                if (curLineLen > 40) {
                    curLineLen = 0;
                }
            }
        }

        String pString =
            "\n-------------------------------------------------------\n" + // 50 chars
            questName + "\n" +
            "-------------------------------------------------------\n" + // 50 chars
            "Description: " + description + "\n" +
            "Experience:  " + experience + "\n" +
            "-----------------------------------\n" +
            "Reward Item: \n" +
            "Name:" + rewardItemName + "\n" +
            "Description: " + rewardItemDesc + "\n" +
            "-------------------------------------------------------\n";

        System.out.println(pString);
    }

    private void printOptions () {
        String optionsString =
            "Available options:\n" +
            "\t1: Print quest\n" +
            "\t2: Accept quest\n" +
            "\t3: Complete quest\n" +
            "\t4: Cancel quest\n" +
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
        String questName;
        do {

            printOptions();
            try {
                input = sc.nextInt();
                questName = printQuests();
                switch (input) {
                    case 1:
                        printQuest(questName);
                        break;
                    case 2:
                        activateQuest(questName);
                        break;
                    case 3:
                        completeQuest(questName);
                        break;
                    case 4:
                        cancelQuest(questName);
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
