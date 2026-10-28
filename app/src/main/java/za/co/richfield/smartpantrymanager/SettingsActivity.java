package za.co.richfield.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private TextView summary;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        db = new DatabaseHelper(this);
        summary = findViewById(R.id.txtSettingsSummary);
        CheckBox alerts = findViewById(R.id.checkExpiryAlerts);
        SharedPreferences prefs = getSharedPreferences("pantry_settings", MODE_PRIVATE);
        alerts.setChecked(prefs.getBoolean("expiry_alerts", true));
        alerts.setOnCheckedChangeListener((button, checked) ->
                prefs.edit().putBoolean("expiry_alerts", checked).apply());
        findViewById(R.id.btnSettingsBack).setOnClickListener(v -> finish());
        updateSummary();
    }
    @Override protected void onResume() { super.onResume(); if (db != null && summary != null) updateSummary(); }
    private void updateSummary() {
        summary.setText("Pantry items saved: " + db.getAllPantryItems().size()
                + "\nRecipe collection: " + db.getAllRecipes().size() + " seeded recipes"
                + "\nStorage: local SQLite database on this device.");
    }
}
