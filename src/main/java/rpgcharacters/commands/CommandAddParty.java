package rpgcharacters.commands;

import java.sql.*;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Add a character to a party")
public class CommandAddParty implements Command {
    @Parameter(names = "--name",
            description = "Name of character to add to party",
            required = true)
    private String name;

    @Parameter(names = "--party",
            description = "Name of party",
            required = true)
    private String party;

    @Parameter(names = "--username",
            description = "",
            required = true)
    private String username;

    @Override
    public void run(Connection connection) {
        try {
            String query = "SELECT * "
                    +"FROM character "
                    + "WHERE name ='" + this.name
                    + "' AND user_username ='" + this.username
                    + "' AND party_id IS NULL;";

            Statement st = connection.createStatement();
            ResultSet results = st.executeQuery(query);

            results.last();
            int total = results.getRow();
            if( total != 0 ) {
                // TODO: combine query and query2 into complex query
                query = "SELECT id, name "
                        + "FROM party "
                        + "WHERE name ='" + this.party + "';";

                Statement findID = connection.createStatement();
                ResultSet partyResult = findID.executeQuery(query);
                partyResult.first();

                String query2 = "UPDATE character "
                        + "SET party_id = " + partyResult.getInt("id")
                        + " WHERE name ='" + this.name + "'"
                        + " AND user_username='" + this.username + "';";

                Statement update = connection.createStatement();
                update.execute(query2);

                System.out.format("Character %s has been successfully added to the party %s.\n",
                        this.name, this.party);

            } else {
                System.out.format("Character %s is already in a party\n", this.name);
                //TODO: Potential future change to message
            }
        } catch( SQLException e) {
            e.printStackTrace();
        }
    }
}
