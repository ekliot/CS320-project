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
        try {
            String query = "CREATE TABLE IF NOT EXISTS user("
                         + "USERNAME VARCHAR(255) PRIMARY KEY,"
                         + "PASSWORD VARCHAR(255)"
                         + ");" ;

            Statement stmt = conn.createStatement();
            stmt.execute(query);
            // TODO: Create remaining tables

            seedRace( conn );
            seedArchetype( conn );
        } catch (SQLException e) {
            // TODO: Do something better here
            e.printStackTrace();
        }
    }

    /**
     * TODO
    **/
    private void seedRace( Connection conn ) {
        HashMap<String, Int[]> raceSeeds = new HashMap<String, Int[]>();

        //             race          pow  pers perc prof
        raceSeeds.put( "Human",     {   5,  10,   0,  10 } ); //  5 + 10 +  0 + 10 = 25
        raceSeeds.put( "Elf",       {   5,  15,  -5,  10 } ); //  5 + 15 -  5 + 15 = 25
        raceSeeds.put( "Half-Elf",  {   0,  10,   0,  15 } ); //  0 + 10 +  0 + 15 = 25
        raceSeeds.put( "Half-Ork",  {  20, -10,   5,  10 } ); // 20 - 10 +  5 + 10 = 25
        raceSeeds.put( "Ork",       {  30, -20,   5,  10 } ); // 30 - 20 +  5 + 10 = 25
        raceSeeds.put( "Halfling",  {   0,   0,   5,  20 } ); //  0 +  0 +  5 + 20 = 25
        raceSeeds.put( "Dwarf",     {  15,  -5,  15,   0 } ); // 15 -  5 + 15 +  0 = 25
        raceSeeds.put( "Gnome",     {  15, -10,  10,  10 } ); // 15 - 10 + 10 + 10 = 25
        raceSeeds.put( "Goblin",    {   5, -15,  10,  25 } ); //  5 - 15 + 10 + 25 = 25
        raceSeeds.put( "Fey",       {   5,  10, -15,  25 } ); //  5 + 10 - 15 + 25 = 25

        String insertRace = "INSERT INTO race VALUES ( '%s', %d, %d, %d, %d );";
        Statement stmt = conn.createStatement();

        for ( String race : raceSeeds.keySet() ) {
            Int[] statMods = raceSeeds[race];
            stmt.execute( String.format(
                    insertRace,
                    race, statMods[0], statMods[1], statMods[2], statMods[3]
                )
            );
        }
    }

    /**
     * TODO
    **/
    private void seedArchetype( Connection conn ) {
        HashMap<String, Int[]> archSeeds = new HashMap<String, Int[]>();

        //             archetype      pow pers perc prof
        archSeeds.put( "Warrior",   {  10,   0,   5,  10 } ); // 10 +  0 +  5 + 10 = 25
        archSeeds.put( "Monk",      {   5,   5,   5,  10 } ); //  5 +  5 +  5 + 10 = 25
        archSeeds.put( "Scoundrel", {   0,   5,   5,  15 } ); //  0 +  5 +  5 + 15 = 25
        archSeeds.put( "Druid",     {  10,  10,   0,   5 } ); // 10 + 10 +  0 +  5 = 25
        archSeeds.put( "Bard",      {   0,  15,   5,   5 } ); //  0 + 15 +  5 +  5 = 25
        archSeeds.put( "Cleric",    {  10,  15,   0,   0 } ); // 10 + 15 +  0 +  0 = 25
        archSeeds.put( "Sorceror",  {  20,   5,   0,   0 } ); // 20 +  5 +  0 +  0 = 25
        archSeeds.put( "Magus",     {  15,   0,   0,  10 } ); // 15 +  0 +  0 + 10 = 25
        archSeeds.put( "Shaman",    {  15,   5,   5,   0 } ); // 15 +  5 +  5 +  0 = 25
        archSeeds.put( "Ranger",    {   0,   0,  10,  15 } ); //  0 +  0 + 10 + 15 = 25

        String insertArch = "INSERT INTO archetype VALUES ( '%s', %d, %d, %d, %d );";
        Statement stmt = conn.createStatement();

        for ( String arch : archSeeds.keySet() ) {
            Int[] statMods = archSeeds[arch];
            stmt.execute( String.format(
                    insertArch,
                    arch, statMods[0], statMods[1], statMods[2], statMods[3]
                )
            );
        }
    }
}
