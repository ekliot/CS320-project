package rpgcharacters.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters( commandDescription = "Create a character" )
public class CommandCreateItem implements Command {

    /**
     || TODO
     || replace command args with a questioning stdin/out process
     || e.g.
     || > What is your character's name?
     || > {user input}
     || > What race is your character?
     || > {user input}
     || etc.
     **/

    // pass as '... --name "John Cena" ...'
    @Parameter( names = "--name",
                description = "Name of the character being created",
                required = true )
    private String name;

    // pass as '... --username "jcena" ...'
    @Parameter( names = "--username",
                description = "Username of the character's owner",
                required = true )
    private String username;

    // deliberately omitting `party` from char creation, characters ought to be
    // added to a party after creation

    // pass as '... --race "Human" ...'
    @Parameter( names = "--race",
                description = "Name of the character's race",
                required = true )
    private String race;

    // pass as '... --archetype "Legend" ...'
    @Parameter( names = "--archetype",
                description = "Name of the character's archetype",
                required = true )
    private String archetype;

    // pass as '... --story "Cena started training to become a professional wre..." ...'
    @Parameter( names = "--story",
                description = "The character's story",
                required = false )
    private String story = "NULL"; // default value to make it possible to check if it was provided

    // pass as '... --power 9 ...'
    @Parameter( names = "--power",
                description = "The character's power stat",
                required = true )
    private int power;

    // pass as '... --proficiency 9 ...'
    @Parameter( names = "--proficiency",
                description = "The character's proficiency stat",
                required = true )
    private int proficiency;

    // pass as '... --perception 9 ...'
    @Parameter( names = "--perception",
                description = "The character's perception stat",
                required = true )
    private int perception;

    // pass as '... --personality 9 ...'
    @Parameter( names = "--personality",
                description = "The character's personality stat",
                required = true )
    private int personality;

    @Override
    public void run( Connection conn ) {
        try {
            String query =
                "INSERT INTO character VALUES ( "
                + "'" + username    + "', "
                + "'" + name        + "', "
                + "NULL, " // party_id = NULL
                + "'" + race        + "', "
                + "'" + archetype   + "', "
                + ( story != "NULL" // wrap story in single-quotes if story is provided
                  ? "'" + story     + "'"
                  : story
                ) + ", "
                + power       + ", "
                + proficiency + ", "
                + personality + ", "
                + perception  + ", "
                + 0           + ", " // characters have 0 xp on creation
                + " );";

            Statement stmt = conn.createStatement();
            stmt.execute( query );
            System.out.println( username + "'s character, " + name + ", has been created!" );
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

}
