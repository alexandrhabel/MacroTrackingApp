package com.example.kaloricketabulky;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.kaloricketabulky.AppDatabase;
import com.example.kaloricketabulky.FoodItem;
import com.example.kaloricketabulky.R;

import java.time.LocalDateTime;

public class AddFoodActivity extends AppCompatActivity {
    private EditText etName, etWeight, etProtein, etCarbs, etFat;
    private AppDatabase db;
    // ... ostatní proměnné ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        etName = findViewById(R.id.etName);
        etWeight = findViewById(R.id.etWeight);
        etProtein = findViewById(R.id.etProtein);
        etCarbs = findViewById(R.id.etCarbs);
        etFat = findViewById(R.id.etFat);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "food-db")
                .fallbackToDestructiveMigration()
                .build();

        // ... inicializace view ...

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveFood());
    }

    private void saveFood() {
        new Thread(() -> {
            try {
                FoodItem food = new FoodItem();
                food.name = etName.getText().toString();

                double weight = Double.parseDouble(etWeight.getText().toString());
                food.weight = weight;

                // Přepočet na skutečné množství
                double proteinPer100g = Double.parseDouble(etProtein.getText().toString());
                double carbsPer100g = Double.parseDouble(etCarbs.getText().toString());
                double fatPer100g = Double.parseDouble(etFat.getText().toString());

                food.protein = (proteinPer100g * weight) / 100;
                food.carbs = (carbsPer100g * weight) / 100;
                food.fat = (fatPer100g * weight) / 100;

                food.dateAdded = LocalDateTime.now().toString();

                if (!food.isValid()) {
                    throw new IllegalArgumentException("Neplatné údaje");
                }

                db.foodDao().insert(food);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Jídlo uloženo", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Chyba: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}