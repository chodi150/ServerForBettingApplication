package server;

import ResultsParser.FinishedMatch;
import ResultsParser.ScheduledResultsParser;
import databaseHandling.DatabaseHandler;
import server.ClientThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;


/**
 * Created by Użytkownik on 09.02.2017.
 */
public class Server{


    ScheduledResultsParser scheduledResultsParser = new ScheduledResultsParser();
    DatabaseHandler dbHandler = new DatabaseHandler();
    private Thread clientThread;
    ServerSocket serverSocket = new ServerSocket(8096);
    long delay = 54000000+1200000;

    int id = 0;
    public Server() throws IOException
    {
        Timer timer = new Timer();
        final long dayInMilliseconds = 86400000;

        timer.scheduleAtFixedRate(scheduledResultsParser, 0, dayInMilliseconds);
        while(true)
        {
            Socket connect = serverSocket.accept();
            clientThread = new ClientThread(connect, id++);
            clientThread.start();
        }
    }
}
