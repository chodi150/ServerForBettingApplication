package ResultsParser;

import databaseHandling.DatabaseUpdater;
import logsWriting.LogsWriter;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;

/**
 * Created by Użytkownik on 10.03.2017.
 */
public class ScheduledResultsParser extends TimerTask {

    HTMLBasketballResultsParser parser = new HTMLBasketballResultsParser();
    LogsWriter logsWriter = new LogsWriter();


    @Override
    public void run()
    {
        logsWriter.writeLog("Starting new thread to marse matches");
        ArrayList<FinishedMatch> finishedMatches = parser.parseResultsToArrayOfMatches();
        DatabaseUpdater dbUpdater = new DatabaseUpdater(finishedMatches);
        dbUpdater.update();

    }

}
