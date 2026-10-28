package za.co.richfield.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 2;

    public DatabaseHelper(Context context) { super(context, DB_NAME, null, DB_VERSION); }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry_items (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, expiry_date TEXT)");
        db.execSQL("CREATE TABLE recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE, steps TEXT NOT NULL)");
        db.execSQL("CREATE TABLE recipe_ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER NOT NULL, name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");
        seedRecipes(db);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade older pantry-only databases without deleting existing pantry records.
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE, steps TEXT NOT NULL)");
            db.execSQL("CREATE TABLE IF NOT EXISTS recipe_ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER NOT NULL, name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");
            seedRecipes(db);
        }
    }

    public long insertPantryItem(String name, double quantity, String unit, String expiry) {
        ContentValues v = new ContentValues();
        v.put("name", name.trim()); v.put("quantity", quantity); v.put("unit", unit.trim());
        v.put("expiry_date", expiry == null ? "" : expiry.trim());
        return getWritableDatabase().insert("pantry_items", null, v);
    }

    public int updatePantryItem(long id, String name, double quantity, String unit, String expiry) {
        ContentValues v = new ContentValues();
        v.put("name", name.trim()); v.put("quantity", quantity); v.put("unit", unit.trim());
        v.put("expiry_date", expiry == null ? "" : expiry.trim());
        return getWritableDatabase().update("pantry_items", v, "id=?", new String[]{String.valueOf(id)});
    }

    public int deletePantryItem(long id) {
        return getWritableDatabase().delete("pantry_items", "id=?", new String[]{String.valueOf(id)});
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT id,name,quantity,unit,expiry_date FROM pantry_items ORDER BY name COLLATE NOCASE", null)) {
            while (c.moveToNext()) list.add(new PantryItem(c.getLong(0), c.getString(1),
                    c.getDouble(2), c.getString(3), c.getString(4)));
        }
        return list;
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().rawQuery("SELECT id,name,steps FROM recipes ORDER BY name", null)) {
            while (c.moveToNext()) list.add(new Recipe(c.getLong(0), c.getString(1), c.getString(2)));
        }
        return list;
    }

    public Recipe getRecipe(long id) {
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT id,name,steps FROM recipes WHERE id=?", new String[]{String.valueOf(id)})) {
            if (c.moveToFirst()) return new Recipe(c.getLong(0), c.getString(1), c.getString(2));
        }
        return null;
    }

    public List<RecipeIngredient> getRecipeIngredients(long recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT name,quantity,unit FROM recipe_ingredients WHERE recipe_id=? ORDER BY id",
                new String[]{String.valueOf(recipeId)})) {
            while (c.moveToNext()) list.add(new RecipeIngredient(c.getString(0), c.getDouble(1), c.getString(2)));
        }
        return list;
    }

    public List<Recipe> getSuggestedRecipes() {
        List<Recipe> result = new ArrayList<>();
        List<PantryItem> pantry = getAllPantryItems();
        for (Recipe recipe : getAllRecipes()) {
            if (IngredientMatcher.canMake(recipe, getRecipeIngredients(recipe.getId()), pantry)) result.add(recipe);
        }
        return result;
    }

    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Tomato and Onion Salad", "1. Wash the tomatoes. 2. Chop the tomatoes and onion. 3. Mix and serve.",
                new String[][]{{"tomato","2","pcs"},{"onion","1","pcs"}});
        addRecipe(db, "Tomato Omelette", "1. Beat the eggs. 2. Chop tomato. 3. Cook together in a lightly oiled pan.",
                new String[][]{{"egg","2","pcs"},{"tomato","1","pcs"}});
        addRecipe(db, "Scrambled Eggs", "1. Beat eggs. 2. Cook gently in a pan while stirring until set.",
                new String[][]{{"egg","2","pcs"}});
        addRecipe(db, "Garlic Fried Rice", "1. Heat a little oil. 2. Add garlic and cooked rice. 3. Stir-fry until hot.",
                new String[][]{{"rice","200","g"},{"garlic","1","pcs"}});
        addRecipe(db, "Vegetable Fried Rice", "1. Dice carrot and onion. 2. Fry vegetables. 3. Add cooked rice and stir.",
                new String[][]{{"rice","200","g"},{"carrot","1","pcs"},{"onion","1","pcs"}});
        addRecipe(db, "Mashed Potatoes", "1. Peel and boil potatoes until soft. 2. Mash and season.",
                new String[][]{{"potato","300","g"}});
        addRecipe(db, "Potato and Onion Fry", "1. Slice potato and onion. 2. Fry until tender and golden.",
                new String[][]{{"potato","250","g"},{"onion","1","pcs"}});
        addRecipe(db, "Carrot Soup", "1. Chop carrots and onion. 2. Simmer in water until soft. 3. Blend or mash.",
                new String[][]{{"carrot","2","pcs"},{"onion","1","pcs"}});
        addRecipe(db, "Tomato Soup", "1. Chop tomatoes and onion. 2. Simmer with water until soft. 3. Blend or mash.",
                new String[][]{{"tomato","3","pcs"},{"onion","1","pcs"}});
        addRecipe(db, "Egg Fried Rice", "1. Scramble egg in a pan. 2. Add cooked rice. 3. Stir-fry until hot.",
                new String[][]{{"egg","1","pcs"},{"rice","200","g"}});
        addRecipe(db, "Garlic Potatoes", "1. Dice potatoes. 2. Fry or roast with crushed garlic until cooked.",
                new String[][]{{"potato","250","g"},{"garlic","1","pcs"}});
        addRecipe(db, "Onion Omelette", "1. Beat eggs. 2. Chop onion. 3. Cook together until the egg is set.",
                new String[][]{{"egg","2","pcs"},{"onion","1","pcs"}});
        addRecipe(db, "Carrot and Potato Mash", "1. Boil carrot and potato until soft. 2. Drain and mash.",
                new String[][]{{"carrot","1","pcs"},{"potato","200","g"}});
        addRecipe(db, "Tomato Rice", "1. Cook rice. 2. Chop and cook tomato. 3. Mix and heat through.",
                new String[][]{{"rice","200","g"},{"tomato","2","pcs"}});
        addRecipe(db, "Simple Vegetable Soup", "1. Chop carrot, potato and onion. 2. Simmer in water until tender.",
                new String[][]{{"carrot","1","pcs"},{"potato","150","g"},{"onion","1","pcs"}});
        addRecipe(db, "Tomato Garlic Pasta", "1. Cook pasta. 2. Cook chopped tomato and garlic. 3. Combine.",
                new String[][]{{"pasta","200","g"},{"tomato","2","pcs"},{"garlic","1","pcs"}});
        addRecipe(db, "Cheese Omelette", "1. Beat eggs. 2. Cook in a pan. 3. Add cheese and fold.",
                new String[][]{{"egg","2","pcs"},{"cheese","30","g"}});
        addRecipe(db, "Carrot Salad", "1. Wash and grate carrots. 2. Add a little lemon juice if available; serve plain otherwise.",
                new String[][]{{"carrot","2","pcs"}});
        addRecipe(db, "Potato and Carrot Roast", "1. Dice potato and carrot. 2. Roast or pan-cook until tender.",
                new String[][]{{"potato","250","g"},{"carrot","2","pcs"}});
        addRecipe(db, "Tomato Egg Rice", "1. Cook rice. 2. Scramble egg and cook tomato. 3. Combine and heat.",
                new String[][]{{"rice","200","g"},{"egg","1","pcs"},{"tomato","1","pcs"}});
    }

    private void addRecipe(SQLiteDatabase db, String name, String steps, String[][] ingredients) {
        ContentValues rv = new ContentValues();
        rv.put("name", name); rv.put("steps", steps);
        long recipeId = db.insertWithOnConflict("recipes", null, rv, SQLiteDatabase.CONFLICT_IGNORE);
        if (recipeId == -1) {
            try (Cursor c = db.rawQuery("SELECT id FROM recipes WHERE name=?", new String[]{name})) {
                if (c.moveToFirst()) recipeId = c.getLong(0);
            }
        }
        for (String[] ingredient : ingredients) {
            ContentValues iv = new ContentValues();
            iv.put("recipe_id", recipeId); iv.put("name", ingredient[0]);
            iv.put("quantity", Double.parseDouble(ingredient[1])); iv.put("unit", ingredient[2]);
            db.insert("recipe_ingredients", null, iv);
        }
    }
}
