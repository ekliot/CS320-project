package rpgcharacters.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Delete a item")
public class CommandDeleteItem implements Command {
    // pass as '... --name "example name" ...'
    @Parameter(names = "--name",
               description = "Name of the item being created",
               required = true)
    private String name;

    @Override
    public void run(Connection conn) {
        try {
            String checkQuery = "SELECT * FROM item " +
                                "WHERE name='" + name + "';";

            Statement checkStmt = conn.createStatement();
            ResultSet results = checkStmt.executeQuery(checkQuery);

            results.last();
            int total = results.getRow();

            if (total == 0) {
                System.out.println("Item " + name + " does not exist.");
            }
            else {
                String deleteQuery = "DELETE FROM item " +
                                     "WHERE name='" + name + "';";

                // TODO: remove select query above
                // use executeUpdate here (returns an int for number of rows affected)
                // if returned value is 0, item doesn't exist
                Statement deleteStmt = conn.createStatement();
                deleteStmt.execute(deleteQuery);

                System.out.println("Item " + name + " has been deleted.");
            }
      } catch (SQLException e) {
          e.printStackTrace();
      }
    }

}
