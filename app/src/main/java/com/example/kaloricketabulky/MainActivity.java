package com.example.kaloricketabulky;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private RecyclerView recyclerView;
    private TextView tvCaloriesSum, tvProteinSum, tvCarbsSum, tvFatSum, tvCaloriesGoal;
    private ProgressBar progressCalories;
    private double dailyCalorieGoal = 2000;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        initViews();
        initDatabase();
        loadInitialData();
    }

    private void initViews() {
        tvCaloriesSum = findViewById(R.id.tvCaloriesSum);
        tvProteinSum = findViewById(R.id.tvProteinSum);
        tvCarbsSum = findViewById(R.id.tvCarbsSum);
        tvFatSum = findViewById(R.id.tvFatSum);
        tvCaloriesGoal = findViewById(R.id.tvCaloriesGoal);
        progressCalories = findViewById(R.id.progressCalories);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddFoodActivity.class)));
    }

    private void initDatabase() {
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "food-db")
                .fallbackToDestructiveMigration()
                .build();
    }

    private void loadInitialData() {
        loadCalorieGoal(); // Na hlavním vlákně, protože jde o SharedPreferences
        loadFoodItems();   // Na pozadí, protože pracuje s databází
    }

    private void loadCalorieGoal() {
        SharedPreferences prefs = getSharedPreferences("user_profile", MODE_PRIVATE);
        dailyCalorieGoal = prefs.getFloat("daily_calories", 2000);
    }

    private void loadFoodItems() {
        executor.execute(() -> {
            try {
                List<FoodItem> todayItems = db.foodDao().getTodayItems(getTodayDate());
                NutritionTotals totals = calculateTotals(todayItems);

                mainHandler.post(() -> {
                    updateNutritionViews(totals);
                    updateRecyclerView(todayItems);
                    updateCalorieProgress(totals.calories);
                });
            } catch (Exception e) {
                mainHandler.post(() ->
                        Toast.makeText(this, "Chyba při načítání dat", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateNutritionViews(NutritionTotals totals) {
        tvCaloriesSum.setText(String.format(Locale.getDefault(), "%.0f kcal", totals.calories));
        tvProteinSum.setText(String.format(Locale.getDefault(), "%.0fg bílk.", totals.protein));
        tvCarbsSum.setText(String.format(Locale.getDefault(), "%.0fg sach.", totals.carbs));
        tvFatSum.setText(String.format(Locale.getDefault(), "%.0fg tuků", totals.fat));
    }

    private void updateRecyclerView(List<FoodItem> items) {
        FoodAdapter adapter = new FoodAdapter(items);
        recyclerView.setAdapter(adapter);
    }

    private void updateCalorieProgress(double consumedCalories) {
        int progress = (int) ((consumedCalories / dailyCalorieGoal) * 100);
        progressCalories.setProgress(Math.min(progress, 100));

        String progressText = String.format(Locale.getDefault(),
                "%.0f/%.0f kcal", consumedCalories, dailyCalorieGoal);
        tvCaloriesGoal.setText(progressText);

        if (consumedCalories > dailyCalorieGoal) {
            tvCaloriesGoal.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else {
            tvCaloriesGoal.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        }
    }

    private NutritionTotals calculateTotals(List<FoodItem> items) {
        NutritionTotals totals = new NutritionTotals();
        for (FoodItem item : items) {
            totals.calories += item.getCalories();
            totals.protein += item.protein;
            totals.carbs += item.carbs;
            totals.fat += item.fat;
        }
        return totals;
    }

    private String getTodayDate() {
        return LocalDate.now().toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInitialData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    private static class NutritionTotals {
        double calories, protein, carbs, fat;
    }
}