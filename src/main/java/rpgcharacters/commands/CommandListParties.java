package rpgcharacters.commands;

import java.sql.*;
import java.util.ArrayList;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.StringKey;

@Parameters( commandDescription = "List all parties")
public class CommandListParties implements Command {

    private void printParty(String name, String gm, ArrayList<String> members) {
        System.out.format("Party name: %s\n" + name);
        System.out.format("Game Moderator: %s\n", gm);
        System.out.format("Members:\n");
        for (String mem : members) {
            System.out.format("\t%s\n", mem);
        }
    }

    @Override
    public void run(Connection connection) {
        ArrayList<String> resultsStr = new ArrayList<String>();
        try {
            String query = "SELECT * FROM party";
            Statement s = connection.createStatement();
            ResultSet results = s.executeQuery(query);

            results.beforeFirst();
            while( results.next() ) {
                String name = results.getString("name");
                String gm = results.getString("gm_username");
                int id = results.getInt("id");

                try {
                    String query2 = "SELECT * FROM character"
                            + " WHERE party_id=" + id;
                    Statement s2 = connection.createStatement();
                    ResultSet results2 = s2.executeQuery(query2);
                    results2.beforeFirst();
                    resultsStr.clear();
                    while( results2.next()) {
                        resultsStr.add(results2.getString("name"));
                    }
                } catch( SQLException e ) {
                    e.printStackTrace();
                }

                printParty(name, gm, resultsStr);
            }

        } catch( SQLException e ) {
            e.printStackTrace();
        }
    }
}
