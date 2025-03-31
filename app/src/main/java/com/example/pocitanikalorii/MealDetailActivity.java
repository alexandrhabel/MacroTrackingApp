package com.example.pocitanikalorii;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pocitanikalorii.FoodDatabaseHelper;
import com.example.pocitanikalorii.Meal;
import com.example.pocitanikalorii.R;

public class MealDetailActivity extends AppCompatActivity {
    private TextView tvFoodName, tvCalories, tvWeight, tvMealType, tvTime, tvProtein, tvCarbs, tvFat;
    private Button btnDelete;
    private FoodDatabaseHelper dbHelper;
    private int mealId;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mealId = getIntent().getIntExtra("meal_id", -1);
        dbHelper = new FoodDatabaseHelper(this);

        initializeViews();
        loadMealData();
        setupDeleteButton();
    }

    private void initializeViews() {
        tvFoodName = findViewById(R.id.tvFoodName);
        tvCalories = findViewById(R.id.tvCalories);
        tvWeight = findViewById(R.id.tvWeight);
        tvMealType = findViewById(R.id.tvMealType);
        tvTime = findViewById(R.id.tvTime);
        btnDelete = findViewById(R.id.btnDelete);
    }

    private void loadMealData() {
        Meal meal = dbHelper.getMealById(mealId);
        if (meal != null) {
            tvFoodName.setText(meal.getFoodName());
            tvCalories.setText(String.format("Kalorie: %.1f kcal", meal.getCalories()));
            tvProtein.setText(String.format("Bílkoviny: %.1f g", meal.getProtein()));
            tvCarbs.setText(String.format("Sacharidy: %.1f g", meal.getCarbs()));
            tvFat.setText(String.format("Tuky: %.1f g", meal.getFat()));
            tvWeight.setText(String.format("Hmotnost: %.1f g", meal.getWeight()));
        }
    }

    private void setupDeleteButton() {
        btnDelete.setOnClickListener(v -> {
            dbHelper.deleteMeal(mealId);
            Toast.makeText(this, "Jídlo smazáno", Toast.LENGTH_SHORT).show();
            finish(); // Zavře detail a vrátí se na seznam
        });
    }
}