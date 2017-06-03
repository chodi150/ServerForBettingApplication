package server;

import databaseHandling.DatabaseHandler;

/**
 * Created by Użytkownik on 11.03.2017.
 */
public class LoginValidator {

    DatabaseHandler dbHandler = new DatabaseHandler();
    public boolean validate(String username, String password)
    {
        boolean result  =dbHandler.checkIfUserExists(username,password);
        System.out.println("RESULT: " + result);
       return result;
    }
}
