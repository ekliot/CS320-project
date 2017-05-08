package rpgcharacters.userflow;

import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.InputMismatchException;

public class EditPartyQuestsMenu implements Menu {

    private Scanner sc;
    private Connection conn;

    private String username;
    private String partyName;

    /**
     * Constructor Method
     */
    public EditPartyQuestsMenu(Scanner sc, String username, String partyName, Connection conn) {
        this.sc = sc;
        this.username = username;
        this.partyName = partyName;
        this.conn = conn;
    }

    private void printMenuTitle() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println(this.partyName + "'s quests Menu");
        System.out.println("-------------------------------------------------------");
    }

    private String printQuests() {

        ArrayList<String> available = new ArrayList<String>();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<String> active = new ArrayList<String>();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<String> completed = new ArrayList<String>();
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String questString = "\nAVAILABLE:\n";
        int num = 0;
        for (int i = 0; i < available.size(); i++) {
            num++;
            questString += "\t" + num + ". " + available.get(i) + "\n";
        }
        if (available.size() == 0) questString += "\tThere are no available quests for this party\n";
        questString += "\nACTIVE:\n";
        for (int i = 0; i < active.size(); i++) {
            num++;
            questString += "\t" + num + ". " + active.get(i) + "\n";
        }
        if (active.size() == 0) questString += "\tThere are no active quests for this party\n";
        questString += "\nCOMPLETED:\n";
        for (int i = 0; i < completed.size(); i++) {
            num++;
            questString += "\t" + num + ". " + completed.get(i) + "\n";
        }
        if (completed.size() == 0) questString += "\tThere are no completed quests for this party\n";
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

    private void cancelQuest(String questName) {
        try {
            String query = "SELECT * FROM party_quest "
                         + "LEFT OUTER JOIN party on party_quest.party_id = party.id "
                         + "WHERE party.name = '" + this.partyName.replaceAll("'", "''") + "' "
                         + "AND quest_name = '" + questName.replaceAll("'", "''") + "';";
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet results = stmt.executeQuery(query);

            if (!results.last() || results.getString("status").equals("Complete")) {
                System.out.println(questName + " can't be cancelled");
            } else {
                results.first();
                int partyID = results.getInt("party_id");
                String deleteQuery = "DELETE FROM party_quest "
                                   + "WHERE party_id = " + partyID
                                   + " AND quest_name = '" + questName.replaceAll("'", "''") + "';";
                Statement deleteStmt = conn.createStatement();
                deleteStmt.executeUpdate(deleteQuery);
                System.out.println(questName + " has been canceled!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
                System.out.println(questName + " can't be completed");
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
                System.out.println(questName + " has been completed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
                System.out.println(questName + " can't be activated");
            } else {
                String insertQuery = "INSERT INTO party_quest VALUES ("
                                   + "(SELECT id FROM party "
                                   + "WHERE name = '" + this.partyName.replaceAll("'", "''") + "' "
                                   + "AND gm_username = '" + this.username.replaceAll("'", "''") + "'), "
                                   + "'" + questName.replaceAll("'", "''") + "', "
                                   + "'Active');";
                Statement insertStmt = conn.createStatement();
                insertStmt.executeUpdate(insertQuery);
                System.out.println(questName + " has been activated!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            String rewardItemName = results.getString("item.name");
            String rewardItemDesc = results.getString("item.description");

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
                "  Description: " + description + "\n" +
                "  Experience:  " + experience + "\n" +
                "  Reward Item\n" +
                "    Name: " + rewardItemName + "\n" +
                "    Description: " + rewardItemDesc + "\n" +
                "-------------------------------------------------------\n";

            System.out.println(pString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void printOptions() {
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
    public void enter() {
        printMenuTitle();
        int input = 0;
        int exit = 5;
        String questName;
        do {

            printOptions();
            try {
                input = sc.nextInt();
                switch (input) {
                    case 1:
                        questName = printQuests();
                        printQuest(questName);
                        break;
                    case 2:
                        questName = printQuests();
                        activateQuest(questName);
                        break;
                    case 3:
                        questName = printQuests();
                        completeQuest(questName);
                        break;
                    case 4:
                        questName = printQuests();
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
                System.out.println("\nInvalid input...\n");
                continue;
            }

        } while (input != exit);
    }

}
