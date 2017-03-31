package rpgcharacters.commands;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Create database tables")
public class CommandCreateTables implements Command {
    // heres a comment
    public void run(Connection conn) {
        // Creates all tables in the database
        try {
            String query = "CREATE TABLE IF NOT EXISTS user("
                         + "USERNAME VARCHAR(255) PRIMARY KEY,"
                         + "PASSWORD VARCHAR(255)"
                         + ");" ;

            Statement stmt = conn.createStatement();
            stmt.execute(query);
            // TODO: Create remaining tables
        } catch (SQLException e) {
            // TODO: Do something better here
            e.printStackTrace();
        }
    }
}
