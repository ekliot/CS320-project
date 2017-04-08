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
            String query = "INSEERT INTO party ('"
                            + this.name + "', '" + this.gm
                         + "') VALUES("
                         + "'" + this.name + "'"
                         + "'" + this.name + "'"
                         + "'" + this.gm + "';";

            Statement st = connection.createStatement();
            st.executeQuery(query);
        } catch( SQLException e) {
            e.printStackTrace();
        }
    }
}
