package rpgcharacters.commands;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

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

        seedRace( conn );
        seedArchetype( conn );
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
                         + "proficiency_mod INT NOT NULL,"
                         + "personality_mod INT NOT NULL,"
                         + "perception_mod INT NOT NULL"
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
                         + "proficiency_mod INT NOT NULL,"
                         + "personality_mod INT NOT NULL,"
                         + "perception_mod INT NOT NULL"
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
                         + "description VARCHAR(65535)"
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
                         + "party_id INT,"
                         + "race_name VARCHAR(255) NOT NULL,"
                         + "archetype_name VARCHAR(255) NOT NULL,"
                         + "story VARCHAR(65535),"
                         + "power INT NOT NULL,"
                         + "proficiency INT NOT NULL,"
                         + "personality INT NOT NULL,"
                         + "perception INT NOT NULL,"
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
                         + "description VARCHAR(65535),"
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
                         + "FOREIGN KEY (quest_name) references quest(name),"
                         + "CHECK (status in ('Active', 'Complete'))"
                         + ");";

            Statement stmt = conn.createStatement();
            stmt.execute(query);
            System.out.println("PartyQuest table created!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO
    **/
    private void seedRace( Connection conn ) {
        HashMap<String, int[]> raceSeeds = new HashMap<String, int[]>();

        //             race                   pow prof pers perc
        raceSeeds.put( "Human",    new int[]{   5,  10,  10,   0 } ); //  5 + 10 + 10 +  0 = 25
        raceSeeds.put( "Elf",      new int[]{   5,  10,  15,  -5 } ); //  5 + 10 + 15 -  5 = 25
        raceSeeds.put( "Half-Elf", new int[]{   0,  15,  10,   0 } ); //  0 + 15 + 10 +  0 = 25
        raceSeeds.put( "Half-Ork", new int[]{  20,  10, -10,   5 } ); // 20 + 10 - 10 +  5 = 25
        raceSeeds.put( "Ork",      new int[]{  30,  10, -20,   5 } ); // 30 + 10 - 20 +  5 = 25
        raceSeeds.put( "Halfling", new int[]{   0,  20,   0,   5 } ); //  0 +  0 +  5 + 20 = 25
        raceSeeds.put( "Dwarf",    new int[]{  15,   0,  -5,  15 } ); // 15 +  0 -  5 + 15 = 25
        raceSeeds.put( "Gnome",    new int[]{  15,  10, -10,  10 } ); // 15 + 10 - 10 + 10 = 25
        raceSeeds.put( "Goblin",   new int[]{   5,  25, -15,  10 } ); //  5 + 25 - 15 + 10 = 25
        raceSeeds.put( "Fey",      new int[]{   5,  25,  10, -15 } ); //  5 + 25 + 10 - 15 = 25

        String insertRace = "INSERT INTO race VALUES ( '%s', %d, %d, %d, %d );";
        try {
            Statement stmt = conn.createStatement();

            for ( String race : raceSeeds.keySet() ) {
                int[] statMods = raceSeeds.get(race);
                stmt.execute( String.format(
                        insertRace,
                        race, statMods[0], statMods[1], statMods[2], statMods[3]
                    )
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO
    **/
    private void seedArchetype( Connection conn ) {
        HashMap<String, int[]> archSeeds = new HashMap<String, int[]>();

        //             archetype               pow prof pers perc
        archSeeds.put( "Warrior",   new int[]{  10,  10,   0,   5 } ); // 10 + 10 +  0 +  5 = 25
        archSeeds.put( "Monk",      new int[]{   5,  10,   5,   5 } ); //  5 + 10 +  5 +  5 = 25
        archSeeds.put( "Scoundrel", new int[]{   0,  15,   5,   5 } ); //  0 + 15 +  5 +  5 = 25
        archSeeds.put( "Druid",     new int[]{  10,   5,  10,   0 } ); // 10 +  5 + 10 +  0 = 25
        archSeeds.put( "Bard",      new int[]{   0,   5,  15,   5 } ); //  0 +  5 + 15 +  5 = 25
        archSeeds.put( "Cleric",    new int[]{  10,   0,  15,   0 } ); // 10 +  0 + 15 +  0 = 25
        archSeeds.put( "Sorceror",  new int[]{  20,   0,   5,   0 } ); // 20 +  0 +  5 +  0 = 25
        archSeeds.put( "Magus",     new int[]{  15,  10,   0,   0 } ); // 15 + 10 +  0 +  0 = 25
        archSeeds.put( "Shaman",    new int[]{  15,   0,   5,   5 } ); // 15 +  0 +  5 +  5 = 25
        archSeeds.put( "Ranger",    new int[]{   0,  15,   0,  10 } ); //  0 + 15 +  0 + 10 = 25

        String insertArch = "INSERT INTO archetype VALUES ( '%s', %d, %d, %d, %d );";
        try {
            Statement stmt = conn.createStatement();

            for ( String arch : archSeeds.keySet() ) {
                int[] statMods = archSeeds.get(arch);
                stmt.execute( String.format(
                        insertArch,
                        arch, statMods[0], statMods[1], statMods[2], statMods[3]
                    )
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
