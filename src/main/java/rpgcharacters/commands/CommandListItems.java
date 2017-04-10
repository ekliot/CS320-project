package rpgcharacters.commands;

import java.sql.*;

import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "List all items")
public class CommandListItems implements Command {

    private void printItem (String name,String desc) {
        System.out.println("Item Name: " + name);
        System.out.println("Item Description:");

        String[] descTokens = desc.split(" ");
        int curLen = 0;
        for (String tok : descTokens) {
          if (curLen + tok.length() > 46) {
              System.out.print("\n" + tok);
              curLen = 0;
          }
          else {
              System.out.print(tok + " ");
              curLen += tok.length() + 1;
          }
        }

        System.out.println("\n--------------------------------------------------"); // length 50
    }

    @Override
    public void run(Connection conn) {
        try {
            // TODO: include characters in this query
            String query = "SELECT * FROM item";

            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);

            results.beforeFirst();

            while (results.next()) {
                String name = results.getString("name");
                String desc = results.getString("description");
                printItem(name,desc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
