package maestro.model;

import java.util.ArrayList;
import java.util.HashMap;

public class RutgersCourseDatabase {

    private static final String coursesEndPoint = "https://sis.rutgers.edu/soc/api/courses.json?year=2023&term=1&campus=NB";
    private static RutgersCourseDatabase INSTANCE;

    private HashMap<String, RutgersCourse> courses;

    private RutgersCourseDatabase() {
        courses = new HashMap<>();
    }

    public static RutgersCourseDatabase getInstance() {
        if(INSTANCE == null)
            INSTANCE = new RutgersCourseDatabase();
        return INSTANCE;
    }





}
