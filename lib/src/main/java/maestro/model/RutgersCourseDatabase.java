package maestro.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

public class RutgersCourseDatabase {

    private static final String coursesEndPoint = "https://sis.rutgers.edu/soc/api/courses.json?year=2023&term=1&campus=NB";
    public static final double LOAD_FAILED = -1;
    private static RutgersCourseDatabase INSTANCE;

    private final HashMap<String, RutgersSection> sectionsMap;

    private RutgersCourseDatabase() {
        sectionsMap = new HashMap<>();
    }

    public static RutgersCourseDatabase getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new RutgersCourseDatabase();
            INSTANCE.loadFromEndpoint();
        }
        return INSTANCE;
    }

    public RutgersSection getSection(String index) {
        return sectionsMap.get(index);
    }

    /**
     *
     * @return the time taken to load from API into database
     */
    public double loadFromEndpoint() {
        long start = System.currentTimeMillis();

        JSONArray raw = retrieve();
        if(raw == null) {
            System.out.println("could not retrieve courses from endpoint");
            return LOAD_FAILED;
        }
        System.out.println("successfully retrieved courses");

        if(!build(raw)) {
            System.out.println("could not build database from retrieved JSON");
            return LOAD_FAILED;
        }

        double time = (System.currentTimeMillis() - start)/1000.0;
        System.out.println("successfully built database in " + time + " seconds");

        return time;
    }

    /**
     * Makes a connection to the Rutgers SOC API and attempts to parse the data into a JSON Array.
     * @return the parsed JSON array from the API call
     */
    public JSONArray retrieve() {
        try{
            URL request = new URL(coursesEndPoint);
            HttpURLConnection con = (HttpURLConnection) request.openConnection();

            Scanner read = new Scanner(new GZIPInputStream(con.getInputStream()));

            String inline = "";
            while(read.hasNext()) {
                inline += read.nextLine();
            }
            read.close();

            JSONParser parse = new JSONParser();
            return (JSONArray) parse.parse(inline);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean build(JSONArray arr) {
        for(Object obj : arr) {
            JSONObject course = (JSONObject)obj;

            String title = (String)course.get("title");
            for(Object obj2 : (JSONArray)course.get("sections")) {
                JSONObject section = (JSONObject)obj2;

                String sectionNo = (String)section.get("number");
                String index = (String)section.get("index");
                RutgersSection rs = new RutgersSection(title, sectionNo, index);

                sectionsMap.put(index, rs);
            }
        }

        return true;
    }

    public static void main(String[] args) {
        RutgersCourseDatabase rcdb = new RutgersCourseDatabase();
        System.out.println(rcdb.loadFromEndpoint() + " seconds");
    }

}
