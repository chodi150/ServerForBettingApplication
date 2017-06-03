package ResultsParser;

import java.util.ArrayList;

/**
 * Created by Użytkownik on 07.02.2017.
 */
public class DateParser{
    DateParser()
    {

    }

    public ArrayList<Integer> parseDate(ArrayList<String> dates)
    {
        ArrayList<Integer> days = new ArrayList<>();
        String str;
        for(int i = 0; i<dates.size(); i++)
        {
            if(dates.get(i).length()==16)
                 str= dates.get(i).substring(9,10);
            else
                str = dates.get(i).substring(9,11);

            days.add(Integer.parseInt(str));
        }
        return days;
    }



}
