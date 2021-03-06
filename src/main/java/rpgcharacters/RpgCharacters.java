package rpgcharacters;

import java.sql.*;
import java.util.Hashtable;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import rpgcharacters.commands.*;
import rpgcharacters.userflow.*;

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
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Output usage if no args or help option input
        if (args.length == 0) {
            Menu initMenu = new InitMenu(conn);
            initMenu.enter();
            return;
        }

        if (rpg.help) {
            jc.usage();
            return;
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
            e.printStackTrace();
        }
    }

}
