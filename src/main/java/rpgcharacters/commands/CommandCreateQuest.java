package rpgcharacters.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Create a quest")
public class CommandCreateQuest implements Command {
    // pass as '... --name "example name" ...'
    @Parameter(names = "--name",
               description = "Name of the quest being created",
               required = true)
    private String name;

    // pass as '... --description "this is an example description" ...'
    @Parameter(names = "--description",
               description = "Description for the quest being created",
               required = true)
    private String description;

    // pass as '... --experience 35 ...'
    @Parameter(names = {"--experience","--exp"},
               description = "Ammount of experience gained by completing the quest",
               required = true)
    private int experience;

    @Override
    public void run(Connection conn) {
        try {
            String query = "INSERT INTO quest VALUES ("
                         + "'" + name + "'"
                         + ","
                         + "'" + description + "'"
                         + ","
                         + "'" + experience + "'"
                         + ");";

            Statement stmt = conn.createStatement();
            stmt.execute(query);
            System.out.println("Welcome, " + username + "!");
      } catch (SQLException e) {
          e.printStackTrace();
      }
    }

}
