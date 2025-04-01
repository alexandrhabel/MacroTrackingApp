package com.example.kaloricketabulky;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private EditText etName, etAge, etWeight, etHeight, etTargetWeight, etCalorieGoal;
    private RadioGroup rgGender;
    private Spinner spinnerActivity, spinnerGoal;
    private TextView tvDailyCalories;
    private Button btnCalculate, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inicializace view komponent
        initViews();

        // Načtení existujícího profilu
        UserProfile profile = loadProfile();
        if (profile != null) {
            fillFormWithProfile(profile);
        }

        // Nastavení listenerů
        btnCalculate.setOnClickListener(v -> calculateAndShowCalories());
        btnSave.setOnClickListener(v -> saveProfile());
    }



    private void initViews() {
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etTargetWeight = findViewById(R.id.etTargetWeight);
        rgGender = findViewById(R.id.rgGender);
        spinnerActivity = findViewById(R.id.spinnerActivity);
        spinnerGoal = findViewById(R.id.spinnerGoal);
        tvDailyCalories = findViewById(R.id.tvDailyCalories);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnSave = findViewById(R.id.btnSave);
    }

    private void fillFormWithProfile(UserProfile profile) {
        etName.setText(profile.name);
        etAge.setText(String.valueOf(profile.age));
        etWeight.setText(String.valueOf(profile.weight));
        etHeight.setText(String.valueOf(profile.height));
        etTargetWeight.setText(String.valueOf(profile.targetWeight));

        // Nastavení pohlaví
        if (profile.gender.equals("male")) {
            rgGender.check(R.id.rbMale);
        } else {
            rgGender.check(R.id.rbFemale);
        }

        // Nastavení spinnerů
        setSpinnerSelection(spinnerActivity, profile.activityLevel, R.array.activity_levels_values);
        setSpinnerSelection(spinnerGoal, profile.goal, R.array.goal_types_values);
    }

    private void setSpinnerSelection(Spinner spinner, String value, int arrayRes) {
        String[] values = getResources().getStringArray(arrayRes);
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void calculateAndShowCalories() {
        try {
            UserProfile profile = createProfileFromInput();
            double calories = profile.calculateDailyCalories();
            tvDailyCalories.setText(String.format("Doporučený denní příjem: %.0f kcal", calories));
        } catch (Exception e) {
            Toast.makeText(this, "Vyplňte všechny údaje správně", Toast.LENGTH_SHORT).show();
        }
    }

    private UserProfile createProfileFromInput() throws Exception {
        UserProfile profile = new UserProfile();

        profile.name = etName.getText().toString();
        if (profile.name.isEmpty()) throw new Exception();

        profile.age = Integer.parseInt(etAge.getText().toString());
        profile.weight = Double.parseDouble(etWeight.getText().toString());
        profile.height = Double.parseDouble(etHeight.getText().toString());
        profile.targetWeight = Double.parseDouble(etTargetWeight.getText().toString());

        // Pohlaví
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        if (selectedGenderId == R.id.rbMale) {
            profile.gender = "male";
        } else if (selectedGenderId == R.id.rbFemale) {
            profile.gender = "female";
        } else {
            throw new Exception();
        }

        // Aktivita
        profile.activityLevel = getResources()
                .getStringArray(R.array.activity_levels_values)[spinnerActivity.getSelectedItemPosition()];

        // Cíl
        profile.goal = getResources()
                .getStringArray(R.array.goal_types_values)[spinnerGoal.getSelectedItemPosition()];

        return profile;
    }

    private void saveProfile() {
        try {
            UserProfile profile = createProfileFromInput();
            saveProfileToPreferences(profile);
            Toast.makeText(this, "Profil uložen", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Vyplňte všechny údaje správně", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfileToPreferences(UserProfile profile) {
        SharedPreferences prefs = getSharedPreferences("user_profile", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("name", profile.name);
        editor.putInt("age", profile.age);
        editor.putFloat("weight", (float) profile.weight);
        editor.putFloat("height", (float) profile.height);
        editor.putFloat("targetWeight", (float) profile.targetWeight);
        editor.putString("gender", profile.gender);
        editor.putString("activityLevel", profile.activityLevel);
        editor.putString("goal", profile.goal);
        editor.putFloat("daily_calories", (float) profile.calculateDailyCalories());

        editor.apply();
    }

    private UserProfile loadProfile() {
        SharedPreferences prefs = getSharedPreferences("user_profile", MODE_PRIVATE);
        if (!prefs.contains("name")) {
            return null;
        }

        UserProfile profile = new UserProfile();
        profile.name = prefs.getString("name", "");
        profile.age = prefs.getInt("age", 0);
        profile.weight = prefs.getFloat("weight", 0);
        profile.height = prefs.getFloat("height", 0);
        profile.targetWeight = prefs.getFloat("targetWeight", 0);
        profile.gender = prefs.getString("gender", "male");
        profile.activityLevel = prefs.getString("activityLevel", "moderate");
        profile.goal = prefs.getString("goal", "maintain");

        return profile;
    }


}