package com.example.pocitanikalorii;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocitanikalorii.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView tvTotalCalories, tvRemainingCalories, tvTotalProtein, tvTotalCarbs, tvTotalFat;
    private RecyclerView rvTodayMeals;
    private com.example.pocitanikalorii.FoodDatabaseHelper dbHelper;
    private com.example.pocitanikalorii.MealAdapter mealAdapter;
    private List<com.example.pocitanikalorii.Meal> mealList;
    private int dailyCalorieGoal = 2000; // Výchozí hodnota, bude se měnit podle profilu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("Počítání kalorií");
        }

        dbHelper = new com.example.pocitanikalorii.FoodDatabaseHelper(this);
        initializeViews();
        setupRecyclerView();
        updateCalorieSummary();
    }

    private void initializeViews() {
        tvTotalCalories = findViewById(R.id.tvTotalCalories);
        tvRemainingCalories = findViewById(R.id.tvRemainingCalories);
        rvTodayMeals = findViewById(R.id.rvTodayMeals);
        tvTotalProtein = findViewById(R.id.tvTotalProtein);
        tvTotalCarbs = findViewById(R.id.tvTotalCarbs);
        tvTotalFat = findViewById(R.id.tvTotalFat);
    }

    private void setupRecyclerView() {
        mealList = new ArrayList<>();
        mealAdapter = new com.example.pocitanikalorii.MealAdapter(this, mealList);
        rvTodayMeals.setLayoutManager(new LinearLayoutManager(this));
        rvTodayMeals.setAdapter(mealAdapter);

        loadTodayMeals();
    }

    private void loadTodayMeals() {
        mealList.clear();
        Cursor cursor = dbHelper.getTodayMeals();

        if (cursor.moveToFirst()) {
            do {
                com.example.pocitanikalorii.Meal meal = new com.example.pocitanikalorii.Meal(
                        cursor.getInt(0),      // id
                        cursor.getString(1),   // foodName
                        cursor.getDouble(2),   // calories
                        cursor.getDouble(3),   // protein
                        cursor.getDouble(4),   // carbs
                        cursor.getDouble(5),   // fat
                        cursor.getDouble(6),   // weight
                        cursor.getString(7),   // mealType
                        cursor.getString(8),   // date
                        cursor.getString(9)    // time
                );
                mealList.add(meal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        mealAdapter.notifyDataSetChanged();
    }

    private void updateMacroSummary() {
        double[] macros = dbHelper.getTodayMacros();
        tvTotalProtein.setText(String.format("B: %.1fg", macros[0]));
        tvTotalCarbs.setText(String.format("S: %.1fg", macros[1]));
        tvTotalFat.setText(String.format("T: %.1fg", macros[2]));
    }


    private void updateCalorieSummary() {
        int totalCalories = (int) dbHelper.getTodayTotalCalories();
        int remaining = dailyCalorieGoal - totalCalories;

        tvTotalCalories.setText(String.valueOf(totalCalories));
        tvRemainingCalories.setText(String.valueOf(remaining));

        // Změna barvy podle zůstatku kalorií
        if (remaining < 0) {
            tvRemainingCalories.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvRemainingCalories.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    public void onAddMealClick(View view) {
        startActivity(new Intent(this, com.example.pocitanikalorii.AddMealActivity.class));
    }

    public void onViewProfileClick(View view) {
        startActivity(new Intent(this, ProfileViewActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTodayMeals();
        updateCalorieSummary();
        updateMacroSummary();
        // Načtení denního cíle z profilu
        dailyCalorieGoal = getSharedPreferences("user_profile", MODE_PRIVATE)
                .getInt("daily_calorie_goal", 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_item_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        else if (id == R.id.menu_item_camera) {
            Toast.makeText(this, "Funkce kamery pro skenování potravin", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}