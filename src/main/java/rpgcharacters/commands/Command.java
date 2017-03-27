package rpgcharacters.commands;

import java.sql.Connection;

public interface Command {
    
    public void run(Connection conn);

}
