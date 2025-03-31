package com.example.pocitanikalorii;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    private Context context;
    private List<Meal> mealList;

    public MealAdapter(Context context, List<Meal> mealList) {
        this.context = context;
        this.mealList = mealList;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = mealList.get(position);
        holder.tvFoodName.setText(meal.getFoodName());
        holder.tvCalories.setText(String.format("%.0f kcal", meal.getCalories()));
        holder.tvMacros.setText(String.format("B:%.1fg S:%.1fg T:%.1fg",
                meal.getProtein(), meal.getCarbs(), meal.getFat()));
        holder.tvMealType.setText(meal.getMealType());
        holder.tvTime.setText(meal.getTime());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MealDetailActivity.class);
            intent.putExtra("meal_id", meal.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvCalories, tvMacros, tvMealType, tvTime;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvMacros = itemView.findViewById(R.id.tvMacros);
            tvMealType = itemView.findViewById(R.id.tvMealType);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}