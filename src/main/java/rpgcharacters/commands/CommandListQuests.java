package rpgcharacters.commands;

import java.sql.*;

import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "List all quests")
public class CommandListQuests implements Command {

    private void printQuest (String name,String desc,int exp,String item) {
        System.out.println("Quest Name: " + name);
        System.out.println("Quest Description:");

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

        System.out.println("Quest Exp: " + Integer.toString(exp));
        System.out.println("Quest Reward Item: " + item);

        System.out.println("--------------------------------------------------"); // length 50
    }

    @Override
    public void run(Connection conn) {
        try {
            // TODO: include item and quest in this query
            String query = "SELECT * FROM quest";

            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);

            results.beforeFirst();

            while (results.next()) {
                String name = results.getString("name");
                String desc = results.getString("description");
                int exp = results.getInt("experience");
                String item = results.getString("item_name");
                printQuest(name,desc,exp,item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
