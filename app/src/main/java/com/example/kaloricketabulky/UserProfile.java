package com.example.kaloricketabulky;

public class UserProfile {
    public String name;
    public int age;
    public double weight; // kg
    public double height; // cm
    public String gender; // "male" / "female"
    public String activityLevel; // "sedentary", "light", "moderate", "active", "extreme"
    public String goal; // "lose", "maintain", "gain"
    public double targetWeight;

    public double calculateDailyCalories() {
        // Harris-Benedict equation
        double bmr;
        if (gender.equals("male")) {
            bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
        } else {
            bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
        }

        // Activity multiplier
        double activityMultiplier = 1.2;
        switch (activityLevel) {
            case "light": activityMultiplier = 1.375; break;
            case "moderate": activityMultiplier = 1.55; break;
            case "active": activityMultiplier = 1.725; break;
            case "extreme": activityMultiplier = 1.9; break;
        }

        // Goal adjustment
        double goalAdjustment = 0;
        if (goal.equals("lose")) goalAdjustment = -500;
        else if (goal.equals("gain")) goalAdjustment = 500;

        return bmr * activityMultiplier + goalAdjustment;
    }
}