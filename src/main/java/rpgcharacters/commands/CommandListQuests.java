package rpgcharacters.commands;

import java.sql.*;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "List all quests")
public class CommandListQuests implements Command {

    private void printQuest (String name,String desc,int exp,String item) {
      System.out.println("Quest Name: " + name);
      System.out.println("Quest Description:");

      String[] descTokens = desc.split("(?<=\\G.{46})"); // split string into lines of max length 46
      for (String tok : descTokens) {
        System.out.println("\t" + tok);
      }

      System.out.println("Quest Exp: " + Integer.toString(exp));
      System.out.println("Quest Reward Item: " + item);

      System.out.println("--------------------------------------------------"); // length 50
    }

    @Override
    public void run(Connection conn) {
        try {
            String query = "SELECT * FROM quest";

            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(query);

            results.beforeFirst();
            results.last();
            int size = results.getRow();

            for (int i = 0; i < size; i++) {
              results.absolute(i);
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
