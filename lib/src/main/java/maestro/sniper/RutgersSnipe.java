package maestro.sniper;

import org.json.simple.JSONArray;

import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

public class RutgersSnipe extends Snipe {

    private static final String openSections = "https://sis.rutgers.edu/soc/api/openSections.json?year=2023&term=1&campus=NB";
    private static final String register = "https://sims.rutgers.edu/webreg/editSchedule.htm?login=cas&semesterSelection=12023&indexList=%s";

    private String index;

    public RutgersSnipe(String url) {
        this.index = url;
        this.url = String.format(register, index);
        this.itemName = parseItemName();
        this.users = new ArrayList<>();
        this.urlType = URLType.RUTGERS;
    }

    @Override
    public boolean inStock() {
        try {
            URL request = new URL(openSections);
            HttpURLConnection con = (HttpURLConnection) request.openConnection();

            Scanner read = new Scanner(new GZIPInputStream(con.getInputStream()));

            String inline = "";
            while(read.hasNext()) {
                inline += read.nextLine();
            }
            read.close();

            JSONParser parse = new JSONParser();
            JSONArray data = (JSONArray) parse.parse(inline);

            return data.contains(index);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String parseItemName() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof RutgersSnipe)) {
            return false;
        }

        return this.index.equals(((RutgersSnipe)obj).index);
    }

    @Override
    public int hashCode() {
        return index.hashCode();
    }

    public static void main(String[] args) {
        RutgersSnipe rutgersSnipe = new RutgersSnipe("02962");

    }
}
