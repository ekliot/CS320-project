package rpgcharacters.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Create a item")
public class CommandDeleteQuest implements Command {
    // pass as '... --name "example name" ...'
    @Parameter(names = "--name",
               description = "Name of the item being created",
               required = true)
    private String name;

    @Override
    public void run(Connection conn) {
        try {
            String query = "DELETE FROM quest " +
                           "WHERE name='" + name + "';";

            Statement stmt = conn.createStatement();
            stmt.execute(query);
            System.out.println("Quest " + name + " has been deleted.");
      } catch (SQLException e) {
          e.printStackTrace();
      }
    }

}
