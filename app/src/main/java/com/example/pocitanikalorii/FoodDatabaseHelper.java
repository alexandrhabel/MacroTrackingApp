package com.example.pocitanikalorii;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FoodDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CalorieTracker.db";
    private static final int DATABASE_VERSION = 2; // Zvýšeno kvůli změně struktury

    private static final String TABLE_MEALS = "meals";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FOOD_NAME = "food_name";
    private static final String COLUMN_CALORIES = "calories";
    private static final String COLUMN_PROTEIN = "protein";
    private static final String COLUMN_CARBS = "carbs";
    private static final String COLUMN_FAT = "fat";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_MEAL_TYPE = "meal_type";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";

    public FoodDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MEALS_TABLE = "CREATE TABLE " + TABLE_MEALS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FOOD_NAME + " TEXT,"
                + COLUMN_CALORIES + " REAL,"
                + COLUMN_PROTEIN + " REAL,"
                + COLUMN_CARBS + " REAL,"
                + COLUMN_FAT + " REAL,"
                + COLUMN_WEIGHT + " REAL,"
                + COLUMN_MEAL_TYPE + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_TIME + " TEXT)";
        db.execSQL(CREATE_MEALS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEALS);
        onCreate(db);
    }

    // Přidání nového jídla s makroživinami
    public long addMeal(String foodName, double protein, double carbs, double fat,
                        double weight, String mealType) {
        double calories = (protein * 4) + (carbs * 4) + (fat * 9);

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOOD_NAME, foodName);
        values.put(COLUMN_CALORIES, calories);
        values.put(COLUMN_PROTEIN, protein);
        values.put(COLUMN_CARBS, carbs);
        values.put(COLUMN_FAT, fat);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_MEAL_TYPE, mealType);
        values.put(COLUMN_DATE, currentDate);
        values.put(COLUMN_TIME, currentTime);

        long result = db.insert(TABLE_MEALS, null, values);
        db.close(); // Přidáno uzavření spojení
        return result;
    }

    // Získání jídla podle ID
    public Meal getMealById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEALS,
                new String[]{COLUMN_ID, COLUMN_FOOD_NAME, COLUMN_CALORIES,
                        COLUMN_PROTEIN, COLUMN_CARBS, COLUMN_FAT,
                        COLUMN_WEIGHT, COLUMN_MEAL_TYPE, COLUMN_DATE, COLUMN_TIME},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Meal meal = new Meal(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getDouble(3),
                    cursor.getDouble(4),
                    cursor.getDouble(5),
                    cursor.getDouble(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9));
            cursor.close();
            return meal;
        }
        return null;
    }

    // Získání dnešních jídel
    public Cursor getTodayMeals() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(TABLE_MEALS,
                new String[]{COLUMN_ID, COLUMN_FOOD_NAME, COLUMN_CALORIES,
                        COLUMN_PROTEIN, COLUMN_CARBS, COLUMN_FAT,
                        COLUMN_WEIGHT, COLUMN_MEAL_TYPE, COLUMN_DATE, COLUMN_TIME},
                COLUMN_DATE + "=?",
                new String[]{today},
                null, null, COLUMN_TIME + " DESC");
    }

    // Smazání jídla
    public void deleteMeal(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEALS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Součet kalorií za dnešní den
    public double getTodayTotalCalories() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_CALORIES + ") FROM " + TABLE_MEALS +
                " WHERE " + COLUMN_DATE + "=?", new String[]{today});

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    // Získání součtu makroživin za den
    public double[] getTodayMacros() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SQLiteDatabase db = this.getReadableDatabase();
        double[] macros = new double[3];

        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COLUMN_PROTEIN + "), SUM(" + COLUMN_CARBS + "), SUM(" + COLUMN_FAT + ") " +
                        "FROM " + TABLE_MEALS + " WHERE " + COLUMN_DATE + "=?",
                new String[]{today}
        );

        if (cursor.moveToFirst()) {
            macros[0] = cursor.getDouble(0); // Protein
            macros[1] = cursor.getDouble(1); // Carbs
            macros[2] = cursor.getDouble(2); // Fat
        }
        cursor.close();
        db.close();
        return macros;
    }

    // Aktualizace existujícího jídla
    public int updateMeal(int id, String foodName, double proteinPer100g,
                          double carbsPer100g, double fatPer100g, double weight, String mealType) {
        double protein = (proteinPer100g * weight) / 100;
        double carbs = (carbsPer100g * weight) / 100;
        double fat = (fatPer100g * weight) / 100;
        double calories = (protein * 4) + (carbs * 4) + (fat * 9);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOOD_NAME, foodName);
        values.put(COLUMN_CALORIES, calories);
        values.put(COLUMN_PROTEIN, protein);
        values.put(COLUMN_CARBS, carbs);
        values.put(COLUMN_FAT, fat);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_MEAL_TYPE, mealType);

        int result = db.update(TABLE_MEALS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }
}