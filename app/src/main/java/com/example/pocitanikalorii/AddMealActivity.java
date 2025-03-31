package com.example.pocitanikalorii;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddMealActivity extends AppCompatActivity {
    private EditText etFoodName, etProtein, etCarbs, etFat, etWeight;
    private Spinner spinnerMealType;
    private FoodDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        dbHelper = new FoodDatabaseHelper(this);
        initializeViews();
        setupSpinner();
        setupSaveButton();
    }

    private void initializeViews() {
        etFoodName = findViewById(R.id.etFoodName);
        etProtein = findViewById(R.id.etProtein);
        etCarbs = findViewById(R.id.etCarbs);
        etFat = findViewById(R.id.etFat);
        etWeight = findViewById(R.id.etWeight);
        spinnerMealType = findViewById(R.id.spinnerMealType);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.meal_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMealType.setAdapter(adapter);
    }

    private void setupSaveButton() {
        Button btnSave = findViewById(R.id.btnSaveMeal);
        btnSave.setOnClickListener(v -> saveMeal());
    }

    private void saveMeal() {
        try {
            String foodName = etFoodName.getText().toString();
            double protein = Double.parseDouble(etProtein.getText().toString());
            double carbs = Double.parseDouble(etCarbs.getText().toString());
            double fat = Double.parseDouble(etFat.getText().toString());
            double weight = Double.parseDouble(etWeight.getText().toString());
            String mealType = spinnerMealType.getSelectedItem().toString();

            long result = dbHelper.addMeal(foodName, protein, carbs, fat, weight, mealType);

            if (result != -1) {
                Toast.makeText(this, "Jídlo úspěšně přidáno", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Chyba při ukládání", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Zadejte platné hodnoty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}