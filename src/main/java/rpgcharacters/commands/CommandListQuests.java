package rpgcharacters.commands;

import java.sql.*;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "List all quests")
public class CommandListQuests implements Command {

    @Override
    public void run(Connection conn) {
        try {
            String query = "SELECT * FROM quest";

            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);

            Array names = results.getArray("name");
            Array descs = results.getArray("description");
            Array exps = results.getArray("experience");
            Array items = results.getArray("item_name");



            System.out.println("Welcome, " + username + "!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
