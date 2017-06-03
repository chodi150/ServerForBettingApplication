package databaseHandling;

import ResultsParser.FinishedMatch;


import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import chodi.Match;
import chodi.UserPointsPair;
import logsWriting.LogsWriter;

/**
 * Created by Użytkownik on 09.02.2017.
 */
public class DatabaseHandler {
    final int TYPE_COLUMN = 1;
    final int ID_COLUMN = 0;
    private static Connection con;


    public DatabaseHandler() {
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:SQLiteTest1.db");

        } catch (ClassNotFoundException e) {
            throw new Error("class not found");
        } catch (SQLException e) {
            throw new Error("SQLexep");
        }
    }

    synchronized public void closeConnection()
    {
        try {
            con.close();
        } catch (SQLException e) {
            LogsWriter.writeLog("closing db: " + e.getMessage());
        }
    }


    synchronized public void updateWinners(ArrayList<FinishedMatch> finishedMatches)
    {
        if(finishedMatches == null || finishedMatches.isEmpty())
            throw new RuntimeException("Unable to update winners");

        for(FinishedMatch m : finishedMatches)
            updateWinnerOfSingleMatch(m);
    }

    synchronized private void updateWinnerOfSingleMatch(FinishedMatch finishedMatch)
    {
        CharSequence winner = finishedMatch.getWinner();
        CharSequence loser = finishedMatch.getLoser();

        try {
            Statement state = con.createStatement();
            ResultSet res = state.executeQuery("SELECT * FROM matches WHERE day=" + getYesterday() + " AND month=" + getYesterdayMonth());
            while (res.next())
            {
                LogsWriter.writeLog(res.getString("team1") + " vs " + res.getString("team2") + " " +getYesterday() + " " + getYesterdayMonth());
                if ((res.getString("team1").contains(winner) && res.getString("team2").contains(loser))
                        || (res.getString("team1").contains(loser) && res.getString("team2").contains(winner)))
                {

                    int id = res.getInt("id");
                    int whoWon = res.getString("team1").contains(winner) ? 1 : 2;
                    Statement updateStatement = con.createStatement();
                    updateStatement.executeUpdate("UPDATE matches SET winner=" + whoWon + " WHERE id=" +id);

                    return;
                }
            }
        } catch (SQLException e) {
            LogsWriter.writeLog("updateWinnerOfSingleMatch: " + e.getMessage());
        }
    }

    synchronized public void updateUsersPoints()
    {
        ArrayList<String> users = getUsers();

        for(String user : users)
            updateSingleUsersPoints(user);


    }

    synchronized private void updateSingleUsersPoints(String username)
    {
        int points = getCurrentNumberOfPoints(username);
        try {
            Statement statement = con.createStatement();
            Statement secondStatement = con.createStatement();
            ResultSet yesterdayMatchesWinners = statement.executeQuery("SELECT * FROM matches WHERE day=" + getYesterday() + " AND month=" + getYesterdayMonth());
            ResultSet userTypes = secondStatement.executeQuery("SELECT type FROM  \"" + username + "\"WHERE day=" + getYesterday() + " AND month=" + getYesterdayMonth());


            while(yesterdayMatchesWinners.next() && userTypes.next())
            {
                if(yesterdayMatchesWinners.getInt("winner")==userTypes.getInt("type"))
                    points++;
            }
            statement.executeUpdate("UPDATE users SET points = " + points + " WHERE username = \"" + username + "\"");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




     synchronized public boolean checkIfUserExists(String user, String pass)
     {
         boolean isUserFound = false;

         try {
             Statement state = con.createStatement();
             ResultSet res = state.executeQuery("SELECT * FROM users");

             while (res.next()) {
                 if (res.getString("username").equals(user) && res.getString("password").equals(pass))
                     isUserFound = true;
             }
         } catch (SQLException e) {
             LogsWriter.writeLog("checkIfUserExists: " +e.getMessage());
         }

         return isUserFound;
    }

     synchronized public ArrayList<Match> getMatches(int monthNr, int dayNr, String username) {

        ArrayList<Match> matches = new ArrayList<>();
        String team1;
        String team2;
        int id;
        int userType;
        try {
            Statement state = con.createStatement();
            ResultSet res = state.executeQuery("SELECT * FROM " + username+ " WHERE day=" + dayNr + " AND month=" + monthNr);

            while (res.next() ) {
                team1 = res.getString("team1");
                team2 = res.getString("team2");
                id = res.getInt("id");
                userType = res.getInt("type");

                matches.add(new Match(team1, team2, id, userType));
            }

        }
        catch (SQLException e) {
            LogsWriter.writeLog("getMatches: " + e.getMessage());
        }

        return matches;
    }


    synchronized public ArrayList<Match> getYesterdayMatches(String username) {

        ArrayList<Match> matches = new ArrayList<>();
        String team1;
        String team2;
        int id;
        int userType;
        int winner;
        try {
            Statement state = con.createStatement();
            Statement secondState = con.createStatement();
            ResultSet res = state.executeQuery("SELECT * FROM " + username+ " WHERE day=" + getYesterday() + " AND month=" + getYesterdayMonth());
            ResultSet winners = secondState.executeQuery("SELECT winner FROM matches WHERE day=" + getYesterday()  + " AND month=" + getYesterdayMonth());
            while (res.next() && winners.next()) {
                team1 = res.getString("team1");
                team2 = res.getString("team2");
                id = res.getInt("id");
                userType = res.getInt("type");
                winner = winners.getInt("winner");
                matches.add(new Match(team1, team2, id, userType, winner));
            }

        }
        catch (SQLException e) {
            LogsWriter.writeLog("getMatches: " + e.getMessage());
        }

        return matches;
    }

    public ArrayList<Match> parseFinishedMatchesIntoArrayOfMatches(ArrayList<FinishedMatch> finishedMatches, String username) {
        ArrayList<Match> matches = new ArrayList<>();
        for (FinishedMatch m : finishedMatches)
            matches.add(parseFinishedMatchIntoMatch(m, username));

        return matches;
    }

    synchronized public void insertTypesIntoDatabase(int[][] userTypes, String username) {

        try
        {
            Statement state = con.createStatement();

            for (int i = 0; i < userTypes.length; i++)
                state.executeUpdate("UPDATE " +username + " SET type = " + userTypes[i][TYPE_COLUMN] + " WHERE id = " + userTypes[i][ID_COLUMN]);

        }
        catch (SQLException e)
        {
            LogsWriter.writeLog("insertTypesIntoDatabase: " + e.getMessage());
        }

    }

    synchronized public void calculatePoints(String username, ArrayList<Match> finishedMatches)
    {
        if(werePointsAddedToday(username) || finishedMatches.isEmpty())
            return;
        int points = getCurrentNumberOfPoints(username);
        try
        {
            for(Match m : finishedMatches)
            {
                if(m.getUserChoice() == m.getWinner())
                    points++;
            }
            Statement state = con.createStatement();
            state.executeQuery("UPDATE users SET points = " + points + " WHERE username = \"" + username + "\"");
        }
        catch (SQLException e)
        {
            LogsWriter.writeLog("calculatePoints: " + e.getMessage());
            throw new RuntimeException(e);
        }
        catch(NullPointerException e)
        {
            LogsWriter.writeLog("calculatePoints: " + e.getMessage());
            return;
        }
        markThatPointsAddedToday(username);

    }

    synchronized private int getCurrentNumberOfPoints(String username)
    {
       int points = 0;
        try {
            Statement state =  con.createStatement();
            ResultSet res = state.executeQuery("SELECT points FROM users WHERE username = \"" + username + "\"" );
            points = res.getInt("points");
        } catch (SQLException e) {
            LogsWriter.writeLog("getCurrentNumberOfPoints: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return points;
    }

    private boolean werePointsAddedToday(String username)
    {

        Calendar now = Calendar.getInstance();
        int today = now.get(Calendar.DAY_OF_MONTH);

        try
        {
            Statement state = con.createStatement();
            ResultSet res =  state.executeQuery("SELECT dayOfLastUpdate FROM users WHERE username = \""+username+"\"");
            if(res.getInt("dayOfLastUpdate")==today)
                return true;
        }
        catch (SQLException e)
        {
            LogsWriter.writeLog("werePointsAddedToday: " + e.getMessage());
        }
        return false;
    }


    private void  markThatPointsAddedToday(String username)
    {

        Calendar now = Calendar.getInstance();
        int today = now.get(Calendar.DAY_OF_MONTH);

        try
        {
            Statement state = con.createStatement();
            state.executeQuery("UPDATE users SET dayOfLastUpdate=" + today + " WHERE username=\""+username+"\"");
        }
        catch (SQLException e)
        {
            LogsWriter.writeLog("markThatPointsAddedToday: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    synchronized public ArrayList<UserPointsPair> getRankOfPlayers()
    {
        ArrayList<UserPointsPair> rank = new ArrayList<>();
        String username;
        int points;
        try
        {
            Statement state = con.createStatement();
            ResultSet res = state.executeQuery("SELECT * FROM users ORDER BY points DESC");
            while (res.next())
            {
                username = res.getString("username");
                points = res.getInt("points");
                rank.add(new UserPointsPair(username, points));
            }
        } catch (SQLException e) {
           LogsWriter.writeLog("getRankOfPlayers: " + e.getMessage());

        }
        return rank;
    }

    synchronized private ArrayList<String> getUsers() {
        ArrayList<String> users = new ArrayList<>();

        String username;
        try {
            Statement state  = con.createStatement();
            ResultSet res = state.executeQuery("SELECT username FROM users");
            while (res.next()) {
                username = res.getString("username");
                users.add(new String(username));
            }

        } catch (SQLException e) {
            throw new Error("Problem while reaching database!");
        }

        return users;
    }

    synchronized public Match parseFinishedMatchIntoMatch(FinishedMatch finishedMatch, String username) {
        Match match;
        CharSequence winner = finishedMatch.getWinner();
        CharSequence loser = finishedMatch.getLoser();

        try {
            Statement state = con.createStatement();
            ResultSet res = state.executeQuery("SELECT * FROM " + username + " WHERE day=" + getYesterday() + " AND month=" + getYesterdayMonth());
            while (res.next())
            {
                if ((res.getString("team1").contains(winner) && res.getString("team2").contains(loser))
                        || (res.getString("team1").contains(loser) && res.getString("team2").contains(winner)))
                {
                    int id = res.getInt("id");
                    String team1 = res.getString("team1");
                    String team2 = res.getString("team2");
                    int userType = res.getInt("type");
                    int whoWon = res.getString("team1").contains(winner) ? 1 : 2;
                    match = new Match(team1, team2, id, userType, whoWon);
                    return match;
                }
            }

        } catch (SQLException e) {
            LogsWriter.writeLog("parseFinishedMatchIntoMatch: " + e.getMessage());
        }

        return null;
    }

    public static String getYesterday() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);

        String day = dateFormat.format(cal.getTime()).substring(8,10);
        day = String.valueOf(Integer.parseInt(day));
        return  day;
    }

    public static String getYesterdayMonth() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String month;
        month = String.valueOf(Calendar.MONTH+2);
        return  month;
    }
}


