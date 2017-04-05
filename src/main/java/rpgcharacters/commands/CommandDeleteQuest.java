package rpgcharacters.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Delete quest")
public class CommandDeleteQuest implements Command {
    // pass as '... --name "example name" ...'
    @Parameter(names = "--name",
               description = "Name of the quest to delete",
               required = true)
    private String name;

    @Override
    public void run(Connection conn) {
        try {
          String checkQuery = "SELECT * FROM quest" +
                              "WHERE name='" + name + "';";

          Statement checkStmt = conn.createStatement();
          ResultSet results = checkStmt.executeQuery(checkQuery);

          results.last();
          int total = results.getRow();

          if (total == 0) {
            System.out.println("Quest " + name + " does not exist.");
          }
          else {
            String deleteQuery = "DELETE FROM quest " +
                                 "WHERE name='" + name + "';";

            Statement deleteStmt = conn.createStatement();
            deleteStmt.execute(deleteQuery);

            System.out.println("Quest " + name + " has been deleted.");
          }
      } catch (SQLException e) {
          e.printStackTrace();
      }
    }

}
