package rpgcharacters.commands;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;

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
        Scanner in = new Scanner(System.in);
        seedRace( conn, in );
        seedArchetype( conn, in );
        in.close();
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

            createAdminRole(conn);
            createUserRole(conn);

            query = "INSERT INTO user VALUES( 'admin', 'admin' );";
            stmt.execute( query );

            query = "GRANT dbAdmin TO public;";
            stmt.execute( query );
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

    private String[][] readParams(String filename) {
        ArrayList<String> lines = new ArrayList<String>();
        try {
            File inFile = new File (filename);
            Scanner in = new Scanner (inFile);
            while (in.hasNext()) {
                lines.add(in.nextLine());
            }
            String[][] lineToks = new String[lines.size()][lines.get(0).split(",").length];

            for (int i = 0; i < lines.size(); i++) {
                lineToks[i] = lines.get(i).split(",");
            }
            in.close();
            return lineToks;
        }
        catch (FileNotFoundException e) {
            System.out.println(e);
        }
        return null;
    }

    private void seedRace(Connection conn, Scanner in) {
        HashMap<String, int[]> raceSeeds = new HashMap<String, int[]>();
        System.out.print("Races seed file: ");
        String filename = in.nextLine();
        String[][] lines = readParams(filename);

        for (int i = 0; i < lines.length; i++) {
            // System.out.println(lines[i][0] + ", " + lines[i][1] + ", " + lines[i][2] + ", " + lines[i][3] + ", " + lines[i][4]);
            raceSeeds.put( lines[i][0], new int[]{Integer.parseInt(lines[i][1]),
                                                  Integer.parseInt(lines[i][2]),
                                                  Integer.parseInt(lines[i][3]),
                                                  Integer.parseInt(lines[i][4])
                                                 });
        }

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

    private void seedArchetype( Connection conn, Scanner in) {
        HashMap<String, int[]> archSeeds = new HashMap<String, int[]>();
        System.out.print("Archetypes seed file: ");
        String filename = in.nextLine();
        String[][] lines = readParams(filename);

        for (int i = 0; i < lines.length; i++) {
            // System.out.println(lines[i][0] + ", " + lines[i][1] + ", " + lines[i][2] + ", " + lines[i][3] + ", " + lines[i][4]);
            archSeeds.put( lines[i][0], new int[]{Integer.parseInt(lines[i][1]),
                                                  Integer.parseInt(lines[i][2]),
                                                  Integer.parseInt(lines[i][3]),
                                                  Integer.parseInt(lines[i][4])
                                                 });
        }

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
        in.close();
    }
    private void createAdminRole( Connection conn ) {
        try {
            Statement s = conn.createStatement();

            String adminRole = "CREATE ROLE dbAdmin;";
            s.execute(adminRole);

        } catch( SQLException e ) {
            e.printStackTrace();
        }
    }

    private void createUserRole( Connection conn ) {
        try {
            Statement s = conn.createStatement();

            String userRole = "CREATE ROLE dbUser;";
            s.execute(userRole);

        } catch( SQLException e ) {
            e.printStackTrace();
        }
    }
}
