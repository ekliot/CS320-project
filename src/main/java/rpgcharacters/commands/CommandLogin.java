package rpgcharacters.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "User login")
public class CommandLogin implements Command {
    @Parameter(names = "--username",
               description = "Username of the user logging in",
               required = true)
    private String username;
    
    @Parameter(names = "--password",
               description = "Password of the user logging in",
               required = true)
    private String password;

    @Override
    public void run(Connection conn) {
        try {
            String query = "SELECT * FROM user "
                         + "WHERE username = '" + username + "' "
                         + "AND password = '" + password + "'"
                         + ";";

            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);
            
            // Use last() and getRow() to get number of rows
            results.last();
            if (results.getRow() == 1) {
                System.out.println("Welcome, " + results.getString("username") + "!");
            } else {
                System.out.println("Invalid Username/Password Combination");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
