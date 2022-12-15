package maestro.model;

import java.io.Serializable;

public class RutgersSection implements Serializable {

    private final String index;
    private final String title;
    private final String section;

    public RutgersSection(String title, String section, String index) {
        this.title = title;
        this.section = section;
        this.index = index;
    }

    public String getIndex() {
        return index;
    }

    public String getTitle() {
        return title;
    }

    public String getSection() {
        return section;
    }
}
