package rpgcharacters.commands;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Create database tables")
public class CommandCreateTables implements Command {
    // heres a comment
    public void run(Connection conn) {
        // Creates all tables in the database
        createUserTable(conn);
        createRaceTable(conn);
        createArchetypeTable(conn);
        createItemTable(conn);
        createPartyTable(conn);
        createCharacterTable(conn);
        createQuestTable(conn);
        createCharacterItemTable(conn);
        createPartyQuestTable(conn);
    }
    
    private void createUserTable(Connection conn) {
        try {
            String query = "CREATE TABLE IF NOT EXISTS user("
                         + "username VARCHAR(255) PRIMARY KEY,"
                         + "password VARCHAR(255) NOT NULL"
                         + ");";

            Statement stmt = conn.createStatement();
            stmt.execute(query);
            System.out.println("User table created!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void createRaceTable(Connection conn) {
        try {
            String query = "CREATE TABLE IF NOT EXISTS race("
                         + "name VARCHAR(255) PRIMARY KEY,"
                         + "power_mod INT NOT NULL,"
                         + "personality_mod INT NOT NULL,"
                         + "perception_mod INT NOT NULL,"
                         + "proficiency_mod INT NOT NULL"
                         + ");";

            Statement stmt = conn.createStatement();
            stmt.execute(query);
            System.out.println("Race table created!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void createArchetypeTable(Connection conn) {
        try {
            String query = "CREATE TABLE IF NOT EXISTS archetype("
                    + "name VARCHAR(255) PRIMARY KEY,"
                    + "power_mod INT NOT NULL,"
                    + "personality_mod INT NOT NULL,"
                    + "perception_mod INT NOT NULL,"
                    + "proficiency_mod INT NOT NULL"
                    + ");";

            Statement stmt = conn.createStatement();
            stmt.execute(query);
            System.out.println("Archetype table created!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void createItemTable(Connection conn) {
        try {
            String query = "CREATE TABLE IF NOT EXISTS item("
                         + "name VARCHAR(255) PRIMARY KEY,"
                         + "description VARCHAR(255)"
                         + ");";

            Statement stmt = conn.createStatement();
            stmt.execute(query);
            System.out.println("Item table created!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void createPartyTable(Connection conn) {
        try {
            String query = "CREATE TABLE IF NOT EXISTS party("
                         + "id INT AUTO_INCREMENT PRIMARY KEY,"
                         + "name VARCHAR(255) NOT NULL,"
                         + "gm_username VARCHAR(255) NOT NULL,"
                         + "FOREIGN KEY (gm_username) references user(username)"
                         + ");";

            Statement stmt = conn.createStatement();
            stmt.execute(query);
            System.out.println("Party table created!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void createCharacterTable(Connection conn) {
        try {
            String query = "CREATE TABLE IF NOT EXISTS character("
                         + "user_username VARCHAR(255) NOT NULL,"
                         + "name VARCHAR(255) NOT NULL,"
                         + "party_id INT NOT NULL,"
                         + "race_name VARCHAR(255) NOT NULL,"
                         + "archetype_name VARCHAR(255) NOT NULL,"
                         + "story VARCHAR(255),"
                         + "power INT NOT NULL,"
                         + "personality INT NOT NULL,"
                         + "perception INT NOT NULL,"
                         + "proficiency INT NOT NULL,"
                         + "experience INT NOT NULL,"
                         + "PRIMARY KEY (user_username, name),"
                         + "FOREIGN KEY (user_username) references user(username),"
                         + "FOREIGN KEY (party_id) references party(id),"
                         + "FOREIGN KEY (race_name) references race(name),"
                         + "FOREIGN KEY (archetype_name) references archetype(name)"
                         + ");";

            Statement stmt = conn.createStatement();
            stmt.execute(query);
            System.out.println("Character table created!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void createQuestTable(Connection conn) {
        try {
            String query = "CREATE TABLE IF NOT EXISTS quest("
                         + "name VARCHAR(255) PRIMARY KEY,"
                         + "description VARCHAR(255),"
                         + "experience INT NOT NULL,"
                         + "item_name VARCHAR(255) NOT NULL,"
                         + "FOREIGN KEY (item_name) references item(name)"
                         + ");";

            Statement stmt = conn.createStatement();
            stmt.execute(query);
            System.out.println("Quest table created!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void createCharacterItemTable(Connection conn) {
        try {
            String query = "CREATE TABLE IF NOT EXISTS character_item("
                         + "user_username VARCHAR(255) NOT NULL,"
                         + "character_name VARCHAR(255) NOT NULL,"
                         + "item_name VARCHAR(255) NOT NULL,"
                         + "PRIMARY KEY (user_username, character_name, item_name),"
                         + "FOREIGN KEY (user_username) references user(username),"
                         + "FOREIGN KEY (character_name) references character(name),"
                         + "FOREIGN KEY (item_name) references item(name)"
                         + ");";

            Statement stmt = conn.createStatement();
            stmt.execute(query);
            System.out.println("CharacterItem table created!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void createPartyQuestTable(Connection conn) {
        try {
            String query = "CREATE TABLE IF NOT EXISTS party_quest("
                         + "party_id INT NOT NULL,"
                         + "quest_name VARCHAR(255) NOT NULL,"
                         + "status VARCHAR(255) NOT NULL,"
                         + "PRIMARY KEY (party_id, quest_name),"
                         + "FOREIGN KEY (party_id) references party(id),"
                         + "FOREIGN KEY (quest_name) references quest(name)"
                         + ");";

            Statement stmt = conn.createStatement();
            stmt.execute(query);
            System.out.println("PartyQuest table created!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
