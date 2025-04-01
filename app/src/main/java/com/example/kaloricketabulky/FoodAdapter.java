package com.example.kaloricketabulky;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    private final List<FoodItem> foodList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvCalories;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvCalories = itemView.findViewById(R.id.tvCalories);
        }
    }

    public FoodAdapter(List<FoodItem> foodList) {
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FoodItem food = foodList.get(position);
        holder.tvName.setText(food.name);
        holder.tvCalories.setText(String.format("%.0f kcal", food.getCalories()));

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(v.getContext(), FoodDetailActivity.class);
            intent.putExtra("food_item", foodList.get(position));
            v.getContext().startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }
}