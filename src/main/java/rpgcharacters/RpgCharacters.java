package rpgcharacters;

import java.sql.*;
import java.util.Hashtable;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import rpgcharacters.commands.*;
import rpgcharacters.userFlow.*;

public class RpgCharacters {
    @Parameter(names = { "--help", "-h" },
               description = "Show this message",
               help = true)
    private boolean help;

    public static void main(String[] args) {
        // Initialize CLI
        RpgCharacters rpg = new RpgCharacters();
        JCommander jc = new JCommander(rpg);
        jc.setProgramName("RpgCharacters");

        // Add commands
        Hashtable<String, Command> commands = new Hashtable<String, Command>();

        commands.put("create-tables", new CommandCreateTables());

        commands.put("login", new CommandLogin());

        commands.put("create-user", new CommandCreateUser());
        commands.put("create-character", new CommandCreateCharacter());
        commands.put("create-item", new CommandCreateItem());
        commands.put("create-quest", new CommandCreateQuest());
        commands.put("add-party", new CommandAddParty());
        commands.put("remove-from-party", new CommandRemoveFromParty());
        commands.put("create-party", new CommandCreateParty());
        commands.put("list-party", new CommandListParties());
        commands.put("delete-quest", new CommandDeleteQuest());
        commands.put("delete-item", new CommandDeleteItem());
        commands.put("list-quests", new CommandListQuests());
        commands.put("list-items", new CommandListItems());
        commands.put("list-characters", new CommandListCharacters());

        for (String command : commands.keySet()) {
            jc.addCommand(command, commands.get(command));
        }

        // Parse args
        jc.parse(args);

        // Create db connection
        Connection conn;

        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:~/h2db",
                                               "foreignkeys",
                                               "password");
        } catch ( SQLException | ClassNotFoundException e ) {
            // TODO: Do something better here
            e.printStackTrace();
            return;
        }

        // Output usage if no args or help option input
        if (args.length == 0) {

            Menu initMenu = new InitMenu();
            initMenu.enter( conn );
            return;

        }

        if (rpg.help) {
            jc.usage();
            return;
        }
        //Create Roles
        try {
            Statement s = conn.createStatement();

            String userView = "CREATE VIEW userView AS "
                            + "SELECT * "
                            + "FROM party, character;";
            s.executeQuery(userView);

            //Change later
            String adminView = "CREATE VIEW adminView AS "
                             + "SELECT * "
                             + "FROM *;";
            s.executeQuery(adminView);

            String adminRole = "CREATE ROLE dbAdmin;";
            s.executeQuery(adminRole);

            String userRole = "CREATE ROLE dbUser;";
            s.executeQuery(userRole);

            String userPrivileges = "GRANT READ, INSERT, UPDATE, DELETE "
                                  + "ON userView "
                                  + "TO dbUser, dbAdmin;";
            s.executeQuery(userPrivileges);

            String adminPrivileges = "GRANT INDEX, RESOURCES, ALTER, DROP "
                                   + "ON adminView "
                                   + "TO dbAdmin;";
            s.executeQuery(adminPrivileges);

        } catch( SQLException e ) {
            e.printStackTrace();
        }
        // Run desired command
        for (String command : commands.keySet()) {
            if (jc.getParsedCommand() == command) {
                commands.get(command).run(conn);
                break;
            }
        }

        // Close db connection
        try {
            conn.close();
        } catch (SQLException e) {
            // TODO: Do something better here
            e.printStackTrace();
        }
    }

}
