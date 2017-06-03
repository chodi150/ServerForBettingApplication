package ResultsParser;

/**
 * Created by Użytkownik on 05.02.2017.
 */
public class FinishedMatch {
    String loser;
    String winner;
    int winnerScore;
    int loserScore;
    int day;
    int month;

    public String getLoser() {
        return loser;
    }

    public void setLoser(String loser) {
        this.loser = loser;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getWinnerScore() {
        return winnerScore;
    }

    public void setWinnerScore(int winnerScore) {
        this.winnerScore = winnerScore;
    }

    public int getLoserScore() {
        return loserScore;
    }

    public void setLoserScore(int loserScore) {
        this.loserScore = loserScore;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }



    public FinishedMatch(String loser, int loserScore, String winner, int winnerScore, int day, int month) {
        this.loser = loser;
        this.winner = winner;
        this.winnerScore = winnerScore;
        this.loserScore = loserScore;
        this.day = day;
        this.month = month;
    }



    public void writeOutMatches()
    {
        System.out.println("Date: " + day +"/"+month + ": " + winner + " " + winnerScore + " : " + loserScore + " " + loser);
    }


}
