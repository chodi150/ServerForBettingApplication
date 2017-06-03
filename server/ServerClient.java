package server;

import ResultsParser.FinishedMatch;
import chodi.Match;
import chodi.UserPointsPair;
import databaseHandling.DatabaseHandler;
import logsWriting.LogsWriter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * Created by Użytkownik on 10.03.2017.
 */
public class ServerClient {
    private ArrayList<Match> upcomingMatches;
    private ArrayList<Match> finishedMatches;
    private int idOfClient;
    private String username;
    private Socket clientSocket;
    private DatabaseHandler dbHandler = new DatabaseHandler();
    private ObjectOutputStream outToClient;
    private ObjectInputStream inFromClient;

    public ServerClient(Socket clientSocket, int idOfClient, String username)
    {
        this.clientSocket = clientSocket;
        this.idOfClient = idOfClient;
        this.username = username;
    }

    public void getUpcomingMatches(int month, int day)
    {
        upcomingMatches = dbHandler.getMatches(month, day, username);
    }

    public void getYesterdayMatches()
    {
        finishedMatches = dbHandler.getYesterdayMatches(username);
    }



    public void sendUpcomingMatches()
    {
        try {
            if(outToClient==null)
                outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
            outToClient.writeObject(upcomingMatches);
        } catch (IOException e) {
            System.out.println("sendUpcomingMatches: " + e.getMessage());
        }
    }

    public void obtainTypes()
    {
        dbHandler.closeConnection();
        dbHandler = null;

            try {
                final int TEN_MINUTES = 600000;
                clientSocket.setSoTimeout(TEN_MINUTES);
                if(inFromClient==null)
                    inFromClient = new ObjectInputStream(clientSocket.getInputStream());
                int[][] userTypes= (int[][])inFromClient.readObject();
                dbHandler = new DatabaseHandler();
                dbHandler.insertTypesIntoDatabase(userTypes, username);
            }catch (SocketTimeoutException s) {
                LogsWriter.writeLog("Did not obtain types in 10 min from logging in...");
            }
            catch (IOException e) {
               System.out.println("obtainTypes: " +e.getMessage());
            } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }

    }


    public void sendRank()
    {
        ArrayList<UserPointsPair> rank = dbHandler.getRankOfPlayers();
        try
        {
            if(outToClient==null)
                outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
            outToClient.writeObject(rank);

        }
        catch (IOException e)
        {
            System.out.println("sendRank: " +e.getMessage());
        }
    }

    public void sendFinishedMatches()
    {
        try {
            if(outToClient==null)
                outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
            outToClient.writeObject(finishedMatches);
        } catch (IOException e) {
            System.out.println("sendFinishedMatches: " +e.getMessage());
        }
    }

    private void endConnectionWithDatabase()
    {
        dbHandler.closeConnection();
    }
    public void updateUsersPoints()
    {
        dbHandler.calculatePoints(username, finishedMatches);
    }

}
