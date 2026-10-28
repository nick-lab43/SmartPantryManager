package za.co.richfield.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        DatabaseHelper db = new DatabaseHelper(this);
        long id = getIntent().getLongExtra("recipe_id", -1);
        Recipe recipe = db.getRecipe(id);
        TextView title = findViewById(R.id.txtDetailName);
        TextView ingredients = findViewById(R.id.txtDetailIngredients);
        TextView steps = findViewById(R.id.txtDetailSteps);
        if (recipe == null) {
            title.setText("Recipe not found");
            ingredients.setText("");
            steps.setText("Return to suggestions and select a recipe again.");
        } else {
            title.setText(recipe.getName());
            StringBuilder b = new StringBuilder();
            List<RecipeIngredient> list = db.getRecipeIngredients(id);
            for (RecipeIngredient item : list) {
                b.append("• ").append(item.getQuantity()).append(" ").append(item.getUnit())
                        .append(" ").append(item.getName()).append("\n");
            }
            ingredients.setText(b.toString());
            steps.setText(recipe.getSteps());
        }
        findViewById(R.id.btnDetailBack).setOnClickListener(v -> finish());
    }
}
