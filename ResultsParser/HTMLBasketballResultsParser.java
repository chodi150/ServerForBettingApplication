package ResultsParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by Użytkownik on 05.02.2017.
 */
public class HTMLBasketballResultsParser extends HTMLparser{


    public ArrayList<FinishedMatch> parseResultsToArrayOfMatches()
    {
        ArrayList<FinishedMatch>  finishedMatches = new ArrayList<>();
        ArrayList<String> winners = new ArrayList<>();
        ArrayList<String> losers = new ArrayList<>();
        ArrayList<Integer> loserScore = new ArrayList<>();
        ArrayList<Integer> winnerScore = new ArrayList<>();
        Document document;

        document = getHTMLcode("http://www.basketball-reference.com/boxscores/");

        Elements fieldsContainingAllInfoAboutLoser = document.select("tr.loser");
        Elements fieldsContainingAllInfoAboutWinners = document.select("tr.winner");

        addLosersToList(losers, fieldsContainingAllInfoAboutLoser);
        addWinnersToList(winners, fieldsContainingAllInfoAboutWinners);


        Elements loserScores = fieldsContainingAllInfoAboutLoser.select("td.right:not(.gamelink)");
        filterFieldScore(loserScore, loserScores);


        Elements winnerScores = fieldsContainingAllInfoAboutWinners.select("td.right:not(.gamelink)");
        filterFieldScore(winnerScore, winnerScores);
        Calendar cal = Calendar.getInstance();

        for(int i = 0; i<losers.size(); i++)
            finishedMatches.add(new FinishedMatch(losers.get(i), loserScore.get(i), winners.get(i), winnerScore.get(i),  cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH)+1));

            return finishedMatches;
        }

    private void filterFieldScore(ArrayList<Integer> score, Elements scores) {
        for(Element e : scores)
        {
            try{
                int a = Integer.parseInt(e.text());
                score.add(a);
            }
            catch (NumberFormatException t)
            {
                //Just do nothing
            }

        }

    }




    public void addLosersToList(ArrayList<String> losers, Elements trLoser) {


        Elements loserTeams = trLoser.select("td:not(.right)");

        for (Element e : loserTeams)
            losers.add(e.text());
    }

    public void addWinnersToList(ArrayList<String> winners, Elements trWinners) {
        Elements winnerTeams = trWinners.select("td:not(.right)");

        for (Element e : winnerTeams)
            winners.add(e.text());
    }







}
