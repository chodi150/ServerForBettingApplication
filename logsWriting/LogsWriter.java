package logsWriting;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Użytkownik on 15.03.2017.
 */
public class LogsWriter {




    public static void writeLog(String log)
    {

        try {
            FileWriter writer = new FileWriter("logs.txt", true);
            final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();

            writer.write(sdf.format(cal.getTime())+": " + log + "\n");

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
