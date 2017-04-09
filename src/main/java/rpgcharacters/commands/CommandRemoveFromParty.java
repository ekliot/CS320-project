package rpgcharacters.commands;

import java.sql.*;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Delete a character from a party")
public class CommandRemoveFromParty implements Command {

    @Parameter(names = "--name",
            description = "Name of character to delete from party",
            required = true)
    private String name;

    @Parameter(names = "--username",
            description = "Owner of character",
            required = true)
    private String username;
    
    @Parameter(names = "--party",
             description = "Name of party",
             required = true)
    private String party;

    @Override
    public void run(Connection connection) {
        try {
            String query = "SELECT * "
                    +"FROM character "
                    + "WHERE name = '" + this.name
                    + "' AND user_name ='" + this.username + "' "
                    + "AND party_id IS NOT NULL;";

            Statement st = connection.createStatement();
            ResultSet results = st.executeQuery(query);

            results.last();
            int total = results.getRow();
            if( total == 0 ) {
                System.out.format("Could not find character %s in party %s\n", this.name, this.party);
            } else {
                query = "UPDATE character "
                        + "SET party_id = NULL "
                        + "WHERE character.name ='" + this.name + "' "
                        + "AND character.user_username ='" + this.username + "';";
                Statement rm = connection.createStatement();
                rm.execute(query);
                System.out.format("Character %s has been successfully deleted from the party.\n",
                        this.name);
            }
        } catch( SQLException e ) {
            e.printStackTrace();
        }
    }
}
