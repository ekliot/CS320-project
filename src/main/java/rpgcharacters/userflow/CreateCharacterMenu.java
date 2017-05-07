package rpgcharacters.userflow;

import java.sql.*;
import java.util.Scanner;

public class CreateCharacterMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    private String username;

    private int curMaxAdd;

    /**
     * Constructor Method
     * @param  Scanner sc    scanner inherited from the parent menu.
     */
    public CreateCharacterMenu(Scanner sc, String username, Connection conn) {
        this.sc = sc;
        this.username = username;
        this.conn = conn;
    }

    private void printMenuTitle() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println("Create Character");
        System.out.println("-------------------------------------------------------");
    }

    private boolean saveToDB(String name, String story, String race, String archetype, int[] stats) {
        try {
            String query = "INSERT INTO character VALUES ("
                         + "'" + username.replaceAll("'", "''") + "',"
                         + "'" + name.replaceAll("'", "''") + "',"
                         + "NULL, " // party_id = NULL
                         + "'" + race.replaceAll("'", "''") + "',"
                         + "'" + archetype.replaceAll("'", "''") + "',"
                         + "'" + story.replaceAll("'", "''") + "',"
                         + stats[0] + ","
                         + stats[1] + ","
                         + stats[2] + ","
                         + stats[3] + ","
                         + "0);"; // characters have 0 xp on creation
            Statement stmt = conn.createStatement();
            stmt.execute(query);
            return true;
        } catch (SQLException e) {
            if (e.getMessage().startsWith("Unique index or primary key violation")) {
                System.out.println("\nCharacter already exists!\n");
            } else {
                System.out.println("\nAn error has occured while saving to the database.");
                e.printStackTrace();
            }
            return false;
        }
    }

    private int[] getCurStats(ResultSet raceResults, ResultSet archResults) throws SQLException {
        int[] raceMods = {
            raceResults.getInt("power_mod"),
            raceResults.getInt("proficiency_mod"),
            raceResults.getInt("personality_mod"),
            raceResults.getInt("perception_mod")
        };
        int[] archMods = {
            archResults.getInt("power_mod"),
            archResults.getInt("proficiency_mod"),
            archResults.getInt("personality_mod"),
            archResults.getInt("perception_mod")
        };

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
        return statMod;
    }

    private ResultSet getStatRow(String table, String name) throws SQLException {
        String query = "SELECT * FROM " + table
                     + " WHERE name='" + name.replaceAll("'", "''") + "';";
        Statement stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery(query);
        if (!results.last()) {
            System.out.println("\n" + name + " does not exist.");
            return null;
        }
        return results;
    }

    private boolean createCharacter() {

        System.out.print("Character Name: ");
        String charName = sc.nextLine();

        System.out.print("Character Backstory: ");
        String charStory = sc.nextLine();

        try {
            System.out.print("Character Race: ");
            String charRace = sc.nextLine();
            ResultSet raceResults = getStatRow("race", charRace);
            if (raceResults == null) return false;

            System.out.print("Character Archetype: ");
            String charArch = sc.nextLine();
            ResultSet archResults = getStatRow("archetype", charArch);
            if (archResults == null) return false;

            int[] curStats = getCurStats(raceResults,archResults);

            curMaxAdd = 20;
            curStats[0] = getStat("power",curStats[0]);
            curStats[1] = getStat("proficiency",curStats[1]);
            curStats[2] = getStat("personality",curStats[2]);
            curStats[3] = getStat("perception",curStats[3]);

            return saveToDB(charName,charStory,charRace,charArch,curStats);
        } catch (SQLException e) {
            System.out.println("\nAn error has occured while saving to the database.");
            e.printStackTrace();
            return false;
        }
    }

    /**
    * Defines the loop for this menu
    */
    public void enter() {
        printMenuTitle();
        sc.nextLine();
        boolean validCharInfo = false;
        int wrongCount = 0;

        do {

            validCharInfo = createCharacter();

            if (!validCharInfo) {
                System.out.println("Please try again...\n");
            }

            wrongCount++;

        } while (!validCharInfo && wrongCount <= 3);

        if (validCharInfo) {
            System.out.println("\nCharacter has been created!\n");
        }
        else {
            System.out.println("\nToo many attempts... Returning...\n");
        }
    }

}
