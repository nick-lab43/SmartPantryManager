package za.co.richfield.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private PantryAdapter adapter;
    private TextView empty;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(this);
        RecyclerView recycler = findViewById(R.id.recyclerPantry);
        empty = findViewById(R.id.txtEmptyPantry);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(new PantryAdapter.Listener() {
            @Override public void onEdit(PantryItem item) {
                Intent i = new Intent(MainActivity.this, AddEditIngredientActivity.class);
                i.putExtra("item_id", item.getId()); startActivity(i);
            }
            @Override public void onDelete(PantryItem item) {
                new AlertDialog.Builder(MainActivity.this).setTitle("Delete ingredient")
                        .setMessage("Remove " + item.getName() + " from your pantry?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", (d, w) -> { db.deletePantryItem(item.getId()); loadItems(); })
                        .show();
            }
        });
        recycler.setAdapter(adapter);
        findViewById(R.id.btnAddIngredient).setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));
        findViewById(R.id.btnSuggestedRecipes).setOnClickListener(v ->
                startActivity(new Intent(this, RecipesActivity.class)));
        findViewById(R.id.btnSettings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override protected void onResume() { super.onResume(); if (db != null) loadItems(); }
    private void loadItems() {
        java.util.List<PantryItem> items = db.getAllPantryItems();
        adapter.setItems(items);
        empty.setVisibility(items.isEmpty() ? TextView.VISIBLE : TextView.GONE);
    }
}
