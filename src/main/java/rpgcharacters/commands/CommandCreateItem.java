package rpgcharacters.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Create a item")
public class CommandCreateItem implements Command {
    // pass as '... --name "example name" ...'
    @Parameter(names = "--name",
               description = "Name of the item being created",
               required = true)
    private String name;

    // pass as '... --description "this is an example description" ...'
    @Parameter(names = "--description",
               description = "Description for the item being created",
               required = true)
    private String description;

    @Override
    public void run(Connection conn) {
        try {
            String query = "INSERT INTO item VALUES ("
                         + "'" + name + "'"
                         + ","
                         + "'" + description + "'"
                         + ");";

            Statement stmt = conn.createStatement();
            stmt.execute(query);
            System.out.println("Item " + name + " has been created!");
      } catch (SQLException e) {
          e.printStackTrace();
      }
    }

}
