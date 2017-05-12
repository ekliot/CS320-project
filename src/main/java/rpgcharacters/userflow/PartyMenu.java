package rpgcharacters.userflow;

import rpgcharacters.UI;

import java.sql.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class PartyMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    private String username;

    private List<String> options;

    private final String PARTY_CREATE = "Create a new party";
    private final String PARTY_PRINT  = "Print party";
    private final String PARTY_DELETE = "Delete party";
    private final String PARTY_REMOVE = "Remove a character from a party";
    private final String PARTY_ADD    = "Add a character to a party";
    private final String PARTY_QUESTS = "Edit a party's quests";
    private final String EXIT         = "Go back";

    /**
     * Constructor Method
     */
    public PartyMenu(Scanner sc, String username, Connection conn) {
        this.sc = sc;
        this.username = username;
        this.conn = conn;
        this.options = Arrays.asList( PARTY_CREATE, PARTY_PRINT, PARTY_DELETE, PARTY_REMOVE, PARTY_ADD, PARTY_QUESTS, EXIT );
    }

    private String printParties() {
        ArrayList<String> parties = new ArrayList<String>();

        try {
            String query = "SELECT name FROM party "
                         + "WHERE gm_username='" + this.username.replaceAll("'", "''") + "';";

            // these options to createStatement let us use beforeFirst() after using last()
            Statement stmt = conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY );
            ResultSet results = stmt.executeQuery( query );

            if ( !results.last() ) {
                UI.printOutput( "You do not have any parties!" );
                return null;
            }

            results.beforeFirst();

            while ( results.next() ) {
                parties.add( results.getString( "name" ) );
            }
        } catch ( SQLException e ) {
            // e.printStackTrace();
            UI.printOutput( "There was an error querying parties" );
            return null;
        }

        UI.printOptions( parties, "Your parties:" );

        int input = UI.promptInt( sc, "Enter the number of the party: ",
                                  1, parties.size());

        return parties.get( input - 1 );
    }

    private void deleteParty( String partyName ) {
        try {
            String query = "DELETE FROM party "
                         + "WHERE gm_username = '" + this.username.replaceAll("'", "''") + "' "
                         + "AND name = '" + partyName.replaceAll("'", "''") + "';";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate( query );
            UI.printOutput( partyName + " has been deleted!" );
        } catch (SQLException e) {
            // e.printStackTrace();
            UI.printOutput( "There was an error deleting the party, " + partyName );
        }
    }

    private void printParty( String partyName ) {

        UI.printDiv2();

        String partyFormat = "%s\n  Game Master: %s\n  Characters:";
        String charFormat = "    %s (Player: %s)";

        System.out.println( String.format( partyFormat, partyName, username ) );

        try {
            String query = "SELECT * FROM character as c "
                         + "LEFT OUTER JOIN party as p on c.party_id = p.id "
                         + "WHERE p.name = '" + partyName.replaceAll("'", "''") + "'";
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery( query );

            results.beforeFirst();

            while ( results.next() ) {
                System.out.println(
                    String.format( charFormat,
                        results.getString( "name" ),
                        results.getString( "user_username" )
                    )
                );
            }
        } catch (SQLException e) {
            // e.printStackTrace();
            UI.printOutput( "There was an error querying the party's characters" );
        }

    }

    /**
    * Defines the loop for this menu
    */
    public void enter() {
        UI.printMenuTitle( "Party Menu" );

        String party;
        int input = -1;
        String option = "";

        do {

            UI.printOptions( options );
            input = UI.promptInt( sc, "Select an option: ",
                                  1, options.size() );
            option = options.get( input - 1 );

            switch ( option ) {
                case PARTY_CREATE:
                    Menu createPartyMenu = new CreatePartyMenu(sc, this.username, conn);
                    createPartyMenu.enter();

                    UI.printMenuTitle( "PartyMenu" );
                    break;
                case PARTY_PRINT:
                    party = printParties();
                    if (party == null) break;
                    printParty(party);

                    UI.printMenuTitle( "PartyMenu" );
                    break;
                case PARTY_DELETE:
                    party = printParties();
                    if (party == null) break;
                    deleteParty(party);

                    UI.printMenuTitle( "PartyMenu" );
                    break;
                case PARTY_REMOVE:
                    party = printParties();
                    if (party == null) break;
                    Menu partyRemoveCharMenu = new PartyRemoveCharMenu(sc, party, conn);
                    partyRemoveCharMenu.enter();

                    UI.printMenuTitle( "PartyMenu" );
                    break;
                case PARTY_ADD:
                    party = printParties();
                    if (party == null) break;
                    Menu partyAddCharMenu = new PartyAddCharMenu(sc, this.username, party, conn);
                    partyAddCharMenu.enter();

                    UI.printMenuTitle( "PartyMenu" );
                    break;
                case PARTY_QUESTS:
                    party = printParties();
                    if (party == null) break;
                    Menu editPartyQuestsMenu = new EditPartyQuestsMenu(sc, this.username, party, conn);
                    editPartyQuestsMenu.enter();

                    UI.printMenuTitle( "PartyMenu" );
                    break;
                case EXIT:
                    UI.printOutput( "Going back..." );
                    break;
                default:
                    UI.printOutput( "Invalid input..." );
            }

        } while ( !option.equals( EXIT ) );
    }

}
