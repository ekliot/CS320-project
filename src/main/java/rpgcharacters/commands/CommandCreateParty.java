package rpgcharacters.commands;

import java.sql.*;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Create a party")
public class CommandCreateParty implements Command {
    @Parameter(names = "--name",
            description = "Name of new party",
            required = true)
    private String name;

    @Parameter(names = "--gm",
            description = "Name of user acting as the game master for the party",
            required = true)
    private String gm;

    public void run(Connection connection) {
        try {
            String query = "INSERT INTO party (name, gm_username) " +
                    "VALUES ('" + name + "', '" + gm + "');";

            Statement st = connection.createStatement();
            st.execute(query);
            // TODO: add success/fail output
        } catch( SQLException e) {
            e.printStackTrace();
        }
    }
}
