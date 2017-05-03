package rpgcharacters.commands;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Create a user")
public class CommandCreateUser implements Command {
    @Parameter(names = "--username",
               description = "Username of the user to create",
               required = true)
    private String username;
    
    @Parameter(names = "--password",
               description = "Password of the user to create",
               required = true)
    private String password;

    @Override
    public void run(Connection conn) {
        try {
            String query = "INSERT INTO user VALUES ("
                         + "'" + username + "'"
                         + ","
                         + "'" + password + "'"
                         + ");";

            Statement stmt = conn.createStatement();
            stmt.execute(query);

            query = "GRANT dbUser "
                  + "TO (SELECT username "
                  +     "FROM user "
                  +     "WHERE username = '" + this.username + "');";
            stmt.executeQuery(query);

            query = "GRANT dbAdmin "
                  + "TO (SELECT admin "
                  +     "FROM user "
                  +     "WHERE admin = TRUE);";
            stmt.executeQuery(query);

            System.out.println("Welcome, " + username + "!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
