package databaseHandling;

import ResultsParser.FinishedMatch;
import logsWriting.LogsWriter;

import java.sql.Array;
import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Created by Użytkownik on 12.03.2017.
 */
public class DatabaseUpdater {

    DatabaseHandler dbHandler = new DatabaseHandler();
    ArrayList<FinishedMatch> finishedMatches;

    public DatabaseUpdater(ArrayList<FinishedMatch> finishedMatches)
    {
        this.finishedMatches = finishedMatches;
    }



    public void update() {
        dbHandler.updateWinners(finishedMatches);
        dbHandler.updateUsersPoints();
        dbHandler.closeConnection();
        LogsWriter.writeLog("Finished updating database!");
    }
}
