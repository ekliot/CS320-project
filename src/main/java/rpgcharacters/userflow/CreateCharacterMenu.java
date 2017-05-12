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
                UI.printOutput("Character already exists!");
            } else {
                UI.printOutput("There was an error saving the character");
                // e.printStackTrace();
            }
            return false;
        }
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
            // e.printStackTrace();
            UI.printOutput( "There was an error querying " + table + "s" );
        }

        System.out.println("\n" + table.substring(0, 1).toUpperCase() + table.substring(1) + "s:");
        UI.printDiv2();
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

        UI.printDiv2();

        int input = UI.promptInt( sc, "Enter corresponding number of the " + table + " to add: ",
                                  1, rows.size() );

        String selectedName = rows.keySet().toArray()[input-1].toString();

        return new Tuple<String, int[]>(selectedName, rows.get(selectedName));
    }

    private boolean createCharacter() {

        UI.printOutput("Character Name: ", false);
        String charName = sc.nextLine();

        UI.printOutput("Character Backstory: ", false);
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

        // select character stats

        boolean stats_valid = false;
        String stat_input;
        String stat_input_regex = "-?\\d+ -?\\d+ -?\\d+ -?\\d+";
        String[] stats;

        int stat_sum        = 0;
        int stat_balance    = 20;

        int power_mod       = 0;
        int proficiency_mod = 0;
        int personality_mod = 0;
        int perception_mod  = 0;

        UI.printOutput( "Enter character's stat modifiers" );
        UI.printOutput( "Must format as four integers with a sum of 20 (e.g. `10 0 15 -5`)" );
        UI.printOutput( "Order:\n\tPower | Proficiency | Personality | Perception" );

        do {
            stat_input = sc.nextLine();

            if ( java.util.regex.Pattern.matches( stat_input_regex, stat_input ) ) {

                stats = stat_input.split( " " );

                power_mod       = Integer.parseInt( stats[0] );
                proficiency_mod = Integer.parseInt( stats[1] );
                personality_mod = Integer.parseInt( stats[2] );
                perception_mod  = Integer.parseInt( stats[3] );

                stat_sum = power_mod + proficiency_mod + personality_mod + perception_mod;

                if ( stat_sum == stat_balance ) {
                    stats_valid = true;
                } else {
                    UI.printOutput( "Stats add up to invalid sum (got " + stat_sum + ", need " + stat_balance + "), try again" );
                }
            } else {
                UI.printOutput( "Input in invalid format, try again" );
            }
        } while ( !stats_valid );

        curStats[0] += power_mod;
        curStats[1] += proficiency_mod;
        curStats[2] += personality_mod;
        curStats[3] += perception_mod;

        return saveToDB(charName, charStory, raceName, archName, curStats);
    }

    /**
    * Defines the loop for this menu
    */
    public void enter() {
        UI.printMenuTitle( "Create Character" );
        UI.printDiv2();

        boolean validCharInfo = false;
        int wrongCount = 0;

        do {

            validCharInfo = createCharacter();

            if (!validCharInfo) {
                UI.printOutput("Please try again...");
            }

            wrongCount++;

        } while (!validCharInfo && wrongCount <= 3);

        if (validCharInfo) {
            UI.printOutput("Character has been created!");
        }
        else {
            UI.printOutput("Too many attempts... Returning...");
        }
    }

}
