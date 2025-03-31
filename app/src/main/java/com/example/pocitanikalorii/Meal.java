package com.example.pocitanikalorii;

public class Meal {
    private int id;
    private String foodName;
    private double calories;
    private double protein;
    private double carbs;
    private double fat;
    private double weight;
    private String mealType;
    private String date;
    private String time;

    // Plný konstruktor
    public Meal(int id, String foodName, double calories, double protein, double carbs,
                double fat, double weight, String mealType, String date, String time) {
        this.id = id;
        this.foodName = foodName;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.weight = weight;
        this.mealType = mealType;
        this.date = date;
        this.time = time;
    }

    // Výpočet kalorií
    private double calculateCalories(double protein, double carbs, double fat) {
        return (protein * 4) + (carbs * 4) + (fat * 9);
    }

    // Gettery
    public int getId() { return id; }
    public String getFoodName() { return foodName; }
    public double getCalories() { return calories; }
    public double getProtein() { return protein; }
    public double getCarbs() { return carbs; }
    public double getFat() { return fat; }
    public double getWeight() { return weight; }
    public String getMealType() { return mealType; }
    public String getDate() { return date; }
    public String getTime() { return time; }
}