package rpgcharacters.userflow;

import java.sql.Connection;
import java.util.Scanner;

public interface Menu {

    /**
     * Defines the loop for this menu and provide a database connection
     *
     * @param Connection conn The database connection inherited from the calling menu
    **/
    public void enter ( Connection conn );

}
