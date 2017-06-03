package ResultsParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by Użytkownik on 07.02.2017.
 */
abstract public class HTMLparser {
    public  Document getHTMLcode(String website) {
        Document document;
        final int MAX_ATTEMPTS = 5;
        int tries = 0;
        while (true) {
            try {
                document = Jsoup.connect(website).get();
                return document;
            } catch (IOException e) {
                if(tries>MAX_ATTEMPTS)
                    throw new RuntimeException("Connection with results not possible");
            }
        }

    }
}
