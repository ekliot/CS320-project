package rpgcharacters.userflow;
import java.util.Scanner;
import java.lang.Boolean;
import java.util.HashMap;

public class LoginMenu implements Menu {

    HashMap<String,String> accounts = new HashMap<String,String>();

    private Scanner sc;

    private boolean isAdmin = false;

    /**
     * Constructor Method
     * @param  Scanner sc    scanner inherited from the parent menu.
     */
    public LoginMenu (Scanner sc) {
        this.sc = sc;
        makeFakes();
    }

    private void makeFakes () {
        accounts.put("username", "password");
        accounts.put("admin", "admin");
        accounts.put("johnDoe", "iamcool123");
        accounts.put("gamerDood69", "fuckyeahV1D30games");
    }

    public boolean checkLogin (String user, String pass) {

        // TODO: Modify to use SQL

        if (user.equals("admin") && pass.equals("admin")) {
            isAdmin = true;
        }

        return (accounts.containsKey(user) &&
            accounts.get(user).equals(pass));
    }

    /**
    * Defines the loop for this menu
    */
    public void enter () {
        sc.nextLine();
        boolean validLogin = false;
        int wrongCount = 0;
        String user;
        do {

            System.out.print("Username: ");
            user = sc.nextLine();

            System.out.print("Password: ");
            String pass = sc.nextLine();

            validLogin = checkLogin(user,pass);

            if (!validLogin) {
                System.out.println("\nThe username and/or password is incorrect\n");
            }
            wrongCount++;

        } while (!validLogin && wrongCount <= 3);

        if (validLogin) {
            System.out.println("\nWelcome " + user + "!\n");
            Menu mainMenu = new MainMenu(sc,user,isAdmin);
            mainMenu.enter();
        }
        else {
            System.out.println("\nToo many attempts... Returning...\n");
        }
    }

}
