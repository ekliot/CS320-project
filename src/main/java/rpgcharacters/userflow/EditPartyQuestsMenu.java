package rpgcharacters.userflow;

import rpgcharacters.UI;

import java.sql.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class EditPartyQuestsMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    private String username;
    private String partyName;

    private List<String> options;

    private final String QUEST_PRINT    = "Print quest";
    private final String QUEST_ACCEPT   = "Accept quest";
    private final String QUEST_COMPLETE = "Complete quest";
    private final String QUEST_CANCEL   = "Cancel quest";
    private final String EXIT           = "Go back";

    /**
     * Constructor Method
     */
    public EditPartyQuestsMenu(Scanner sc, String username, String partyName, Connection conn) {
        this.sc = sc;
        this.username = username;
        this.partyName = partyName;
        this.conn = conn;
        this.options = Arrays.asList( QUEST_PRINT, QUEST_ACCEPT, QUEST_COMPLETE, QUEST_CANCEL, EXIT );
    }

    private String printQuests( boolean ava, boolean act, boolean com ) {

        String questString = "";
        int num = 0;

        ArrayList<String> available = new ArrayList<String>();
        if ( ava ) {
            try {
                String query = "SELECT name FROM quest "
                             + "WHERE name NOT IN ( "
                             + "SELECT quest.name FROM party_quest "
                             + "LEFT OUTER JOIN quest on party_quest.quest_name = quest.name "
                             + "LEFT OUTER JOIN party on party_quest.party_id = party.id "
                             + "WHERE party.name = '" + this.partyName.replaceAll("'", "''") + "');";
                Statement stmt = conn.createStatement();
                ResultSet results = stmt.executeQuery(query);

                results.beforeFirst();
                while (results.next()) {
                    available.add(results.getString("name"));
                }

                questString += "\nAVAILABLE:\n";
                for (int i = 0; i < available.size(); i++) {
                    num++;
                    questString += "\t" + num + ": " + available.get(i) + "\n";
                }
                if (available.size() == 0) questString += "\tThere are no available quests for this party\n";
            } catch (SQLException e) {
                // e.printStackTrace();
                UI.printOutput( "There was an error querying the party's available quests" );
            }
        }

        ArrayList<String> active = new ArrayList<String>();
        if ( act ) {
            try {
                String query = "SELECT quest.name FROM quest "
                             + "LEFT OUTER JOIN party_quest ON quest.name = party_quest.quest_name "
                             + "LEFT OUTER JOIN party on party_quest.party_id = party.id "
                             + "WHERE party.name = '" + this.partyName.replaceAll("'", "''") + "' "
                             + "AND party_quest.status = 'Active';";
                Statement stmt = conn.createStatement();
                ResultSet results = stmt.executeQuery(query);

                results.beforeFirst();
                while (results.next()) {
                    active.add(results.getString("name"));
                }

                questString += "\nACTIVE:\n";
                for (int i = 0; i < active.size(); i++) {
                    num++;
                    questString += "\t" + num + ": " + active.get(i) + "\n";
                }
                if (active.size() == 0) questString += "\tThere are no active quests for this party\n";
            } catch (SQLException e) {
                // e.printStackTrace();
                UI.printOutput( "There was an error querying the party's active quests" );
            }
        }

        ArrayList<String> completed = new ArrayList<String>();
        if ( com ) {
            try {
                String query = "SELECT quest.name FROM quest "
                             + "LEFT OUTER JOIN party_quest ON quest.name = party_quest.quest_name "
                             + "LEFT OUTER JOIN party on party_quest.party_id = party.id "
                             + "WHERE party.name = '" + this.partyName.replaceAll("'", "''") + "' "
                             + "AND party_quest.status = 'Complete';";
                Statement stmt = conn.createStatement();
                ResultSet results = stmt.executeQuery(query);

                results.beforeFirst();
                while (results.next()) {
                    completed.add(results.getString("name"));
                    num++;
                }

                questString += "\nCOMPLETED:\n";
                for (int i = 0; i < completed.size(); i++) {
                    num++;
                    questString += "\t" + num + ": " + completed.get(i) + "\n";
                }
                if (completed.size() == 0) questString += "\tThere are no completed quests for this party\n";
            } catch (SQLException e) {
                // e.printStackTrace();
                UI.printOutput( "There was an error querying the party's completed quests" );
            }
        }

        System.out.println(questString);

        UI.printDiv2();

        if ( num > 0 ) {
            int input = UI.promptInt( sc, "Select a quest: ",
            1, num );

            if (input > available.size() + active.size()) {
                return completed.get(input-1 - (available.size() + active.size()));
            }
            else if (input > available.size()) {
                return active.get(input-1 - available.size());
            }
            return available.get(input-1);
        } else {
            return "";
        }
    }

    private void cancelQuest(String questName) {
        try {
            String query = "SELECT * FROM party_quest "
                         + "LEFT OUTER JOIN party on party_quest.party_id = party.id "
                         + "WHERE party.name = '" + this.partyName.replaceAll("'", "''") + "' "
                         + "AND quest_name = '" + questName.replaceAll("'", "''") + "';";
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet results = stmt.executeQuery(query);

            if (!results.last() || results.getString("status").equals("Complete")) {
                UI.printOutput(questName + " can't be cancelled");
            } else {
                results.first();
                int partyID = results.getInt("party_id");
                String deleteQuery = "DELETE FROM party_quest "
                                   + "WHERE party_id = " + partyID
                                   + " AND quest_name = '" + questName.replaceAll("'", "''") + "';";
                Statement deleteStmt = conn.createStatement();
                deleteStmt.executeUpdate(deleteQuery);
                UI.printOutput(questName + " has been canceled!");
            }
        } catch (SQLException e) {
            UI.printOutput("There was an error cancelling the quest");
        }
    }

    private void completeQuest(String questName) {
        try {
            String query = "SELECT * FROM party_quest "
                         + "LEFT OUTER JOIN party on party_quest.party_id = party.id "
                         + "LEFT OUTER JOIN quest on party_quest.quest_name = quest.name "
                         + "WHERE party.name = '" + this.partyName.replaceAll("'", "''") + "' "
                         + "AND quest_name = '" + questName.replaceAll("'", "''") + "';";
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet results = stmt.executeQuery(query);

            if (!results.last() || results.getString("status").equals("Complete")) {
                UI.printOutput(questName + " can't be completed");
            } else {
                results.first();
                int partyID = results.getInt("party_id");
                int experience = results.getInt("quest.experience");
                String itemName = results.getString("item_name");

                String updateQuery = "UPDATE party_quest "
                                   + "SET status = 'Complete' "
                                   + "WHERE party_id = " + partyID
                                   + " AND quest_name = '" + questName.replaceAll("'", "''") + "';";
                Statement updateStmt = conn.createStatement();
                updateStmt.executeUpdate(updateQuery);

                String charQuery = "SELECT name, user_username FROM character "
                                 + "WHERE party_id = " + partyID + ";";
                Statement charStmt = conn.createStatement();
                ResultSet characters = charStmt.executeQuery(charQuery);

                results.beforeFirst();
                while (characters.next()) {
                    String updateCharQuery = "UPDATE character "
                                           + "SET experience = experience + " + experience
                                           + " WHERE user_username = '" + characters.getString("user_username").replaceAll("'", "''") + "' "
                                           + "AND name = '" + characters.getString("name").replaceAll("'", "''") + "';";
                    Statement updateCharStmt = conn.createStatement();
                    updateCharStmt.executeUpdate(updateCharQuery);

                    String charItemQuery = "INSERT INTO character_item VALUES ("
                                         + "'" + characters.getString("user_username").replaceAll("'", "''") + "', "
                                         + "'" + characters.getString("name").replaceAll("'", "''") + "', "
                                         + "'" + itemName.replaceAll("'", "''") + "');";
                    Statement charItemStmt = conn.createStatement();
                    try {
                        charItemStmt.executeUpdate(charItemQuery);
                    } catch (SQLException e) {
                        // Character already has this item. Ignore exception
                    }
                }
                UI.printOutput(questName + " has been completed!");
            }
        } catch (SQLException e) {
            UI.printOutput( "There was an error completing the quest" );
        }
    }

    private void activateQuest(String questName) {
        try {
            String query = "SELECT * FROM party_quest "
                         + "LEFT OUTER JOIN party on party_quest.party_id = party.id "
                         + "WHERE party.name = '" + this.partyName.replaceAll("'", "''") + "' "
                         + "AND quest_name = '" + questName.replaceAll("'", "''") + "';";
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);

            if (results.last()) {
                UI.printOutput(questName + " can't be activated");
            } else {
                String insertQuery = "INSERT INTO party_quest VALUES ("
                                   + "(SELECT id FROM party "
                                   + "WHERE name = '" + this.partyName.replaceAll("'", "''") + "' "
                                   + "AND gm_username = '" + this.username.replaceAll("'", "''") + "'), "
                                   + "'" + questName.replaceAll("'", "''") + "', "
                                   + "'Active');";
                Statement insertStmt = conn.createStatement();
                insertStmt.executeUpdate(insertQuery);
                UI.printOutput(questName + " has been activated!");
            }
        } catch (SQLException e) {
            UI.printOutput( "There was an error activating the quest" );
        }
    }

    private void printQuest(String questName) {
        try {
            String query = "SELECT * FROM quest "
                         + "LEFT OUTER JOIN item on quest.item_name = item.name "
                         + "WHERE quest.name = '" + questName.replaceAll("'", "''") + "';";
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);
            results.first();

            String description = results.getString("quest.description");
            int experience = results.getInt("experience");
            String item = results.getString("item.name");

            UI.printDiv2();

            System.out.println( questName );
            System.out.println( "  Experience: " + experience );
            System.out.println( "  Item reward: " + item );
            System.out.println( "  Description:" );

            if ( !description.isEmpty() ) {
                UI.printParagraph( description, 46, 4 );
            }

            UI.printDiv2();
        } catch (SQLException e) {
            // e.printStackTrace();
            UI.printOutput( "There was an error querying the quest" );
        }
    }

    /**
    * Defines the loop for this menu
    */
    public void enter() {
        UI.printMenuTitle( this.partyName + "'s quests Menu" );

        int input = -1;
        String option = "";

        String questName;

        do {

            UI.printOptions( options );
            input = UI.promptInt( sc, "Select an option: ",
                                  1, options.size() );
            option = options.get( input - 1 );

            switch ( option ) {
                case QUEST_PRINT:
                    questName = printQuests( true, true, true );
                    printQuest(questName);
                    break;
                case QUEST_ACCEPT:
                    questName = printQuests( true, false, false );
                    activateQuest(questName);
                    break;
                case QUEST_COMPLETE:
                    questName = printQuests( false, true, false );
                    completeQuest(questName);
                    break;
                case QUEST_CANCEL:
                    questName = printQuests( false, true, false );
                    cancelQuest(questName);
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
