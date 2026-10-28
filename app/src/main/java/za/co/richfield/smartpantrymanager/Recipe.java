package za.co.richfield.smartpantrymanager;

public class Recipe {
    private final long id;
    private final String name;
    private final String steps;

    public Recipe(long id, String name, String steps) {
        this.id = id;
        this.name = name;
        this.steps = steps;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getSteps() { return steps; }
    public String getDescription() { return "Open to view ingredients and preparation steps."; }
}
