package server;

import ResultsParser.FinishedMatch;
import logsWriting.LogsWriter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Użytkownik on 05.03.2017.
 */
public class ClientThread extends Thread {

    ServerClient serverClient;
    Calendar now = Calendar.getInstance();
    ObjectInputStream inFromClient;

    public ClientThread(Socket clientSocket, int idOfClient)
    {
        String username = obtainStringFromUser(clientSocket);
        String password = obtainStringFromUser(clientSocket);

        LoginValidator loginValidator = new LoginValidator();


        try
        {
            ObjectOutputStream outToClient = new ObjectOutputStream(clientSocket.getOutputStream());

            if(loginValidator.validate(username, password))
             {
                 LogsWriter.writeLog(username + " logged in");
                 serverClient = new ServerClient(clientSocket, idOfClient, username);
                 outToClient.writeObject(true);
             }
             else
             {
                 outToClient.writeObject(false);
                 return;
             }

        }
        catch (IOException e)
        {
            System.out.println("Sending login confirmation: " + e.getMessage());
        }


    }

    @Override
    public void run()
    {
        int today = now.get(Calendar.DAY_OF_MONTH);
        int thisMonth = now.get(Calendar.MONTH)+1;


        serverClient.getYesterdayMatches();
        serverClient.getUpcomingMatches(thisMonth, today);
        serverClient.sendUpcomingMatches();
        serverClient.sendFinishedMatches();
        serverClient.sendRank();
        serverClient.obtainTypes();

    }


    private String obtainStringFromUser(Socket clientSocket)
    {
         String string = "empty";
        try {
            if(inFromClient==null)
                 inFromClient = new ObjectInputStream(clientSocket.getInputStream());
            string = (String) inFromClient.readObject();
        } catch (IOException e) {
            System.out.println("ObtainStringFromUser: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return string;
    }









}
