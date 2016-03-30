package model;

/**
 * Created by Aleks on 23-Mar-16.
 */
public class GooglePlace {
    private String name;
    private String category;

    public GooglePlace()
    {
        this.name = "";
        this.category = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
