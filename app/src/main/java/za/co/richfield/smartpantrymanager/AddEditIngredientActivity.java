package za.co.richfield.smartpantrymanager;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditIngredientActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private EditText name, quantity, unit, expiry;
    private long itemId = -1;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);
        db = new DatabaseHelper(this);
        name = findViewById(R.id.editName); quantity = findViewById(R.id.editQuantity);
        unit = findViewById(R.id.editUnit); expiry = findViewById(R.id.editExpiry);
        TextView title = findViewById(R.id.txtFormTitle);
        itemId = getIntent().getLongExtra("item_id", -1);
        if (itemId != -1) {
            title.setText("Edit ingredient");
            for (PantryItem item : db.getAllPantryItems()) {
                if (item.getId() == itemId) {
                    name.setText(item.getName()); quantity.setText(String.valueOf(item.getQuantity()));
                    unit.setText(item.getUnit()); expiry.setText(item.getExpiryDate()); break;
                }
            }
        }
        findViewById(R.id.btnSaveIngredient).setOnClickListener(v -> save());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
    }

    private void save() {
        String n = name.getText().toString().trim();
        String q = quantity.getText().toString().trim();
        String u = unit.getText().toString().trim();
        String e = expiry.getText().toString().trim();
        if (n.isEmpty()) { name.setError("Enter an ingredient name"); name.requestFocus(); return; }
        if (q.isEmpty()) { quantity.setError("Enter a quantity"); quantity.requestFocus(); return; }
        if (u.isEmpty()) { unit.setError("Enter a unit (pcs, g, ml, etc.)"); unit.requestFocus(); return; }
        double amount;
        try { amount = Double.parseDouble(q); }
        catch (NumberFormatException ex) { quantity.setError("Enter a valid number"); return; }
        if (amount <= 0 || Double.isNaN(amount) || Double.isInfinite(amount)) {
            quantity.setError("Quantity must be greater than zero"); return;
        }
        if (!e.isEmpty() && !e.matches("\\d{4}-\\d{2}-\\d{2}")) {
            expiry.setError("Use YYYY-MM-DD or leave blank"); return;
        }
        if (itemId == -1) db.insertPantryItem(n, amount, u, e);
        else db.updatePantryItem(itemId, n, amount, u, e);
        Toast.makeText(this, "Ingredient saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
