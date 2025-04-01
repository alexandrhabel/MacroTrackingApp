package com.example.kaloricketabulky;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "fooditem")
public class FoodItem implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public double weight;
    public double protein;
    public double carbs;
    public double fat;
    public String dateAdded;

    public double getCalories() {
        return (protein * 4) + (carbs * 4) + (fat * 9);
    }

    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
                weight > 0 &&
                protein >= 0 &&
                carbs >= 0 &&
                fat >= 0;
    }
}