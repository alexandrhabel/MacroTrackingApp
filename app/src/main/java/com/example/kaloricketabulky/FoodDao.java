package com.example.kaloricketabulky;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FoodDao {
    @Insert
    void insert(FoodItem food);

    @Query("SELECT * FROM fooditem ORDER BY dateAdded DESC")
    List<FoodItem> getAll();

    @Query("SELECT * FROM fooditem WHERE dateAdded LIKE :todayDate || '%'")
    List<FoodItem> getTodayItems(String todayDate);
    @Delete
    void delete(FoodItem food);
}