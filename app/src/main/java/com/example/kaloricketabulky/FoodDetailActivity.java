package com.example.kaloricketabulky;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.kaloricketabulky.AppDatabase;
import com.example.kaloricketabulky.FoodItem;
import com.example.kaloricketabulky.R;

public class FoodDetailActivity extends AppCompatActivity {
    private AppDatabase db;
    private FoodItem currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Skrytí ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_food_detail);

        // Inicializace databáze
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "food-db")
                .fallbackToDestructiveMigration()
                .build();

        // Získání potraviny z intentu
        currentFood = (FoodItem) getIntent().getSerializableExtra("food_item");

        // Nastavení hodnot
        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvCalories = findViewById(R.id.tvDetailCalories);
        TextView tvProtein = findViewById(R.id.tvDetailProtein);
        TextView tvCarbs = findViewById(R.id.tvDetailCarbs);
        TextView tvFat = findViewById(R.id.tvDetailFat);

        tvName.setText(currentFood.name);
        tvCalories.setText(String.format("%.0f kcal", currentFood.getCalories()));
        tvProtein.setText(String.format("Bílkoviny: %.1fg", currentFood.protein));
        tvCarbs.setText(String.format("Sacharidy: %.1fg", currentFood.carbs));
        tvFat.setText(String.format("Tuky: %.1fg", currentFood.fat));

        // Nastavení tlačítka pro smazání
        Button btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> deleteFoodItem());
    }

    private void deleteFoodItem() {
        new Thread(() -> {
            db.foodDao().delete(currentFood);
            runOnUiThread(() -> {
                Toast.makeText(this, "Položka smazána", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK); // DŮLEŽITÉ - přidáno
                finish(); // Zavře aktivitu po smazání
            });
        }).start();
    }
}