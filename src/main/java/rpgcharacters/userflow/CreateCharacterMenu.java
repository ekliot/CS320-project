package rpgcharacters.userflow;

import rpgcharacters.UI;

import java.sql.*;
import java.util.Scanner;
import java.util.LinkedHashMap;

public class CreateCharacterMenu implements Menu {

    private class Tuple<X, Y> {
        public final X x;
        public final Y y;
        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }

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
                         + "'" + this.username.replaceAll("'", "''") + "',"
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

    private Tuple<String, int[]> selectStat(String table) {
        LinkedHashMap<String, int[]> rows = new LinkedHashMap<String, int[]>();

        try {
            String query = "SELECT * FROM " + table + ";";
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);

            results.beforeFirst();
            while (results.next()) {
                rows.put(results.getString("name"), new int[]{
                    results.getInt("power_mod"),
                    results.getInt("proficiency_mod"),
                    results.getInt("personality_mod"),
                    results.getInt("perception_mod")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(table.substring(0, 1).toUpperCase() + table.substring(1) + "s:");
        String pString = "";
        int indLen = (rows.size() + "").length() + 2;
        for (int i = 0; i < rows.size(); i++) {
            String lineString1 = "";
            lineString1 += (i+1) + ".";
            while (lineString1.length() < indLen) {
                lineString1 += " ";
            }
            lineString1 += rows.keySet().toArray()[i].toString() + "\n";
            pString += lineString1;
        }
        System.out.print(pString);

        System.out.println("-------------------------------------------------------");
        System.out.print("Enter corresponding number of the " + table + " to add: ");
        int input = sc.nextInt();
        while (input < 1 || input > rows.size()) {
            System.out.println("\nInvalid input...\n");
            System.out.print("Enter corresponding number of the " + table + " to add: ");
            input = sc.nextInt();
        }
        String selectedName = rows.keySet().toArray()[input-1].toString();
        return new Tuple<String, int[]>(selectedName, rows.get(selectedName));
    }

    private boolean createCharacter() {

        System.out.print("Character Name: ");
        String charName = sc.nextLine();

        System.out.print("Character Backstory: ");
        String charStory = sc.nextLine();

        Tuple<String, int[]> race = selectStat("race");
        String raceName = race.x;
        int[] raceMods = race.y;

        Tuple<String, int[]> archetype = selectStat("archetype");
        String archName = archetype.x;
        int[] archMods = archetype.y;

        int[] curStats = new int[4];
        for (int i = 0; i < 4; i++) {
            curStats[i] = raceMods[i] + archMods[i];
        }

        curMaxAdd = 20;
        curStats[0] = getStat("power", curStats[0]);
        curStats[1] = getStat("proficiency", curStats[1]);
        curStats[2] = getStat("personality", curStats[2]);
        curStats[3] = getStat("perception", curStats[3]);

        return saveToDB(charName, charStory, raceName, archName, curStats);
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
