package za.co.richfield.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecipesActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private RecipeAdapter adapter;
    private TextView empty;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        db = new DatabaseHelper(this);
        empty = findViewById(R.id.txtNoRecipes);
        RecyclerView recycler = findViewById(R.id.recyclerRecipes);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(recipe -> {
            Intent i = new Intent(this, RecipeDetailActivity.class);
            i.putExtra("recipe_id", recipe.getId()); startActivity(i);
        });
        recycler.setAdapter(adapter);
        findViewById(R.id.btnBackPantry).setOnClickListener(v -> finish());
    }
    @Override protected void onResume() {
        super.onResume();
        if (db != null) {
            java.util.List<Recipe> matches = db.getSuggestedRecipes();
            adapter.setRecipes(matches);
            empty.setVisibility(matches.isEmpty() ? TextView.VISIBLE : TextView.GONE);
        }
    }
}
