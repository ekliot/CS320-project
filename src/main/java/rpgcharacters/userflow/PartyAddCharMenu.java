package rpgcharacters.userflow;

import rpgcharacters.UI;

import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;

public class PartyAddCharMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    private String username;
    private String partyName;

    /**
     * Constructor Method
     * @param  Scanner sc    scanner inherited from the parent menu.
     */
    public PartyAddCharMenu(Scanner sc,String username, String partyName, Connection conn) {
        this.sc = sc;
        this.username = username;
        this.partyName = partyName;
        this.conn = conn;
    }

    private String[] selectCharacter() {
        ArrayList<String> charNames = new ArrayList<String>();
        ArrayList<String> userNames = new ArrayList<String>();
        ArrayList<String> options = new ArrayList<String>();
        String optionFormat = "%s (Player: %s)";

        try {
            String query = "SELECT * FROM character "
                         + "WHERE party_id IS NULL "
                         + "AND user_username != '" + username.replaceAll( "'", "''" ) + "';";

            // these options to createStatement let us use beforeFirst() after using last()
            Statement stmt = conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY );
            ResultSet results = stmt.executeQuery( query );

            if ( !results.last() ) {
                // UI.printOutput( "There are no characters in the party!" );
                return null;
            }

            results.beforeFirst();

            while ( results.next() ) {
                charNames.add( results.getString( "name" ) );
                userNames.add( results.getString( "user_username" ) );
                options.add( String.format( optionFormat,
                             results.getString( "name" ),
                             results.getString( "user_username" ) ) );
            }
        } catch (SQLException e) {
            // e.printStackTrace();
            UI.printOutput( "There was an error querying characters" );
        }

        UI.printOptions( options, "Characters:" );
        UI.printDiv2();

        int input = UI.promptInt( sc, "Enter number of character to add: ",
                                  1, charNames.size() );

        return new String[]{
            charNames.get(input-1),
            userNames.get(input-1)
        };
    }

    private boolean addChararacter(String charName, String charUsername) {
        try {
            String query = "UPDATE character "
                         + "SET party_id=(SELECT id FROM party "
                         + "WHERE name = '" + this.partyName.replaceAll("'", "''") + "' "
                         + "AND gm_username = '" + this.username.replaceAll("'", "''") + "') "
                         + "WHERE user_username = '" + charUsername.replaceAll("'", "''") + "' "
                         + "AND name = '" + charName.replaceAll("'", "''") + "';";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);

            UI.printOutput( charName + " has been added to " + this.partyName );
            return true;
        } catch (SQLException e) {
            // e.printStackTrace();
            UI.printOutput( "There was an error adding the character to a party" );
            return false;
        }
    }

    /**
    * Defines the loop for this menu
    */
    public void enter() {
        UI.printMenuTitle( "Add character to " + this.partyName );
        UI.printDiv2();

        boolean success = false;
        int wrongCount = 0;

        do {

            String[] character = selectCharacter();

            if (character == null) {
                UI.printOutput( "There are no characters that can be added to this party!" );
                success = true;
            } else {
                success = addChararacter(character[0], character[1]);
            }

            wrongCount++;

        } while (!success && wrongCount <= 3);

        if (!success) {
            UI.printOutput( "Too many attempts... Returning...\n" );
        }
    }

}
