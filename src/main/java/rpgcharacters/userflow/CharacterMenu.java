package rpgcharacters.userflow;

import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.InputMismatchException;

public class CharacterMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    private String username;

    /**
    * Constructor Method
    */
    public CharacterMenu(Scanner sc, String username, Connection conn) {
        this.sc = sc;
        this.username = username;
        this.conn = conn;
    }

    private void printMenuTitle() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println("Character Menu");
        System.out.println("-------------------------------------------------------");
    }

    private String printChars() {
        ArrayList<String> characters = new ArrayList<String>();

        try {
            String query = "SELECT name FROM character "
                        + " WHERE user_username='" + username.replaceAll("'", "''") + "';";
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);

            results.beforeFirst();
            while (results.next()) {
                characters.add(results.getString("name"));
            }

            if (characters.size() == 0) {
                System.out.println("\n You do not have any characters!");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        String charsString = "Your characters:\n";

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

    private void deleteCharacter(String charName) {
        try {
            String query = "DELETE FROM character "
                         + "WHERE user_username = '" + username.replaceAll("'", "''") + "' "
                         + "AND name = '" + charName.replaceAll("'", "''") + "';";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
            System.out.println(charName + " has been deleted!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeFromParty(String charName) {
        try {
            String query = "SELECT * FROM character "
                         + "WHERE user_username = '" + username.replaceAll("'", "''") + "' "
                         + "AND name = '" + charName.replaceAll("'", "''") + "' "
                         + "AND party_id IS NOT NULL;";
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);

            if (!results.last()) {
                System.out.println(charName + " is not in a party!");
            } else {
                String updateQuery = "UPDATE character "
                                   + "SET party_id = NULL "
                                   + "WHERE user_username = '" + username.replaceAll("'", "''") + "' "
                                   + "AND name = '" + charName.replaceAll("'", "''") + "';";
                Statement updateStmt = conn.createStatement();
                updateStmt.executeUpdate(updateQuery);
                System.out.println(charName + " has been successfully removed from the party!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void printCharacter(Connection conn, String charName, String username) {
        try {
            String query = "SELECT * FROM character AS c "
                         + "LEFT OUTER JOIN race as r on c.race_name = r.name "
                         + "LEFT OUTER JOIN archetype as a on c.archetype_name = a.name "
                         + "LEFT OUTER JOIN party as p on c.party_id = p.id "
                         + "WHERE c.user_username = '" + username.replaceAll("'", "''") + "' "
                         + "AND c.name = '" + charName.replaceAll("'", "''") + "';";
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);
            results.first();

            String story = results.getString("story");
            String race = results.getString("race.name");
            String arch = results.getString("archetype.name");
            String party = results.getString("party.name");
            int power = results.getInt("power") +
                        results.getInt("race.power_mod") +
                        results.getInt("archetype.power_mod");
            int proficiency = results.getInt("proficiency") +
                              results.getInt("race.proficiency_mod") +
                              results.getInt("archetype.proficiency_mod");
            int personality = results.getInt("personality") +
                              results.getInt("race.personality_mod") +
                              results.getInt("archetype.personality_mod");
            int perception = results.getInt("perception") +
                             results.getInt("race.perception_mod") +
                             results.getInt("archetype.perception_mod");
            int experience = results.getInt("experience");

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
                (party != null? "Party:       " + party + "\n" : "") +
                "Power:       " + power + "\n" +
                "Proficiency: " + proficiency + "\n" +
                "Personality: " + personality + "\n" +
                "Perception:  " + perception + "\n" +
                "Experience:  " + experience + "\n" +
                "-------------------------------------------------------\n";

            System.out.println(pString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void printOptions() {
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
    public void enter() {
        printMenuTitle();
        int input = 0;
        int exit = 5;
        String character;
        do {

            printOptions();
            try {
                input = sc.nextInt();

                switch (input) {
                    case 1:
                        Menu createCharacterMenu = new CreateCharacterMenu(sc, username, conn);
                        createCharacterMenu.enter();
                        break;
                    case 2:
                        character = printChars();
                        if (character == null) break;
                        printCharacter(conn, character, username);
                        break;
                    case 3:
                        character = printChars();
                        if (character == null) break;
                        deleteCharacter(character);
                        break;
                    case 4:
                        character = printChars();
                        if (character == null) break;
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
                System.out.println("\nInvalid input...\n");
                continue;
            }

        } while (input != exit);
    }

}
