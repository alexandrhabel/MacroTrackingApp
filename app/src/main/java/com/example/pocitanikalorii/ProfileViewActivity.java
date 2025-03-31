package com.example.pocitanikalorii;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class ProfileViewActivity extends AppCompatActivity {

    private TextView tvName, tvAge, tvGender, tvWeight, tvHeight, tvActivity, tvCalorieGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        // Inicializace view
        tvName = findViewById(R.id.tvName);
        tvAge = findViewById(R.id.tvAge);
        tvGender = findViewById(R.id.tvGender);
        tvWeight = findViewById(R.id.tvWeight);
        tvHeight = findViewById(R.id.tvHeight);
        tvActivity = findViewById(R.id.tvActivity);
        tvCalorieGoal = findViewById(R.id.tvCalorieGoal);

        // Načtení dat
        loadProfileData();

        // Nastavení tlačítka
        Button btnEdit = findViewById(R.id.btnEditProfile);
        btnEdit.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
    }

    private void loadProfileData() {
        SharedPreferences prefs = getSharedPreferences("user_profile", MODE_PRIVATE);

        // Vždy používejte výchozí hodnoty
        tvName.setText("Jméno: " + prefs.getString("name", "Není nastaveno"));
        tvAge.setText("Věk: " + prefs.getInt("age", 0));
        tvGender.setText("Pohlaví: " + (prefs.getString("gender", "male").equals("male") ? "Muž" : "Žena"));
        tvWeight.setText("Váha: " + prefs.getFloat("weight", 0) + " kg");
        tvHeight.setText("Výška: " + prefs.getFloat("height", 0) + " cm");

        String activity = "Střední";
        switch(prefs.getInt("activity_level", 2)) {
            case 1: activity = "Nízká"; break;
            case 3: activity = "Vysoká"; break;
        }
        tvActivity.setText("Aktivita: " + activity);

        tvCalorieGoal.setText(prefs.getInt("daily_calorie_goal", 2000) + " kcal");
    }
}