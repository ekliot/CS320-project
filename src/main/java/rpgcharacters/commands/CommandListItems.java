package rpgcharacters.commands;

import java.sql.*;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "List all items")
public class CommandListItems implements Command {

    private void printQuest (String name,String desc) {
        System.out.println("Item Name: " + name);
        System.out.println("Item Description:");

        String[] descTokens = desc.split(" ");
        int curLen = 0;
        for (String tok : descTokens) {
          if (curLen + tok.length() > 46) {
              System.out.println(tok);
              curLen = 0;
          }
          else {
              System.out.print(tok + " ");
              curLen += tok.length();
          }
        }

        System.out.println("--------------------------------------------------"); // length 50
    }

    @Override
    public void run(Connection conn) {
        try {
            String query = "SELECT * FROM item";

            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);

            results.beforeFirst();

            while (results.next()) {
                String name = results.getString("name");
                String desc = results.getString("description");
                printQuest(name,desc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
