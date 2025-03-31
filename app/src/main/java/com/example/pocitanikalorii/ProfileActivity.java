package com.example.pocitanikalorii;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

public class ProfileActivity extends AppCompatActivity {

    // Deklarace všech View komponent
    private TextView tvName, tvAge, tvGender, tvWeight, tvHeight, tvActivity, tvCalorieGoal;
    private Button btnEditProfile, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences prefs = getSharedPreferences("user_profile", MODE_PRIVATE);

        if (prefs.contains("name")) {
            // Přehledový režim
            setContentView(R.layout.activity_profile_view);
            setupProfileView();
        } else {
            // Editační režim
            setContentView(R.layout.activity_profile_edit);
            setupProfileEdit();
        }
    }

    private void setupProfileView() {
        // Inicializace View pro přehledový režim
        tvName = findViewById(R.id.tvName);
        tvAge = findViewById(R.id.tvAge);
        tvGender = findViewById(R.id.tvGender);
        tvWeight = findViewById(R.id.tvWeight);
        tvHeight = findViewById(R.id.tvHeight);
        tvActivity = findViewById(R.id.tvActivity);
        tvCalorieGoal = findViewById(R.id.tvCalorieGoal);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        loadProfileData();

        btnEditProfile.setOnClickListener(v -> switchToEditMode());
    }

    private void setupProfileEdit() {
        // Inicializace View pro editační režim
        btnSave = findViewById(R.id.btnSave);
        Button btnBack = findViewById(R.id.btnBack);

        btnSave.setOnClickListener(v -> {
            saveProfileData();
            switchToViewMode();
        });

        btnBack.setOnClickListener(v -> switchToViewMode());

        // Povolení tlačítka zpět v ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadProfileData() {
        SharedPreferences prefs = getSharedPreferences("user_profile", MODE_PRIVATE);

        tvName.setText("Jméno: " + prefs.getString("name", ""));
        tvAge.setText("Věk: " + prefs.getInt("age", 0));
        tvGender.setText("Pohlaví: " + (prefs.getString("gender", "male").equals("male") ? "Muž" : "Žena"));
        tvWeight.setText("Váha: " + prefs.getFloat("weight", 0) + " kg");
        tvHeight.setText("Výška: " + prefs.getFloat("height", 0) + " cm");

        // Opravený switch blok se středníkem na konci
        switch(prefs.getInt("activity_level", 2)) {
            case 1:
                tvActivity.setText("Aktivita: Nízká");
                break;
            case 3:
                tvActivity.setText("Aktivita: Vysoká");
                break;
            default:
                tvActivity.setText("Aktivita: Střední");
        }

        tvCalorieGoal.setText(prefs.getInt("daily_calorie_goal", 0) + " kcal");
    }

    private void saveProfileData() {
        // Zde doplňte váš existující kód pro ukládání
    }

    private void switchToEditMode() {
        setContentView(R.layout.activity_profile_edit);
        setupProfileEdit();
    }

    private void switchToViewMode() {
        setContentView(R.layout.activity_profile_view);
        setupProfileView();
    }
}