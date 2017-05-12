package rpgcharacters.userflow;

import rpgcharacters.UI;

import java.sql.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class CharacterMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    private String username;

    private List<String> options;

    private final String CHAR_CREATE = "Create a new character";
    private final String CHAR_PRINT  = "Print character";
    private final String CHAR_DELETE = "Delete character";
    private final String CHAR_REMOVE = "Remove a character from a party";
    private final String EXIT        = "Go back";

    /**
    * Constructor Method
    */
    public CharacterMenu(Scanner sc, String username, Connection conn) {
        this.sc = sc;
        this.username = username;
        this.conn = conn;
        this.options = Arrays.asList( CHAR_CREATE, CHAR_PRINT, CHAR_DELETE, CHAR_REMOVE, EXIT );
    }

    private String printChars() {
        ArrayList<String> characters = new ArrayList<String>();

        try {
            String query = "SELECT name FROM character "
                        + " WHERE user_username='" + this.username.replaceAll("'", "''") + "';";
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);

            results.beforeFirst();
            while (results.next()) {
                characters.add(results.getString("name"));
            }

            if (characters.size() == 0) {
                UI.printOutput("You do not have any characters!");
                return null;
            }
        } catch (SQLException e) {
            UI.printOutput( "There was an error querying characters" );
            // e.printStackTrace();
            return null;
        }

        UI.printOptions( characters, "Your characters:" );

        int input = UI.promptInt( sc, "Select a character: ",
                                  1, characters.size() );

        return characters.get(input-1);
    }

    private void deleteCharacter(String charName) {
        try {
            String query = "DELETE FROM character "
                         + "WHERE user_username = '" + this.username.replaceAll("'", "''") + "' "
                         + "AND name = '" + charName.replaceAll("'", "''") + "';";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
            UI.printOutput(charName + " has been deleted!");
        } catch (SQLException e) {
            // e.printStackTrace();
            UI.printOutput( "There was an error deleting the character" );
        }
    }

    private void removeFromParty(String charName) {
        try {
            String query = "SELECT * FROM character "
                         + "WHERE user_username = '" + this.username.replaceAll("'", "''") + "' "
                         + "AND name = '" + charName.replaceAll("'", "''") + "' "
                         + "AND party_id IS NOT NULL;";
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);

            if (!results.last()) {
                UI.printOutput(charName + " is not in a party!");
            } else {
                String updateQuery = "UPDATE character "
                                   + "SET party_id = NULL "
                                   + "WHERE user_username = '" + this.username.replaceAll("'", "''") + "' "
                                   + "AND name = '" + charName.replaceAll("'", "''") + "';";
                Statement updateStmt = conn.createStatement();
                updateStmt.executeUpdate(updateQuery);
                UI.printOutput(charName + " has been successfully removed from the party!");
            }
        } catch (SQLException e) {
            // e.printStackTrace();
            UI.printOutput( "There was an error removing the character from the party" );
        }
    }

    public void printCharacter(String charName) {
        try {
            String query = "SELECT * FROM character AS c "
                         + "LEFT OUTER JOIN race as r on c.race_name = r.name "
                         + "LEFT OUTER JOIN archetype as a on c.archetype_name = a.name "
                         + "LEFT OUTER JOIN party as p on c.party_id = p.id "
                         + "WHERE c.user_username = '" + this.username.replaceAll("'", "''") + "' "
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
            story = ( story.length() > 46 ? "\n" + UI.formatParagraph( story, 46, 4 ) : story );

            String pString =
                "\n-------------------------------------------------------\n" + // 50 chars
                charName + "\n" +
                "  User:        " + this.username + "\n" +
                "  Story:       " + story + "\n" +
                "  Race:        " + race + "\n" +
                "  Archetype:   " + arch + "\n" +
                (party != null? "  Party:       " + party + "\n" : "") +
                "  Power:       " + power + "\n" +
                "  Proficiency: " + proficiency + "\n" +
                "  Personality: " + personality + "\n" +
                "  Perception:  " + perception + "\n" +
                "  Experience:  " + experience + "\n";

            ArrayList<String> itemNames = new ArrayList<String>();
            String itemQuery = "SELECT * FROM character_item "
                             + "WHERE user_username = '" + this.username.replaceAll("'", "''") + "' "
                             + "AND character_name = '" + charName.replaceAll("'", "''") + "';";

            // these options to createStatement let us use beforeFirst() after using last()
            Statement itemStmt = conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY );
            ResultSet items = itemStmt.executeQuery( itemQuery );

            if ( items.last() ) {
                items.beforeFirst();

                while (items.next()) {
                    itemNames.add(items.getString("item_name"));
                }

                if (itemNames.size() > 0) pString += "  Items:\n";
                for (String name : itemNames) {
                    pString += "    " + name + "\n";
                }
            }

            System.out.println( pString );

            UI.printDiv2();
        } catch (SQLException e) {
            // e.printStackTrace();s
            UI.printOutput( "There was an error querying the character" );
        }
    }

    /**
    * Defines the loop for this menu
    */
    public void enter() {
        UI.printMenuTitle( "Character Menu" );

        int input = -1;
        String option = "";

        String character;

        do {

            UI.printOptions( options );
            input = UI.promptInt( sc, "Select an option: ",
                                  1, options.size() );
            option = options.get( input - 1 );

            switch ( option ) {
                case CHAR_CREATE:
                    Menu createCharacterMenu = new CreateCharacterMenu(sc, this.username, conn);
                    createCharacterMenu.enter();
                    UI.printMenuTitle( "Character Menu" );
                    break;
                case CHAR_PRINT:
                    character = printChars();
                    if (character == null) break;
                    printCharacter(character);
                    UI.printMenuTitle( "Character Menu" );
                    break;
                case CHAR_DELETE:
                    character = printChars();
                    if (character == null) break;
                    deleteCharacter(character);
                    UI.printMenuTitle( "Character Menu" );
                    break;
                case CHAR_REMOVE:
                    character = printChars();
                    if (character == null) break;
                    removeFromParty(character);
                    UI.printMenuTitle( "Character Menu" );
                    break;
                case EXIT:
                    UI.printOutput("Going back...");
                    break;
                default:
                    UI.printOutput("Invalid input...");
            }

        } while ( !option.equals( EXIT ) );
    }

}
