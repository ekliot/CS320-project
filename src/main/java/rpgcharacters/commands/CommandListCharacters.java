package rpgcharacters.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters( commandDescription = "List created characters" )
public class CommandListCharacters implements Command {

    // TODO add flags to print specific data points
    // e.g. specify whether or not to print a character's items, fellow party members, etc.

    // pass as '... --name "example name" ...'
    @Parameter( names = { "--username", "--user" },
               description = "Username of the user whose characters to list",
               required = false )
    private String username = ""; // default to empty string for checking if param was proided

    /**
     * TODO docstring
     **/
    private void printCharacters( Connection conn, ResultSet chars ) {
        // print a buffer line
        System.out.println();

        int count = 0;
        
        try {
            chars.beforeFirst();
    
            while ( chars.next() ) {
                // only print divider before non-first characters (or after non-last characters)
                if ( count > 0 ) {
                    System.out.println( "--------------------------------------------------" ); // '-' * 50
                }
    
                count++;
    
                printCharacter( conn, count,
                    chars.getString( "user_username" ),
                    chars.getString( "character.name" ),
                    chars.getInt( "party_id" ),
                    chars.getString( "race_name" ),
                    chars.getString( "archetype_name" ),
                    chars.getString( "story" ),
                    chars.getInt( "power" ),
                    chars.getInt( "proficiency" ),
                    chars.getInt( "personality" ),
                    chars.getInt( "perception" ),
                    chars.getInt( "experience" ),
                    chars.getString( "party.name" )
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // if count was unmodified (i.e. ResultSet had no characters), print an error
        if ( count == 0 ) {
            System.out.println( "Could not find any characters"
            + ( username == "" ? "" : " for user " + username ) );
        }

        // print a buffer line
        System.out.println();
    }

    /**
     * TODO docstring
     **/
    private void printCharacter( Connection conn, int number, String user_username,
                                 String char_name, int partyID, String race,
                                 String archetype, String story, int power,
                                 int proficiency, int personality, int perception,
                                 int experience, String party_name ) {

        System.out.println( "Character " + number );

        // print name
        System.out.println( "Name: " + char_name );

        // don't print the username if the user was already specified
        if ( username == "" ) {
            System.out.println( "Belongs to: " + user_username );
        }

        // if the character has a partyID, find the name of the associated party
        // ResultSet getInt returns 0 if the value is null
        if ( partyID != 0 ) {
            System.out.println( "Member of " + party_name );
            // TODO print out user's party members?
        }

        // print race and archetype
        System.out.println( "Race:      " + race );
        System.out.println( "Archetype: " + archetype );

        // if story exists, print it with max chars per line
        if ( story != null && story != "" ) {
            System.out.print( "Story: " );

            String[] descTokens = story.split( " " );

            // start at 7 to account for "Story: "
            int curLen = 7;

            for ( String tok : descTokens ) {
                if ( curLen + tok.length() > 43 ) {
                    // move print cursor to new line and pad with spaces to keep
                    // it in line with "Story: "
                    System.out.print( "\n       " );
                    System.out.print( tok + " " );
                    // make sure to account for the token length and the extra space
                    curLen = 7 + tok.length() + 1;
                }
                else {
                    System.out.print( tok + " " );
                    // make sure to account for the extra space
                    curLen += tok.length() + 1;
                }
            }

            // print empty line to end the story print block
            System.out.println();
        }

        // print stats
        System.out.println( "Power:        " + power );
        System.out.println( "Proficiency:  " + proficiency );
        System.out.println( "Personality:  " + personality );
        System.out.println( "Perception:   " + perception );
        System.out.println( "Experience:   " + experience + "xp" );

        // TODO print out owned items?

    }

    /**
     * TODO docstring
    **/
    @Override
    public void run( Connection conn ) {
        try {
            // root (unfinished) query
            // TODO: also include race and archetype mods in this query
            String query = "SELECT * FROM character"
                         + " LEFT OUTER JOIN party ON character.party_id = party.id";
            Statement stmt = conn.createStatement();
            ResultSet result;

            // if a username was specified, modify the query to get only that user's characters
            if ( username != "" ) {
                // count the users with this username (should be 0 or 1)
                String userCheck = "SELECT COUNT(username) AS usercount FROM user WHERE username='" + username + "';";
                result = stmt.executeQuery( userCheck );

                // sanity check to go before first
                result.beforeFirst();

                // check if the result set actually has a row, and that the
                // usercount isn't 0 (i.e. a user of username==username exists)
                if ( result.next() && result.getInt( "usercount" ) == 1 ) {
                    query += " WHERE user_username='" + username + "';";
                } else {
                    System.out.println( "User with username " + username + " could not be found, listing all characters instead" );
                    username = "";
                    query += ";";
                }
            } else {
                query += ";";
            }

            result = stmt.executeQuery( query );
            printCharacters( conn, result );

        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

}
