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
    private void printCharacters( ResultSet chars ) {
        // print a buffer line
        System.out.println();

        Int count = 0;
        chars.beforeFirst();

        while ( chars.next() ) {
            // only print divider before non-first characters (or after non-last characters)
            if ( count > 0 ) {
                System.out.println( "--------------------------------------------------" ); // '-' * 50
            }

            count++;

            printCharacter( count,
                results.getString( "name" ),
                results.getInt( "party_id" ),
                results.getString( "race" ),
                results.getString( "archetype" ),
                results.getString( "story" ),
                results.getInt( "power" ),
                results.getInt( "proficiency" ),
                results.getInt( "personality" ),
                results.getInt( "perception" ),
                results.getInt( "experience" )
            );
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
    private void printCharacter( Int number, String name, Int partyID,
                                 String race, String archetype, String story,
                                 Int power, Int proficiency, Int personality,
                                 Int perception, Int experience ) {

        System.out.println( "Character " + number );

        // print name
        System.out.println( "Name: " + name );

        // don't print the username if the user was already specified
        if ( username == "" ) {
            System.out.println( "Belongs to: " + name );
        }

        // if the character has a partyID, find the name of the associated party
        if ( partyID != null ) {
            String query = "SELECT name FROM party WHERE id=" + partyID + ";";
            Statement stmt = conn.createStatement();

            try {
                ResultSet result = stmt.execute( query );

                result.beforeFirst();

                // if the partyID matches an existing party
                if ( result.next() ) {
                    System.out.println( "Member of " + result.getString( name ) );
                }
                // no else, user shouldn't care
            } catch ( SQLException e ) {
                e.printStackTrace();
            }

            // TODO print out user's party members?
        }

        // print race and archetype
        System.out.println( "Race:      " + race );
        System.out.println( "Archetype: " + archetype );

        // if story exists, print it with max chars per line
        if ( story != null && story != "" ) {
            System.out.print( "Story: " );

            String[] descTokens = desc.split( " " );

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
            String query = "SELECT * FROM character";
            Statement stmt = conn.createStatement();
            ResultSet result;

            // if a username was specified, modify the query to get only that user's characters
            if ( username != "" ) {
                // count the users with this username (should be 0 or 1)
                String userCheck = "SELECT COUNT(username) AS usercount FROM user WHERE username='" + username + "';";
                result = stmt.execute( userCheck );

                // sanity check to go before first
                result.beforeFirst();

                // check if the result set actually has a row, and that the
                // usercount isn't 0 (i.e. a user of username==username exists)
                if ( result.next() && result.getInt( "usercount" ) == 1 ) {
                    query += " WHERE username='" + username + "';";
                } else {
                    System.out.println( "User with username " + username + " could not be found, listing all characters instead" );
                    username = "";
                    query += ";";
                }
            } else {
                query += ";";
            }

            result = stmt.execute( query );
            printCharacters( result );

        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

}
